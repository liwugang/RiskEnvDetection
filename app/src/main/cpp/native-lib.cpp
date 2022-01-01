#include <jni.h>
#include <string>

#include <android/log.h>

#define LOG_TAG "RiskEnvDetection"
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)


#define RESULT_ERROR        -1
#define RESULT_NOT_FOUND    0
#define RESULT_LSPOSED      1
#define RESULT_EDXPOSED     2

std::string LSPOSED_KEY = "org.lsposed.lspd.core.Main";
std::string EDXPOSED_KEY = "com.elderdrivers.riru.edxp.core.Main";


int memstr(const unsigned char *src, size_t length, unsigned char *find_str, size_t str_len) {
    for (int i = 0; i < length - str_len; i++) {
        if (!memcmp(src + i, find_str, str_len)) {
            return 0;
        }
    }
    return 1;
}

extern "C" JNIEXPORT int JNICALL
Java_com_example_riskenvdetection_PosedDetection_detectAnonMem(
        JNIEnv* env,
        jobject /* this */,
        jlong start,
        jlong end) {

    long s = start;
    long e = end;

    long length = end - start;
    LOGD("size: %d", sizeof(long));
    LOGD("start: 0x%lx end: 0x%lx length: %d", s, e, length);
    unsigned char *buffer = (unsigned char *)calloc(length, 1);
    if (!buffer) {
        LOGD("out of memory");
        return RESULT_ERROR;
    }

    int result = RESULT_NOT_FOUND;

    memcpy((void *)buffer, (void *)s, length);

    if (!memstr(buffer, (size_t)length, (unsigned char *) LSPOSED_KEY.c_str(), strlen(LSPOSED_KEY.c_str()))) {
        LOGD("Found lsposed");
        result = RESULT_LSPOSED;
        goto exit;
    }

    if (!memstr(buffer, (size_t)length, (unsigned char *) EDXPOSED_KEY.c_str(), strlen(EDXPOSED_KEY.c_str()))) {
        LOGD("Found edxposed");
        result = RESULT_EDXPOSED;
        goto exit;
    }

exit:
    free(buffer);
    return result;
}