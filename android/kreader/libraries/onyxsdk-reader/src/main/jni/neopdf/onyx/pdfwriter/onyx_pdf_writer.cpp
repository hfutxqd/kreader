#include "onyx_pdf_writer.h"

#include <fstream>
#include <vector>

#include "podofo/podofo.h"

#include "page_annotation.h"
#include "page_scribble.h"

namespace {

PoDoFo::PdfColor colorFromRgb(uint32_t rgb) {
    double r = (rgb >> 16 & 0xFF) / static_cast<double>(255);
    double g = (rgb >> 8 & 0xFF) / static_cast<double>(255);
    double b = (rgb & 0xFF) / static_cast<double>(255);
    return PoDoFo::PdfColor(r, g, b);
}

/**
 * @brief getPageCropBox
 * @param page
 * @return rect in PDF's user space coordinate system
 */
RectF getPageCropBox(PoDoFo::PdfPage *page) {
    const PoDoFo::PdfRect cropBox = page->GetCropBox();
    return RectF(static_cast<float>(cropBox.GetLeft()),
                 static_cast<float>(cropBox.GetBottom() + cropBox.GetHeight()),
                 static_cast<float>(cropBox.GetLeft() + cropBox.GetWidth()),
                 static_cast<float>(cropBox.GetBottom()));
}

bool translateFromDeviceToPage(const RectF &cropBox, PointF *point) {
    if (point->y > cropBox.height()) {
        return false;
    }
    point->x += cropBox.left;
    point->y = cropBox.bottom + (cropBox.height() - point->y);
    return true;
}

bool translateFromDeviceToPage(const RectF &cropBox, RectF *rect) {
    PointF leftTop(rect->left, rect->top);
    PointF rightBottom(rect->right, rect->bottom);
    if (!translateFromDeviceToPage(cropBox, &leftTop)) {
        return false;
    }
    if (!translateFromDeviceToPage(cropBox, &rightBottom)) {
        return false;
    }
    rect->set(leftTop, rightBottom);
    return true;
}

bool initAnnotationAndPainter(PoDoFo::PdfDocument *document,
                              PoDoFo::PdfPage *pdfPage,
                              const RectF &cropBox,
                              const PoDoFo::EPdfAnnotation annotationType,
                              const RectF &rect,
                              uint_t color,
                              float strokeThickness,
                              PoDoFo::PdfAnnotation **annotation,
                              PoDoFo::PdfRect *annotRect,
                              PoDoFo::PdfXObject **xobj,
                              PoDoFo::PdfPainter *painter) {
    using namespace PoDoFo;

    RectF bounds = rect;
    if (!translateFromDeviceToPage(cropBox, &bounds)) {
        return false;
    }

    // use larger thickness to compute annotation bounds
    float largerThickness = strokeThickness * 2;
    *annotRect = PdfRect(static_cast<int>(bounds.left - largerThickness),
                    static_cast<int>(bounds.bottom - largerThickness),
                    static_cast<int>(bounds.width() + largerThickness * 2),
                    static_cast<int>(bounds.height()) + largerThickness * 2);

    *annotation = pdfPage->CreateAnnotation(annotationType, *annotRect);
    const PdfColor pdfColor(colorFromRgb(color));
    (*annotation)->SetColor(pdfColor.GetRed(), pdfColor.GetGreen(), pdfColor.GetBlue());

    *xobj = new PdfXObject(*annotRect, document);
    painter->SetPage(*xobj);
    painter->SetStrokeWidth(strokeThickness);
    painter->SetStrokingColor(pdfColor);
    painter->SetLineCapStyle(ePdfLineCapStyle_Round);
    return true;
}

bool createAnnotationHightlight(PoDoFo::PdfDocument *document,
                                PoDoFo::PdfPage *page,
                                const PageAnnotation &annotation) {
    using namespace PoDoFo;

    std::vector<RectF> rectList = annotation.rects;
    if (rectList.empty()) {
        return false;
    }

    RectF cropBox = getPageCropBox(page);

    RectF boundingRect(PointF(rectList.at(0).left, rectList.at(0).top),
                     PointF(rectList.at(0).right, rectList.at(0).bottom));
    for (auto &rect : rectList) {
        boundingRect.unite(RectF(rect.left, rect.top, rect.right, rect.bottom));
    }
    if (!translateFromDeviceToPage(cropBox, &boundingRect)) {
        return false;
    }

    PdfRect pdfBoundingRect(static_cast<int>(boundingRect.left),
                            static_cast<int>(boundingRect.bottom),
                            static_cast<int>(boundingRect.width()),
                            static_cast<int>(boundingRect.height()));
    PdfAnnotation *highlight = page->CreateAnnotation(ePdfAnnotation_Highlight, pdfBoundingRect);
    if (!highlight) {
        return false;
    }

    if (annotation.note.size() > 0) {
        highlight->SetContents(PdfString(reinterpret_cast<const pdf_utf8*>(annotation.note.c_str())));
    }

    // default (1.0, 1.0, 0.0) is very obscure on device,
    // so we choose darker color instead
    const PdfColor color(0.9, 0.9, 0.0);
    highlight->SetColor(color.GetRed(), color.GetGreen(), color.GetBlue());

    PdfXObject *xobj = new PdfXObject(pdfBoundingRect, document);
    PdfPainter pnt;
    pnt.SetPage(xobj);
    PdfExtGState *gstate = new PdfExtGState((document));
    gstate->SetFillOpacity(1.0);
    gstate->SetBlendMode("Multiply");
    pnt.SetExtGState(gstate);
    pnt.SetColor(color);

    PdfArray quads;
    for (auto r : rectList) {
        if (!translateFromDeviceToPage(cropBox, &r)) {
            return false;
        }
        quads.push_back(static_cast<double>(r.left));
        quads.push_back(static_cast<double>(r.top));
        quads.push_back(static_cast<double>(r.right));
        quads.push_back(static_cast<double>(r.top));
        quads.push_back(static_cast<double>(r.left));
        quads.push_back(static_cast<double>(r.bottom));
        quads.push_back(static_cast<double>(r.right));
        quads.push_back(static_cast<double>(r.bottom));

        pnt.Rectangle(r.left, r.bottom, r.width(), r.height());
        pnt.Fill();
    }

    highlight->SetQuadPoints(quads);

    pnt.FinishPage();
    highlight->SetAppearanceStream(xobj);

    return true;
}

}

