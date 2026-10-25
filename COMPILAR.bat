@echo off
where gradle >nul 2>nul || (echo Necesitas instalar Gradle 8.x y JDK 17 primero & pause & exit /b 1)
gradle build
echo.
echo Listo. Tu mod esta en la carpeta build\libs\gamerpc-1.0.0.jar
pause
