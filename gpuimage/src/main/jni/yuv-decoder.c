#include <jni.h>
#include "libyuv/include/libyuv.h"

JNIEXPORT void JNICALL Java_cn_m15_gpuimage_GPUImageNativeLibrary_YUVtoARGB(JNIEnv * env, jobject obj, jbyteArray yuv420sp, jint width, jint height, jintArray rgbOut)
{
    uint8_t *rgbData = (uint8_t *)((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    const uint8* src_y = yuv;
    int src_stride_y = width;
    const uint8* src_vu = src_y + width * height;
    int src_stride_vu = (width + 1) / 2 * 2;;
    int dst_stride_argb = width * 4;

    NV12ToARGB(src_y, src_stride_y,
               src_vu, src_stride_vu,
               rgbData, dst_stride_argb,
               width, height); //不知道为什么用NV21toARGB不行，有空再研究

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}