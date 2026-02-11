@echo off
REM Script para ejecutar el proyecto CRUDThymeilif

cd /d C:\Users\fredb\IdeaProjects\CRUDThymeilif

echo.
echo ========================================
echo   COMPILANDO PROYECTO...
echo ========================================
echo.

mvn clean compile

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
echo El proyecto se ha compilado correctamente.
echo.
echo Para ejecutar el proyecto:
echo   mvn spring-boot:run
echo.
echo Nota: Asegúrate de que MySQL esté corriendo primero
echo.
pause
