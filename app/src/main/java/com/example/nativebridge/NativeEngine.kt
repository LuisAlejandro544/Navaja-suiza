package com.example.nativebridge

object NativeEngine {
    init {
        try {
            System.loadLibrary("swiss_knife_core")
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
        try {
            System.loadLibrary("swissknife_native")
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    external fun getCppVersion(): String
    external fun getLuaVersion(): String
    external fun getRustVersion(): String
}
