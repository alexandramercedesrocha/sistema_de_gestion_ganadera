@echo off
title Importar Base de Datos SGG en MySQL
color 0A
echo ====================================================================
echo   IMPORTADOR AUTOMATICO DE BASE DE DATOS 'sgg' - ROCHA & ALTAMIRANO
echo ====================================================================
echo.

set MYSQL_BIN=C:\xampp\mysql\bin\mysql.exe

if not exist "%MYSQL_BIN%" (
    where mysql >nul 2>nul
    if %errorlevel% equ 0 (
        set MYSQL_BIN=mysql
    ) else (
        echo [ERROR] No se encontro mysql.exe en C:\xampp\mysql\bin ni en el PATH.
        echo Asegurese de que XAMPP este instalado en C:\xampp.
        pause
        exit /b 1
    )
)

set MYSQL_AUTH=-u root

echo [1/3] Verificando conexion a MySQL (localhost:3306)...
"%MYSQL_BIN%" %MYSQL_AUTH% -e "SELECT 1;" >nul 2>nul
if %errorlevel% neq 0 (
    echo [AVISO] No se pudo conectar a MySQL sin contrasena.
    echo Si su usuario root tiene contrasena (ej. MySQL Workbench o XAMPP con clave), escribala a continuacion:
    set /p DB_PASS="Contrasena de MySQL root (o presione Enter para reintentar): "
    if defined DB_PASS (
        set MYSQL_AUTH=-u root -p%DB_PASS%
    )
    "%MYSQL_BIN%" %MYSQL_AUTH% -e "SELECT 1;" >nul 2>nul
    if %errorlevel% neq 0 (
        echo [ERROR] No se pudo conectar al servidor MySQL.
        echo Asegurese de que MySQL este iniciado (Start en XAMPP o servicio de Windows) y que la contrasena sea correcta.
        echo.
        pause
        exit /b 1
    )
)

echo [2/3] Creando base de datos 'sgg' si no existe...
"%MYSQL_BIN%" %MYSQL_AUTH% -e "CREATE DATABASE IF NOT EXISTS sgg CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if %errorlevel% neq 0 (
    echo [ERROR] No se pudo crear la base de datos 'sgg'.
    pause
    exit /b 1
)

echo [3/3] Importando esquema relacional (17 tablas) desde sgg.sql...
"%MYSQL_BIN%" %MYSQL_AUTH% sgg < "%~dp0src\main\resources\db\sgg.sql"
if %errorlevel% neq 0 (
    echo [ERROR] Ocurrio un problema al importar las tablas en 'sgg'.
    pause
    exit /b 1
)

echo.
echo ====================================================================
echo   EXITO: Base de datos 'sgg' creada e importada correctamente!
echo   Las 17 tablas normalizadas estan listas y limpias (sin datos falsos).
echo ====================================================================
echo.
pause

