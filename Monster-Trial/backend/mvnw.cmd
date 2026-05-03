@echo off
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)
echo Maven is not installed. Please install Maven or run ./mvnw from Git Bash/WSL so it can download Maven.
exit /b 1