class OnyxPdfWriter::Impl {
public:
    Impl()
        : doc_(nullptr) {
    }

    ~Impl() {
        if (doc_) {
            delete doc_;
            doc_ = nullptr;
        }
        for (auto entry : subPages_) {
            delete entry.second;
        }
    }

    PoDoFo::PdfPage *getPage(int page, int subPage, bool create) {
        if (subPage == 0) {
            return getPage(page);
        }
        return getSubPage(page, subPage, create);
    }

    PoDoFo::PdfPage *getPage(int page) {
        PoDoFo::PdfPage *pdfPage = doc_->GetPage(page);
        if (!pdfPage) {
            return nullptr;
        }
        pagesWithAnnotation_.insert(page);
        return pdfPage;
    }

    PoDoFo::PdfPage *getSubPage(int page, int subPage, bool create) {
        PoDoFo::PdfPage *pdfPage = doc_->GetPage(page);
        if (!pdfPage) {
            return nullptr;
        }
        auto pageList = subPages_[page];
        if (pageList) {
            for (auto it = pageList->cbegin(); it != pageList->cend(); ++it) {
                if (it->first == subPage) {
                    return it->second;
                }
            }
        }

        if (!create) {
            return nullptr;
        }

        PoDoFo::PdfPage *subPdfPage = new PoDoFo::PdfPage(pdfPage->GetMediaBox(), doc_);
        if (!subPages_[page]) {
            subPages_[page] = new std::vector<std::pair<int, PoDoFo::PdfPage*>>();
        }
        subPages_[page]->push_back(std::pair<int, PoDoFo::PdfPage*>(subPage, subPdfPage));

        return subPdfPage;
    }

