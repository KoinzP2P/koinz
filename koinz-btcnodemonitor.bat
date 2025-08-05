@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  koinz-btcnodemonitor startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and KOINZ_BTCNODEMONITOR_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\btcnodemonitor.jar;%APP_HOME%\lib\core.jar;%APP_HOME%\lib\p2p.jar;%APP_HOME%\lib\common.jar;%APP_HOME%\lib\proto.jar;%APP_HOME%\lib\logback-classic-1.1.11.jar;%APP_HOME%\lib\logback-core-1.1.11.jar;%APP_HOME%\lib\assets.jar;%APP_HOME%\lib\guava-30.1.1-jre.jar;%APP_HOME%\lib\commons-lang3-3.14.0.jar;%APP_HOME%\lib\tor.external-2b459dc.jar;%APP_HOME%\lib\tor.native-2b459dc.jar;%APP_HOME%\lib\tor-2b459dc.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.3.41.jar;%APP_HOME%\lib\kotlin-logging-1.5.4.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.3.41.jar;%APP_HOME%\lib\kotlin-stdlib-1.3.41.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\spark-core-2.9.4.jar;%APP_HOME%\lib\jsonrpc4j-1.6.0.bisq.1.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar;%APP_HOME%\lib\bitcoinj-b005953e5eec339a82daf4866f85518091f6b9f6.jar;%APP_HOME%\lib\grpc-protobuf-1.42.1.jar;%APP_HOME%\lib\proto-google-common-protos-2.0.1.jar;%APP_HOME%\lib\protobuf-java-3.19.1.jar;%APP_HOME%\lib\grpc-stub-1.42.1.jar;%APP_HOME%\lib\javafx-graphics-16-mac.jar;%APP_HOME%\lib\javafx-base-16-mac.jar;%APP_HOME%\lib\javafx-base-16.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.42.1.jar;%APP_HOME%\lib\grpc-netty-shaded-1.42.1.jar;%APP_HOME%\lib\grpc-core-1.42.1.jar;%APP_HOME%\lib\grpc-api-1.42.1.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\guice-5.0.1.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\jopt-simple-5.0.4.jar;%APP_HOME%\lib\bcpg-jdk15on-1.63.jar;%APP_HOME%\lib\jackson-databind-2.17.1.jar;%APP_HOME%\lib\jackson-core-2.17.1.jar;%APP_HOME%\lib\jackson-annotations-2.17.1.jar;%APP_HOME%\lib\commons-codec-1.13.jar;%APP_HOME%\lib\httpclient-4.5.12.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\easybind-1.0.3.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-qual-3.8.0.jar;%APP_HOME%\lib\error_prone_annotations-2.9.0.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\jsocks-43b004b5bcc468fb1213767853e1d8a54fd4a359.jar;%APP_HOME%\lib\bcprov-jdk15to18-1.63.jar;%APP_HOME%\lib\jcip-annotations-1.0.jar;%APP_HOME%\lib\jetty-webapp-9.4.48.v20220622.jar;%APP_HOME%\lib\websocket-server-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-servlet-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-security-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-server-9.4.48.v20220622.jar;%APP_HOME%\lib\websocket-servlet-9.4.48.v20220622.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\bcprov-jdk15on-1.63.jar;%APP_HOME%\lib\perfmark-api-0.23.0.jar;%APP_HOME%\lib\tor-binary-macos-580d31bdcf1fabccd38456aa084044064d89d5c1.jar;%APP_HOME%\lib\tor-binary-linux32-580d31bdcf1fabccd38456aa084044064d89d5c1.jar;%APP_HOME%\lib\tor-binary-linux64-580d31bdcf1fabccd38456aa084044064d89d5c1.jar;%APP_HOME%\lib\tor-binary-windows-580d31bdcf1fabccd38456aa084044064d89d5c1.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\tor-binary-geoip-580d31bdcf1fabccd38456aa084044064d89d5c1.jar;%APP_HOME%\lib\jtorctl-b2a172f44edcd8deaa5ed75d936dcbb007f0d774.jar;%APP_HOME%\lib\commons-compress-1.21.jar;%APP_HOME%\lib\xz-1.6.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\websocket-client-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-client-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-http-9.4.48.v20220622.jar;%APP_HOME%\lib\websocket-common-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-io-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-xml-9.4.48.v20220622.jar;%APP_HOME%\lib\websocket-api-9.4.48.v20220622.jar;%APP_HOME%\lib\grpc-context-1.42.1.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.3.41.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\jetty-util-ajax-9.4.48.v20220622.jar;%APP_HOME%\lib\jetty-util-9.4.48.v20220622.jar


@rem Execute koinz-btcnodemonitor
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %KOINZ_BTCNODEMONITOR_OPTS%  -classpath "%CLASSPATH%" koinz.btcnodemonitor.BtcNodeMonitorMain %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable KOINZ_BTCNODEMONITOR_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%KOINZ_BTCNODEMONITOR_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
