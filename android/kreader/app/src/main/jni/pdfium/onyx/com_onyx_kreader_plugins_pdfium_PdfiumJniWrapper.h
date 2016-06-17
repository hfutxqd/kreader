/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper */

#ifndef _Included_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
#define _Included_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeInitLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeInitLibrary
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeDestroyLibrary
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeDestroyLibrary
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeOpenDocument
 * Signature: (ILjava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeOpenDocument
  (JNIEnv *, jobject, jint, jstring, jstring);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeCloseDocument
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeCloseDocument
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeMetadata
 * Signature: (ILjava/lang/String;[B)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeMetadata
  (JNIEnv *, jobject, jint, jstring, jbyteArray);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativePageCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageCount
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativePageSize
 * Signature: (II[F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativePageSize
  (JNIEnv *, jobject, jint, jint, jfloatArray);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeRenderPage
 * Signature: (IIIIIIILandroid/graphics/Bitmap;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeRenderPage
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeHitTest
 * Signature: (IIIIIIIIIIILcom/onyx/kreader/utils/ReaderTextSplitter;Lcom/onyx/kreader/plugins/pdfium/PdfiumSelection;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeHitTest
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jint, jint, jint, jint, jobject, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeSelection
 * Signature: (IIIIIIIIILcom/onyx/kreader/plugins/pdfium/PdfiumSelection;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeSelection
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jint, jint, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeSearchInPage
 * Signature: (IIIIIII[BZZLjava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeSearchInPage
  (JNIEnv *, jobject, jint, jint, jint, jint, jint, jint, jint, jbyteArray, jboolean, jboolean, jobject);

/*
 * Class:     com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper
 * Method:    nativeGetPageText
 * Signature: (II)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_onyx_kreader_plugins_pdfium_PdfiumJniWrapper_nativeGetPageText
  (JNIEnv *, jobject, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