    void insertSubPages() {
        std::vector<int> pagesWithSubPage;
        for (auto entry : subPages_) {
            pagesWithSubPage.push_back(entry.first);
        }
        std::sort(pagesWithSubPage.begin(), pagesWithSubPage.end(), std::greater<int>());
        for (auto page : pagesWithSubPage) {
            insertSubPages(doc_, page, subPages_[page]);
        }
    }

    void insertSubPages(int srcPage, PoDoFo::PdfDocument *dstDoc, int nAtIndex) {
        auto subPageList = subPages_[srcPage];
        if (!subPageList) {
            return;
        }
        insertSubPages(dstDoc, nAtIndex, subPageList);
    }

    void insertSubPages(PoDoFo::PdfDocument *doc, int pageIndex, std::vector<std::pair<int, PoDoFo::PdfPage*>> *subPages) {
        for (size_t i = 0; i < subPages->size(); i++) {
            doc->GetPagesTree()->InsertPage(pageIndex + static_cast<int>(i),
                                            subPages->at(i).second);
        };
    }

public:
    std::string docPath_;
    PoDoFo::PdfMemDocument *doc_;
    std::set<int, std::less<int>> pagesWithAnnotation_;
    std::map<int, std::vector<std::pair<int, PoDoFo::PdfPage*>>*> subPages_;
};

OnyxPdfWriter::OnyxPdfWriter()
{
}

OnyxPdfWriter::~OnyxPdfWriter()
{
}

bool OnyxPdfWriter::openPDF(const std::string &path)
{
    if (isOpened()) {
        close();
    }

    impl.reset(new Impl());

    impl->doc_ = new PoDoFo::PdfMemDocument(path.c_str());
    if (!impl->doc_->GetInfo()) {
        delete impl->doc_;
        impl->doc_ = nullptr;
        return false;
    }

    impl->docPath_ = path;
    return true;
}

bool OnyxPdfWriter::saveAs(const std::string &path, bool saveOnlyPagesWithAnnotation)
{
    if (!isOpened()) {
        return false;
    }

    impl->insertSubPages();

    if (!saveOnlyPagesWithAnnotation) {
        impl->doc_->Write(path.c_str());
        return true;
    }

    std::set<int, std::less<int>> pageSet;
    for (const auto page : impl->pagesWithAnnotation_) {
        pageSet.insert(page);
    }
    for (const auto &entry : impl->subPages_) {
        pageSet.insert(entry.first);
    }

    if (pageSet.size() <= 0) {
        return true;
    }

    int subPageCount = 0;

    std::vector<int> pages(pageSet.cbegin(), pageSet.cend());
    std::vector<std::pair<int, int>> segments; // pages to save, in [a, b] form
    for (size_t i = 0; i < pages.size(); i++) {
        int realPage = pages.at(i) + subPageCount;
        if (!impl->subPages_[pages.at(i)]) {
            segments.push_back(std::make_pair(realPage, realPage));
        } else {
            int count = static_cast<int>(impl->subPages_[pages.at(i)]->size());
            subPageCount += count;

            if (impl->pagesWithAnnotation_.find(pages.at(i)) == impl->pagesWithAnnotation_.end()) {
                // skip doc page
                realPage += 1;
                count -= 1;
            }
            segments.push_back(std::make_pair(realPage, realPage + count));
        }
    }

    for (int i = static_cast<int>(segments.size()) - 1; i >= 0; i--) {
        std::pair<int, int> &pair = segments.at(static_cast<size_t>(i));
        if (i == static_cast<int>(segments.size()) - 1) {
            impl->doc_->DeletePages(pair.second + 1, impl->doc_->GetPageCount() - pair.second - 1);
        } else {
            std::pair<int, int> &nextPair = segments.at(static_cast<size_t>(i + 1));
            impl->doc_->DeletePages(pair.second + 1, nextPair.first - pair.second - 1);
        }
    }

    std::pair<int, int> &pair = segments.at(0);
    if (pair.first > 0) {
        impl->doc_->DeletePages(0, pair.first);
    }

    impl->doc_->Write(path.c_str());
    return true;
}

