@echo off
title Sistema Ganadero Rocha & Altamirano
color 0F
echo ====================================================================
echo   SISTEMA DE GESTION GANADERA - ROCHA & ALTAMIRANO
echo   Control Operativo, Trazabilidad Bovina y Balance Financiero (C$)
echo ====================================================================
echo.

set MYSQL_BIN=C:\xampp\mysql\bin\mysql.exe
if not exist "%MYSQL_BIN%" (
    where mysql >nul 2>nul
    if %errorlevel% equ 0 set MYSQL_BIN=mysql
)

if exist "%MYSQL_BIN%" (
    "%MYSQL_BIN%" -u root -e "SELECT 1;" >nul 2>nul
    if %errorlevel% neq 0 (
        echo [AVISO] El servicio de MySQL no parece estar encendido.
        echo Recuerde abrir XAMPP Control Panel y presionar 'Start' en MySQL.
        echo Si la base de datos 'sgg' aun no existe, puede ejecutar 'importar_base_datos.bat'.
        echo.
    ) else (
        echo [OK] MySQL detectado y conectado en localhost:3306.
    )
)

echo Iniciando servidor Spring Boot en http://localhost:8080 ...
echo Presione Ctrl + C para detener el servidor.
echo ====================================================================
echo.

call mvnw.cmd spring-boot:run
pause
