/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_onyx_kreader_utils_ReaderImageUtils */

#ifndef _Included_com_onyx_kreader_utils_ReaderImageUtils
#define _Included_com_onyx_kreader_utils_ReaderImageUtils
#ifdef __cplusplus
extern "C" {
#endif
#undef com_onyx_kreader_utils_ReaderImageUtils_NO_GAMMA
#define com_onyx_kreader_utils_ReaderImageUtils_NO_GAMMA -1.0f
#undef com_onyx_kreader_utils_ReaderImageUtils_STANDARD_GAMMA
#define com_onyx_kreader_utils_ReaderImageUtils_STANDARD_GAMMA 150.0f
#undef com_onyx_kreader_utils_ReaderImageUtils_MAX_GAMMA
#define com_onyx_kreader_utils_ReaderImageUtils_MAX_GAMMA 200.0f
/*
 * Class:     com_onyx_kreader_utils_ReaderImageUtils
 * Method:    crop
 * Signature: (Landroid/graphics/Bitmap;IIIID)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_com_onyx_kreader_utils_ReaderImageUtils_crop
  (JNIEnv *, jclass, jobject, jint, jint, jint, jint, jdouble);

/*
 * Class:     com_onyx_kreader_utils_ReaderImageUtils
 * Method:    reflowPage
 * Signature: (Landroid/graphics/Bitmap;Ljava/lang/String;Lcom/onyx/kreader/reflow/ReaderScannedPageReflowManager;Lcom/onyx/kreader/reflow/ReaderScannedPageReflowSettings;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_utils_ReaderImageUtils_reflowPage
  (JNIEnv *, jclass, jobject, jstring, jobject, jobject);

/*
 * Class:     com_onyx_kreader_utils_ReaderImageUtils
 * Method:    emboldenInPlace
 * Signature: (Landroid/graphics/Bitmap;I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_utils_ReaderImageUtils_emboldenInPlace
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_onyx_kreader_utils_ReaderImageUtils
 * Method:    gammaCorrection
 * Signature: (Landroid/graphics/Bitmap;F)Z
 */
JNIEXPORT jboolean JNICALL Java_com_onyx_kreader_utils_ReaderImageUtils_gammaCorrection
  (JNIEnv *, jclass, jobject, jfloat);

#ifdef __cplusplus
}
#endif
#endif
