cmake_minimum_required(VERSION 3.0)
find_package(JAVA REQUIRED)
include(UseJava)

project(game)

add_jar(game Game.java)

# Generate the run.bat file
# message("java = ${Java_JAVA_EXECUTABLE}")
set(JAR_FILE ${CMAKE_BINARY_DIR}/game.jar)
configure_file(run.bat.in run.bat @ONLY)