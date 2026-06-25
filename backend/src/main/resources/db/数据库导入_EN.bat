@echo off
setlocal enabledelayedexpansion
title Database Import Tool

echo ========================================
echo   Database Import Tool---by cerovorti
echo ========================================
echo.

echo [Info] Searching for MySQL installation location...
echo.

REM Method 1: Check mysql in PATH environment variable
where mysql >nul 2>&1
if not errorlevel 1 (
    echo Found mysql command in PATH
    set MYSQL_CMD=mysql
    goto found_mysql
)

REM Method 2: Check common installation paths
set "MYSQL_PATHS[0]=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "MYSQL_PATHS[1]=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
set "MYSQL_PATHS[2]=C:\Program Files\MySQL\MySQL Server 5.7\bin\mysql.exe"
set "MYSQL_PATHS[3]=C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
set "MYSQL_PATHS[4]=C:\Program Files (x86)\MySQL\MySQL Server 5.7\bin\mysql.exe"
set "MYSQL_PATHS[5]=D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

set FOUND=0
for /L %%i in (0,1,5) do (
    if exist "!MYSQL_PATHS[%%i]!" (
        echo Found MySQL: !MYSQL_PATHS[%%i]!
        set "MYSQL_CMD=!MYSQL_PATHS[%%i]!"
        set FOUND=1
        goto found_mysql
    )
)

REM If not found, ask user to enter manually
echo Automatic search did not find MySQL installation location
echo.
echo Please specify the full path to mysql.exe:
echo Example: C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
echo.
set /p MYSQL_CMD="Enter full path: "

if "%MYSQL_CMD%"=="" (
    echo No path entered, exiting
    pause
    exit /b 1
)

if not exist "%MYSQL_CMD%" (
    echo Path does not exist: %MYSQL_CMD%
    pause
    exit /b 1
)

:found_mysql
echo.
echo [Info] Using MySQL command: %MYSQL_CMD%
echo.

REM Show SQL files in current directory
echo SQL backup files in current directory:
dir *.sql /b 2>nul

echo.
set /p BACKUP_FILE="Enter the backup filename to import: "

if "%BACKUP_FILE%"=="" (
    echo Filename cannot be empty
    pause
    exit /b 1
)

if not exist "%BACKUP_FILE%" (
    echo File does not exist: %BACKUP_FILE%
    echo.
    echo Current directory file list:
    dir *.sql /b
    pause
    exit /b 1
)

echo.
echo ========================================
echo Confirm import information: %BACKUP_FILE%
echo ========================================
echo.
set /p CONFIRM="Confirm import? (y/N): "

if /i not "%CONFIRM%"=="y" (
    echo Import cancelled
    pause
    exit /b 0
)

echo.
echo Importing database...
echo If prompted for password, enter MySQL root user password
echo.

%MYSQL_CMD% -u root -p < "%BACKUP_FILE%"

if %errorlevel% equ 0 (
    echo.
    echo Database import successful!
    echo.
    echo You can now start the System
) else (
    echo.
    echo Database import failed!
    echo.
    echo Possible reasons:
    echo   - MySQL password incorrect
    echo   - MySQL service not started
    echo   - Backup file corrupted
    echo   - Database conflict (database already exists)
)

echo.
pause
endlocal
