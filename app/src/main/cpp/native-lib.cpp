#include <jni.h>
#include <string>

// Lua 100% original en C
extern "C" {
#include "lua/lua.h"
#include "lua/lauxlib.h"
#include "lua/lualib.h"
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativebridge_NativeEngine_getCppVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string version = "C++20 (Clang NDK)";
    return env->NewStringUTF(version.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativebridge_NativeEngine_getLuaVersion(
        JNIEnv* env,
        jobject /* this */) {
    lua_State *L = luaL_newstate();
    std::string luaVer;
    if (L != nullptr) {
        luaL_openlibs(L);
        luaVer = LUA_RELEASE;
        lua_close(L);
    } else {
        luaVer = "Lua (Init Failed)";
    }
    return env->NewStringUTF(luaVer.c_str());
}