void OnyxPdfWriter::close()
{
    if (!isOpened()) {
        return;
    }

    impl.reset(nullptr);
}

bool OnyxPdfWriter::isOpened() const
{
    return impl.get() && impl->doc_ && impl->doc_->IsLoaded();
}

bool OnyxPdfWriter::writeLine(const int page, const int subPage, const RectF &rect, const uint32_t color, const float strokeThickness, const PointF &start, const PointF &end)
{
    using namespace PoDoFo;

    if (!isOpened()) {
        return false;
    }

    PdfPage *pdfPage = impl->getPage(page, subPage, true);
    if (!pdfPage) {
        return false;
    }
    RectF cropBox = getPageCropBox(pdfPage);

    PdfAnnotation *line = nullptr;
    PdfRect annotRect;
    PdfXObject *xobj = nullptr;
    PdfPainter pnt;
    if (!initAnnotationAndPainter(impl->doc_, pdfPage, cropBox,
                                  ePdfAnnotation_Line, rect, color, strokeThickness,
                                  &line, &annotRect, &xobj, &pnt)) {
        return false;
    }

    PointF p1 = start;
    PointF p2 = end;
    if (!translateFromDeviceToPage(cropBox, &p1) ||
            !translateFromDeviceToPage(cropBox, &p2)) {
        return false;
    }
    pnt.DrawLine(p1.x, p1.y, p2.x, p2.y);

    pnt.FinishPage();
    line->SetAppearanceStream(xobj);
    return true;
}

bool OnyxPdfWriter::writePolyLine(const int page, const int subPage, const RectF &rect, const uint32_t color, const float strokeThickness, const std::vector<PointF> &points)
{
    using namespace PoDoFo;

    if (!isOpened()) {
        return false;
    }

    PdfPage *pdfPage = impl->getPage(page, subPage, true);
    if (!pdfPage) {
        return false;
    }
    const RectF cropBox = getPageCropBox(pdfPage);

    PdfAnnotation *polyline = nullptr;
    PdfRect annotRect;
    PdfXObject *xobj = nullptr;
    PdfPainter pnt;
    if (!initAnnotationAndPainter(impl->doc_, pdfPage, cropBox,
                                  ePdfAnnotation_PolyLine, rect, color, strokeThickness,
                                  &polyline, &annotRect, &xobj, &pnt)) {
        return false;
    }

    PdfArray vertices;
    for (auto point : points) {
        if (!translateFromDeviceToPage(cropBox, &point)) {
            return false;
        }
        vertices.push_back(point.x);
        vertices.push_back(point.y);
    }
    polyline->GetObject()->GetDictionary().AddKey(PdfName("Vertices"), vertices);

    for (size_t i = 0; i < points.size() - 1; i++) {
        PointF p1 = points[i];
        PointF p2 = points[i + 1];
        if (!translateFromDeviceToPage(cropBox, &p1) ||
                !translateFromDeviceToPage(cropBox, &p2)) {
            return false;
        }
        pnt.DrawLine(p1.x, p1.y, p2.x, p2.y);
    }

    pnt.FinishPage();
    polyline->SetAppearanceStream(xobj);
    return true;
}

