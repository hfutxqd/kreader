

#ifndef IMAGE_WRAPPER_H_
#define IMAGE_WRAPPER_H_


#include <unordered_map>
#include <png.h>

class ImageWrapper {

protected:
    int width;
    int height;
    int bpp;
    int channels;
    unsigned int colorType;

public:
    ImageWrapper();
    virtual ~ImageWrapper();

public:
    virtual bool loadImage(const std::string & path);
    virtual bool draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride);

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int getBitPerPixel() {
        return bpp;
    }

    int getChannels() {
        return channels;
    }

    unsigned int getColorType() {
        return colorType;
    }

};

class PNGWrapper : public ImageWrapper {

private:
    FILE * fp;
    png_structp pngPtr;
    png_infop infoPtr;
    std::string myPath;

public:
    PNGWrapper();
    virtual ~PNGWrapper();

public:
    virtual bool loadImage(const std::string & path);
    virtual bool draw(void *pixel, int x, int y, int width, int height, int bmpWidth, int bmpHeight, int stride);

private:
    void cleanup();
};

class ImageManager {

private:
    std::unordered_map<std::string, ImageWrapper *> imageTable;
    typedef std::unordered_map<std::string, ImageWrapper *>::iterator table_iterator;

public:
    ImageManager();
    ~ImageManager();

public:
    ImageWrapper * getImage(const std::string & path);
    bool releaseImage(const std::string & path);
    void clear();

};


#endif