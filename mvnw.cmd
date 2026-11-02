@ECHO OFF
SETLOCAL

IF "%JAVA_HOME%"=="" (
    SET "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.101-hotspot"
)

SET "MAVEN_BIN=C:\Users\LEGION\.gemini\antigravity\scratch\apache-maven\apache-maven-3.9.9\bin"
SET "PATH=%MAVEN_BIN%;%JAVA_HOME%\bin;%PATH%"

"%MAVEN_BIN%\mvn.cmd" %*

ENDLOCAL
