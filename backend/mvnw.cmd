@echo off
setlocal enabledelayedexpansion

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "MVNW_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"

REM Find Java
if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

REM Download Maven if needed
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.6"
set "MAVEN_ZIP=%MAVEN_HOME%\apache-maven-3.9.6-bin.zip"

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    echo Downloading Maven 3.9.6...
    if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"
    PowerShell -Command "& {Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip' -OutFile '%MAVEN_ZIP%'}"
    if %errorlevel% neq 0 (
        echo Failed to download Maven. Please install Maven manually.
        pause
        exit /b 1
    )
    echo Extracting Maven...
    PowerShell -Command "& {Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%MAVEN_HOME%' -Force}"
    if %errorlevel% neq 0 (
        echo Failed to extract Maven. Please install Maven manually.
        pause
        exit /b 1
    )
    del "%MAVEN_ZIP%"
)

set "M2_HOME=%MAVEN_HOME%\apache-maven-3.9.6"
set "MAVEN_CMD=%M2_HOME%\bin\mvn.cmd"

if not exist "%MAVEN_CMD%" (
    echo Maven installation not found at: %M2_HOME%
    echo Please install Maven manually: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

"%MAVEN_CMD%" %*
exit /b %errorlevel%
