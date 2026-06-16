@echo off
REM setup-android-probe.bat — Install and start Android Probe on connected device
REM Usage: setup-android-probe.bat [apk-path]

setlocal

set APK=%~1
if "%APK%"=="" set APK=%~dp0android-probe.apk

if not exist "%APK%" (
    echo ERROR: APK not found at %APK%
    echo Please build: android-probe\gradlew.bat assembleDebug
    exit /b 1
)

echo Checking adb...
adb version >nul 2>&1
if errorlevel 1 (
    echo ERROR: adb not found in PATH
    echo Please install Android SDK Platform Tools
    exit /b 1
)

echo Checking devices...
for /f "skip=1 tokens=1,2" %%a in ('adb devices') do (
    if "%%b"=="device" (
        echo Found device: %%a
        echo Installing APK...
        adb -s %%a install -r "%APK%"
        if errorlevel 1 (
            echo ERROR: Install failed
            exit /b 1
        )
        echo Starting Probe...
        adb -s %%a shell am start -n io.legado.probe/.WebViewProbeActivity
        echo Setting up port forward...
        adb -s %%a forward tcp:18888 tcp:18888
        echo.
        echo Android Probe is running on device %%a
        echo Port forward: localhost:18888 ^> device:18888
        echo.
        echo To stop: adb -s %%a shell am force-stop io.legado.probe
        goto :done
    )
)

echo ERROR: No Android devices connected
exit /b 1

:done
endlocal
