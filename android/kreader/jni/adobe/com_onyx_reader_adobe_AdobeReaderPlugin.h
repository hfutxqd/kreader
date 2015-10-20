/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_reader_adobe_AdobeReaderPlugin */

#ifndef _Included_com_onyx_reader_adobe_AdobeReaderPlugin
#define _Included_com_onyx_reader_adobe_AdobeReaderPlugin
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    openFile
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_openFile
  (JNIEnv *, jobject, jstring, jstring, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    closeFile
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_closeFile
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    countPagesInternal
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_countPagesInternal
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    gotoLocationInternal
 * Signature: (ILjava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_gotoLocationInternal
  (JNIEnv *, jobject, jint, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    pageSizeNative
 * Signature: (I[F)V
 */
JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_pageSizeNative
  (JNIEnv *, jobject, jint, jfloatArray);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageWidth
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageWidth
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageHeight
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageHeight
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    drawPage
 * Signature: (ILandroid/graphics/Bitmap;IIIIIIDZ)J
 */
JNIEXPORT jlong JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_drawPage
  (JNIEnv *, jobject, jint, jobject, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    drawVisiblePages
 * Signature: (Landroid/graphics/Bitmap;IIIIIIDZ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_drawVisiblePages
  (JNIEnv *, jobject, jobject, jint, jint, jint, jint, jboolean);

JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_clear
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageNumberOfScreenPoint
 * Signature: (DD)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageNumberOfScreenPoint
  (JNIEnv *, jobject, jdouble, jdouble);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    convertPointFromDeviceSpaceToDocumentSpace
 * Signature: (DDI)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_convertPointFromDeviceSpaceToDocumentSpace
  (JNIEnv *, jobject, jdouble, jdouble, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    convertPointFromDocumentSpaceToDeviceSpace
 * Signature: (DDI)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_convertPointFromDocumentSpaceToDeviceSpace
  (JNIEnv *, jobject, jdouble, jdouble, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setPageMode
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setPageMode
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getTextNative
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getTextNative
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageTextNative
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageTextNative
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getMetadataNative
 * Signature: (Lcom/onyx/reader/ReaderDocumentMetadata;Ljava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getMetadataNative
  (JNIEnv *, jobject, jobject, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    nextScreenNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_nextScreenNative
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    prevScreenNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_prevScreenNative
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setFontSizeNative
 * Signature: (DDD)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setFontSizeNative
  (JNIEnv *, jobject, jdouble, jdouble, jdouble);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    collectVisibleLinksNative
 * Signature: (Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_collectVisibleLinksNative
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageNumberByLocationNative
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageNumberByLocationNative
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setAbortFlagNative
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setAbortFlagNative
  (JNIEnv *, jobject, jboolean);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getAbortFlagNative
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getAbortFlagNative
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    hitTestNative
 * Signature: (FFILcom/onyx/reader/utils/ReaderTextSplitter;)Lcom/onyx/reader/ReaderSelection;
 */
JNIEXPORT jobject JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_hitTestNative
  (JNIEnv *, jobject, jfloat, jfloat, jint, jobject);


JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_locationNative
  (JNIEnv *, jobject, jfloat, jfloat);


/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    rectangles
 * Signature: (Ljava/lang/String;Ljava/lang/String;)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_rectangles
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    pageDisplayRectangles
 * Signature: (II)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_pageDisplayRectangles
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    allVisiblePagesRectangle
 * Signature: (Ljava/util/List;)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_allVisiblePagesRectangle
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    updateLocationNative
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_updateLocationNative
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    changeNavigationMatrix
 * Signature: (DDD)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_changeNavigationMatrix
  (JNIEnv *, jobject, jdouble, jdouble, jdouble);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setNavigationMatrix
 * Signature: (DDD)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setNavigationMatrix
  (JNIEnv *, jobject, jdouble, jdouble, jdouble);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    searchNextNative
 * Signature: (Ljava/lang/String;ZZIILjava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_searchNextNative
  (JNIEnv *, jobject, jstring, jboolean, jboolean, jint, jint, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    searchPrevNative
 * Signature: (Ljava/lang/String;ZZIILjava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_searchPrevNative
  (JNIEnv *, jobject, jstring, jboolean, jboolean, jint, jint, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    searchAllInPageNative
 * Signature: (Ljava/lang/String;ZZIILjava/util/List;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_searchAllInPageNative
  (JNIEnv *, jobject, jstring, jboolean, jboolean, jint, jint, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getPageOrientationNative
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getPageOrientationNative
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    hasTableOfContent
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_hasTableOfContent
  (JNIEnv *, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getTableOfContent
 * Signature: (Lcom/onyx/reader/ReaderTableOfContentEntry;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getTableOfContent
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    isLocationInCurrentScreenNative
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_isLocationInCurrentScreenNative
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setFontFaceInternal
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setFontFaceInternal
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    setDisplayMarginsNative
 * Signature: (DDDD)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_setDisplayMarginsNative
  (JNIEnv *, jobject, jdouble, jdouble, jdouble, jdouble);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    initDeviceForDRM
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_initDeviceForDRM
  (JNIEnv *, jclass, jstring, jstring, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    registerAdobeDRMCallback
 * Signature: (Lcom/onyx/reader/ReaderDRMCallback;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_registerAdobeDRMCallback
  (JNIEnv *, jclass, jobject);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    fulfillByAcsm
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_fulfillByAcsm
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    activateDevice
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_activateDevice
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getActivatedDRMAccount
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getActivatedDRMAccount
  (JNIEnv *, jclass);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    deactivateDevice
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_deactivateDevice
  (JNIEnv *, jclass);

/*
 * Class:     com_onyx_reader_adobe_AdobeReaderPlugin
 * Method:    getNextSentence
 * Signature: (Lcom/onyx/reader/utils/ReaderTextSplitter;Ljava/lang/String;)Lcom/onyx/reader/ReaderSentenceResult;
 */
JNIEXPORT jobject JNICALL Java_com_onyx_reader_plugins_adobe_AdobeJniWrapper_getNextSentence
  (JNIEnv *, jclass, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
