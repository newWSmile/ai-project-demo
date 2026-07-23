@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "PATH=%JAVA_HOME%\bin;%PATH%"
mvn %*
set "MAVEN_EXIT_CODE=%ERRORLEVEL%"
endlocal & exit /b %MAVEN_EXIT_CODE%

