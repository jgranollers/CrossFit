@echo off
setlocal enabledelayedexpansion
REM Script para compilar y ejecutar el proyecto CrossFit (Spring Boot)

cd /d "%~dp0"

if not exist mvnw.cmd (
    echo.
    echo ERROR: No se encuentra mvnw.cmd en la raiz del proyecto.
    pause
    exit /b 1
)

REM Fuerza una JDK valida si JAVA_HOME no esta definido correctamente
if not defined JAVA_HOME (
    if exist "C:\Users\usuari\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.0.36-hotspot\bin\javac.exe" (
        set "JAVA_HOME=C:\Users\usuari\AppData\Local\Programs\Eclipse Adoptium\jdk-25.0.0.36-hotspot"
    ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-17\bin\javac.exe" (
        set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17"
    )
)

if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo.
    echo ERROR: JAVA_HOME no apunta a una JDK valida.
    echo Define JAVA_HOME a una JDK 17+ y vuelve a ejecutar.
    echo Ejemplo:
    echo   setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-17"
    pause
    exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%PATH%"

echo.
echo ========================================
echo   ENTORNO JAVA
echo ========================================
echo JAVA_HOME=%JAVA_HOME%
java -version
echo.

echo ========================================
echo   COMPILANDO PROYECTO...
echo ========================================
echo.

call .\mvnw.cmd clean compile

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error durante la compilacion
    pause
    exit /b 1
)

echo.
echo ========================================
echo   COMPILACION EXITOSA!
echo ========================================
echo.
echo Para ejecutar el proyecto:
echo   .\mvnw.cmd spring-boot:run
echo.
echo Nota: Asegurate de que MySQL este corriendo primero.
echo.
pause
