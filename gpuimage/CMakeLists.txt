cmake_minimum_required(VERSION 3.4.1)

set(ly_source_files
  src/main/jni/libyuv/source/compare.cc
  src/main/jni/libyuv/source/compare_common.cc
  src/main/jni/libyuv/source/compare_neon.cc
  src/main/jni/libyuv/source/compare_neon64.cc
  src/main/jni/libyuv/source/compare_gcc.cc
  src/main/jni/libyuv/source/compare_win.cc
  src/main/jni/libyuv/source/convert.cc
  src/main/jni/libyuv/source/convert_argb.cc
  src/main/jni/libyuv/source/convert_from.cc
  src/main/jni/libyuv/source/convert_from_argb.cc
  src/main/jni/libyuv/source/convert_jpeg.cc
  src/main/jni/libyuv/source/convert_to_argb.cc
  src/main/jni/libyuv/source/convert_to_i420.cc
  src/main/jni/libyuv/source/cpu_id.cc
  src/main/jni/libyuv/source/mjpeg_decoder.cc
  src/main/jni/libyuv/source/mjpeg_validate.cc
  src/main/jni/libyuv/source/planar_functions.cc
  src/main/jni/libyuv/source/rotate.cc
  src/main/jni/libyuv/source/rotate_any.cc
  src/main/jni/libyuv/source/rotate_argb.cc
  src/main/jni/libyuv/source/rotate_common.cc
  src/main/jni/libyuv/source/rotate_mips.cc
  src/main/jni/libyuv/source/rotate_msa.cc
  src/main/jni/libyuv/source/rotate_neon.cc
  src/main/jni/libyuv/source/rotate_neon64.cc
  src/main/jni/libyuv/source/rotate_gcc.cc
  src/main/jni/libyuv/source/rotate_win.cc
  src/main/jni/libyuv/source/row_any.cc
  src/main/jni/libyuv/source/row_common.cc
  src/main/jni/libyuv/source/row_mips.cc
  src/main/jni/libyuv/source/row_msa.cc
  src/main/jni/libyuv/source/row_neon.cc
  src/main/jni/libyuv/source/row_neon64.cc
  src/main/jni/libyuv/source/row_gcc.cc
  src/main/jni/libyuv/source/row_win.cc
  src/main/jni/libyuv/source/scale.cc
  src/main/jni/libyuv/source/scale_any.cc
  src/main/jni/libyuv/source/scale_argb.cc
  src/main/jni/libyuv/source/scale_common.cc
  src/main/jni/libyuv/source/scale_mips.cc
  src/main/jni/libyuv/source/scale_msa.cc
  src/main/jni/libyuv/source/scale_neon.cc
  src/main/jni/libyuv/source/scale_neon64.cc
  src/main/jni/libyuv/source/scale_gcc.cc
  src/main/jni/libyuv/source/scale_win.cc
  src/main/jni/libyuv/source/video_common.cc
  src/main/jni/yuv-decoder.c
)

include_directories(src/main/jni/libyuv/include)

add_library( yuv-decoder SHARED ${ly_source_files} )

target_link_libraries( yuv-decoder )