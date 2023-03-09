@echo off

set DIRECTORY=build\libs
set FILENAME=SideDroid-Desktop-jvm-1.3.0.jar

if exist "%DIRECTORY%\%FILENAME%" (
  echo running
  java -jar %DIRECTORY%\%FILENAME%
) else (
  echo building
  gradlew build
  java -jar %DIRECTORY%\%FILENAME%
)

