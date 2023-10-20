cmake_minimum_required(VERSION 3.10.2)

set(PaddleLite_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../PaddleLite")
include_directories(${PaddleLite_DIR}/cxx/include)

set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../OpenCV/sdk/native/jni")
find_package(OpenCV REQUIRED)
include_directories(${OpenCV_INCLUDE_DIRS})

aux_source_directory(. SOURCES)

set(CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS} -ffast-math -Ofast -Os"
        )
set(CMAKE_CXX_FLAGS
        "${CMAKE_CXX_FLAGS} -fvisibility=hidden -fvisibility-inlines-hidden -fdata-sections -ffunction-sections"
        )
set(CMAKE_SHARED_LINKER_FLAGS
        "${CMAKE_SHARED_LINKER_FLAGS} -Wl,--gc-sections -Wl,-z,nocopyreloc")

add_library(native SHARED ${SOURCES})

add_library(paddle_light_api_shared SHARED IMPORTED)
set_target_properties(paddle_light_api_shared PROPERTIES IMPORTED_LOCATION ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libpaddle_light_api_shared.so)

find_library(log-lib log)

target_link_libraries(
        native

        paddle_light_api_shared
        ${OpenCV_LIBS}
        GLESv2
        EGL

        jnigraphics
        ${log-lib}
)


add_custom_command(
        TARGET native
        POST_BUILD
        COMMAND
        ${CMAKE_COMMAND} -E copy
        ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libc++_shared.so
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libc++_shared.so)

add_custom_command(
        TARGET native
        POST_BUILD
        COMMAND
        ${CMAKE_COMMAND} -E copy
        ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libpaddle_light_api_shared.so
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libpaddle_light_api_shared.so)

add_custom_command(
        TARGET native
        POST_BUILD
        COMMAND
        ${CMAKE_COMMAND} -E copy
        ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libhiai.so
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libhiai.so)

add_custom_command(
        TARGET native
        POST_BUILD
        COMMAND
        ${CMAKE_COMMAND} -E copy
        ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libhiai_ir.so
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libhiai_ir.so)

add_custom_command(
        TARGET native
        POST_BUILD
        COMMAND
        ${CMAKE_COMMAND} -E copy
        ${PaddleLite_DIR}/cxx/libs/${ANDROID_ABI}/libhiai_ir_build.so
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libhiai_ir_build.so)