bool OnyxPdfWriter::writePolygon(const int page, const int subPage, const RectF &rect, const uint32_t color, const float strokeThickness, const std::vector<PointF> &points)
{
    using namespace PoDoFo;

    if (!isOpened()) {
        return false;
    }

    PdfPage *pdfPage = impl->getPage(page, subPage, true);
    if (!pdfPage) {
        return false;
    }
    const RectF cropBox = getPageCropBox(pdfPage);

    PdfAnnotation *polygon = nullptr;
    PdfRect annotRect;
    PdfXObject *xobj = nullptr;
    PdfPainter pnt;
    if (!initAnnotationAndPainter(impl->doc_, pdfPage, cropBox,
                                  ePdfAnnotation_Polygon, rect, color, strokeThickness,
                                  &polygon, &annotRect, &xobj, &pnt)) {
        return false;
    }

    PdfArray vertices;
    for (auto point : points) {
        if (!translateFromDeviceToPage(cropBox, &point)) {
            return false;
        }
        vertices.push_back(point.x);
        vertices.push_back(point.y);
    }
    polygon->GetObject()->GetDictionary().AddKey(PdfName("Vertices"), vertices);

    for (size_t i = 0; i < points.size() - 1; i++) {
        PointF p1 = points[i];
        PointF p2 = points[i + 1];
        if (!translateFromDeviceToPage(cropBox, &p1) ||
                !translateFromDeviceToPage(cropBox, &p2)) {
            return false;
        }
        pnt.DrawLine(p1.x, p1.y, p2.x, p2.y);
    }
    if (points.size() > 2) {
        PointF start = points[0];
        PointF end = points[points.size() - 1];
        if (!translateFromDeviceToPage(cropBox, &start) ||
                !translateFromDeviceToPage(cropBox, &end)) {
            return false;
        }
        pnt.DrawLine(end.x, end.y, start.x, start.y);
    }

    pnt.FinishPage();
    polygon->SetAppearanceStream(xobj);
    return true;
}

bool OnyxPdfWriter::writeSquare(const int page, const int subPage, const RectF &rect, const uint32_t color, const float strokeThickness)
{
    using namespace PoDoFo;

    if (!isOpened()) {
        return false;
    }

    PdfPage *pdfPage = impl->getPage(page, subPage, true);
    if (!pdfPage) {
        return false;
    }
    RectF cropBox = getPageCropBox(pdfPage);

    PdfAnnotation *square = nullptr;
    PdfRect annotRect;
    PdfXObject *xobj = nullptr;
    PdfPainter pnt;
    if (!initAnnotationAndPainter(impl->doc_, pdfPage, cropBox,
                                  ePdfAnnotation_Square, rect, color, strokeThickness,
                                  &square, &annotRect, &xobj, &pnt)) {
        return false;
    }

    pnt.Rectangle(annotRect);
    pnt.Stroke();

    pnt.FinishPage();
    square->SetAppearanceStream(xobj);
    return true;
}

bool OnyxPdfWriter::writeCircle(const int page, const int subPage, const RectF &rect, const uint32_t color, const float strokeThickness)
{
    using namespace PoDoFo;

    if (!isOpened()) {
        return false;
    }

    PdfPage *pdfPage = impl->getPage(page, subPage, true);
    if (!pdfPage) {
        return false;
    }
    RectF cropBox = getPageCropBox(pdfPage);

    PdfAnnotation *square = nullptr;
    PdfRect annotRect;
    PdfXObject *xobj = nullptr;
    PdfPainter pnt;
    if (!initAnnotationAndPainter(impl->doc_, pdfPage, cropBox,
                                  ePdfAnnotation_Square, rect, color, strokeThickness,
                                  &square, &annotRect, &xobj, &pnt)) {
        return false;
    }

    pnt.Ellipse(annotRect.GetLeft(), annotRect.GetBottom(), annotRect.GetWidth(), annotRect.GetHeight());
    pnt.Stroke();

    pnt.FinishPage();
    square->SetAppearanceStream(xobj);
    return true;
}

bool OnyxPdfWriter::writeAnnotation(const PageAnnotation &annotation)
{
    if (!isOpened()) {
        return false;
    }

    PoDoFo::PdfPage *page = impl->getPage(annotation.page);
    if (!page) {
        return false;
    }
    return createAnnotationHightlight(impl->doc_, page, annotation);
}

bool OnyxPdfWriter::setDocumentTitle(const std::string &path, const std::string &title)
{
    // PoDoFo can't modify doc in place, so we read in the contents, modify it in memory, then write back
    std::ifstream in(path, std::ios::in | std::ios::binary);
    if (!in) {
        return false;
    }

    std::vector<char> contents;
    in.seekg(0, std::ios::end);
    contents.resize(in.tellg());
    in.seekg(0, std::ios::beg);
    in.read(&contents[0], contents.size());
    in.close();

    PoDoFo::PdfMemDocument doc;
    doc.Load(contents.data(), contents.size());
    doc.GetInfo()->SetTitle(title.c_str());
    doc.Write(path.c_str());

    return true;
}
