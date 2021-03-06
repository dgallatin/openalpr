#include <alpr.h>
 
#include "com_openalpr_jni_Alpr.h"

using namespace alpr;

JNIEXPORT jlong JNICALL Java_com_openalpr_jni_Alpr_initialize
  (JNIEnv *env, jobject thisObj, jstring jcountry, jstring jconfigFile, jstring jruntimeDir)
  {
    // Convert strings from java to C++ and release resources
    const char *ccountry = env->GetStringUTFChars(jcountry, NULL);
    std::string country(ccountry);
    env->ReleaseStringUTFChars(jcountry, ccountry);

    const char *cconfigFile = env->GetStringUTFChars(jconfigFile, NULL);
    std::string configFile(cconfigFile);
    env->ReleaseStringUTFChars(jconfigFile, cconfigFile);

    const char *cruntimeDir = env->GetStringUTFChars(jruntimeDir, NULL);
    std::string runtimeDir(cruntimeDir);
    env->ReleaseStringUTFChars(jruntimeDir, cruntimeDir);

    Alpr* nativeAlpr = new alpr::Alpr(country, configFile, runtimeDir);

    return (long) nativeAlpr;
  }

JNIEXPORT void JNICALL Java_com_openalpr_jni_Alpr_dispose
  (JNIEnv *env, jobject thisObj, jlong alprPtr)
  {
    delete reinterpret_cast<Alpr*>(alprPtr);
  }

JNIEXPORT jstring JNICALL Java_com_openalpr_jni_Alpr_native_1recognize__JLjava_lang_String_2
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jstring jimageFile)
  {
    // Convert strings from java to C++ and release resources
    const char *cimageFile = env->GetStringUTFChars(jimageFile, NULL);
    std::string imageFile(cimageFile);
    env->ReleaseStringUTFChars(jimageFile, cimageFile);

    AlprResults results = reinterpret_cast<Alpr*>(alprPtr)->recognize(imageFile);

    std::string json = Alpr::toJson(results);

    return env->NewStringUTF(json.c_str());
  }

JNIEXPORT jstring JNICALL Java_com_openalpr_jni_Alpr_native_1recognize__J_3B
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jbyteArray jimageBytes)
  {
    //printf("Recognize byte array");

    int len = env->GetArrayLength (jimageBytes);
    unsigned char* buf = new unsigned char[len];
    env->GetByteArrayRegion (jimageBytes, 0, len, reinterpret_cast<jbyte*>(buf));

    std::vector<char> cvec(buf, buf+len);

    AlprResults results = reinterpret_cast<Alpr*>(alprPtr)->recognize(cvec);
    std::string json = Alpr::toJson(results);

    delete buf;
    return env->NewStringUTF(json.c_str());
  }

JNIEXPORT jstring JNICALL Java_com_openalpr_jni_Alpr_native_1recognize__JJIII
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jlong data, jint bytesPerPixel, jint width, jint height)
  {
    AlprResults results = reinterpret_cast<Alpr*>(alprPtr)->recognize(
            reinterpret_cast<unsigned char*>(data),
            static_cast<int>(bytesPerPixel),
            static_cast<int>(width),
            static_cast<int>(height),
            std::vector<AlprRegionOfInterest>());

    std::string json = Alpr::toJson(results);

    return env->NewStringUTF(json.c_str());
  }


JNIEXPORT void JNICALL Java_com_openalpr_jni_Alpr_set_1default_1region
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jstring jdefault_region)
  {
    // Convert strings from java to C++ and release resources
    const char *cdefault_region = env->GetStringUTFChars(jdefault_region, NULL);
    std::string default_region(cdefault_region);
    env->ReleaseStringUTFChars(jdefault_region, cdefault_region);
    
    reinterpret_cast<Alpr*>(alprPtr)->setDefaultRegion(default_region);
  }

JNIEXPORT void JNICALL Java_com_openalpr_jni_Alpr_detect_1region
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jboolean detect_region)
  {
	reinterpret_cast<Alpr*>(alprPtr)->setDetectRegion(detect_region);
  }

JNIEXPORT void JNICALL Java_com_openalpr_jni_Alpr_set_1top_1n
  (JNIEnv *env, jobject thisObj, jlong alprPtr, jint top_n)
  {
	reinterpret_cast<Alpr*>(alprPtr)->setTopN(top_n);
  }

JNIEXPORT jstring JNICALL Java_com_openalpr_jni_Alpr_get_1version
  (JNIEnv *env, jobject thisObj, jlong alprPtr)
  {
    std::string version = reinterpret_cast<Alpr*>(alprPtr)->getVersion();

    return env->NewStringUTF(version.c_str());
  }
