@echo off
echo Start....
setlocal enabledelayedexpansion
for %%i in (..\lib\*.jar) do  set CLASSPATH=!CLASSPATH!;%%i
java  -classpath %CLASSPATH%  net.oliver.Application
echo Finished....
@pause