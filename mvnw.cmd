@ECHO OFF
SETLOCAL

REM 1. Deteccion automatica de JAVA_HOME si no esta configurado
IF "%JAVA_HOME%"=="" (
    IF EXIST "C:\Program Files\Java\jdk-21.0.10" (
        SET "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
    ) ELSE IF EXIST "C:\Program Files\Java\jdk-21" (
        SET "JAVA_HOME=C:\Program Files\Java\jdk-21"
    ) ELSE IF EXIST "C:\Program Files\Java\jdk-17" (
        SET "JAVA_HOME=C:\Program Files\Java\jdk-17"
    ) ELSE IF EXIST "C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot" (
        SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
    )
)

IF NOT "%JAVA_HOME%"=="" (
    SET "PATH=%JAVA_HOME%\bin;%PATH%"
)

REM 2. Deteccion de Maven (System PATH, Wrapper o Portable)
WHERE mvn >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    mvn %*
    EXIT /B %ERRORLEVEL%
)

IF EXIST "C:\Users\Benja\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.9\bin\mvn.cmd" (
    "C:\Users\Benja\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.9\bin\mvn.cmd" %*
    EXIT /B %ERRORLEVEL%
)

REM 3. Intentar invocar wrapper estandar con Java
IF EXIST ".mvn\wrapper\maven-wrapper.jar" (
    java -jar .mvn\wrapper\maven-wrapper.jar %*
    EXIT /B %ERRORLEVEL%
)

REM 4. Fallback general
mvn %*

ENDLOCAL
