@rem Gradle startup script for Windows
@if "%DEBUG%"=="" @echo off
set DIRNAME=%~dp0
if "%JAVA_HOME%" == "" goto javaHomeEnd
set JAVA_EXE=%JAVA_HOME%/bin/java.exe
goto javaHomeEnd
:javaHomeEnd
if exist "%JAVA_EXE%" goto execute
set JAVA_EXE=java.exe
:execute
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%DIRNAME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
