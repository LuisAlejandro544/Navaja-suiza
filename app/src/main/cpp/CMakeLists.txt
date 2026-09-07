cmake_minimum_required(VERSION 3.22.1)

project("swissknife_native")

# Recopilar todos los archivos fuente en C de Lua 5.4 oficial
file(GLOB LUA_SOURCES "lua/*.c")

# Biblioteca C con Lua 100% original
add_library(
        lua_native
        STATIC
        ${LUA_SOURCES}
)

target_include_directories(
        lua_native
        PUBLIC
        ${CMAKE_CURRENT_SOURCE_DIR}/lua
)

# Biblioteca nativa C++ que integra Lua y JNI
add_library(
        swissknife_native
        SHARED
        native-lib.cpp
)

target_link_libraries(
        swissknife_native
        lua_native
        log
)
