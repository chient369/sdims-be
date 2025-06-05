@echo off
REM Script to load data_dump.sql into the MySQL database by executing commands inside the Docker container
REM This script assumes that Docker Compose is running and the MySQL service is accessible.
REM It also requires the Docker command-line client to be installed and in the PATH.

REM MySQL Connection details (used by mysql client inside the container)
SET MYSQL_USER=root
SET MYSQL_PASSWORD=Admin@123
SET DATABASE_NAME=dev_sdims

REM Docker container details
SET CONTAINER_NAME=mysql
REM Path for the SQL file inside the Linux container (use forward slashes)
SET SQL_FILE_CONTAINER_PATH=/tmp/data_dump.sql

REM The SQL file on the host is expected to be in the same directory as this batch script.
SET SQL_FILE_HOST_PATH=%~dp0data_dump.sql

echo Checking for Docker CLI...
docker --version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    echo Docker CLI (docker.exe) is not found in your system's PATH.
    echo Please install Docker.
    pause
    exit /b 1
)

echo Checking if container '%CONTAINER_NAME%' is running...
REM Check if container is running by trying to get its ID. Output is redirected to a temporary file.
docker ps -q -f "name=%CONTAINER_NAME%" > "%TEMP%\docker_check.tmp"
SET CONTAINER_ID=
FOR /F "usebackq tokens=*" %%A IN ("%TEMP%\docker_check.tmp") DO SET CONTAINER_ID=%%A
DEL "%TEMP%\docker_check.tmp"

IF NOT DEFINED CONTAINER_ID (
    echo Error: Container '%CONTAINER_NAME%' is not running or not found.
    echo Please ensure the container name is correct and start your services using 'docker-compose up -d'.
    pause
    exit /b 1
)

echo Container '%CONTAINER_NAME%' is running (ID: %CONTAINER_ID%).

IF NOT EXIST "%SQL_FILE_HOST_PATH%" (
    echo Error: SQL dump file not found at "%SQL_FILE_HOST_PATH%"
    pause
    exit /b 1
)

echo Copying SQL file to container '%CONTAINER_NAME%' at '%SQL_FILE_CONTAINER_PATH%'...
docker cp "%SQL_FILE_HOST_PATH%" %CONTAINER_NAME%:%SQL_FILE_CONTAINER_PATH%
IF %ERRORLEVEL% NEQ 0 (
    echo Error: Failed to copy SQL file to container %CONTAINER_NAME%.
    pause
    exit /b 1
)

echo Attempting to load data from '%SQL_FILE_CONTAINER_PATH%' (inside container) into database '%DATABASE_NAME%'...
echo Using user: %MYSQL_USER%

REM Execute the SQL script inside the container
docker exec %CONTAINER_NAME% mysql -u %MYSQL_USER% -p"%MYSQL_PASSWORD%" %DATABASE_NAME% -e "source %SQL_FILE_CONTAINER_PATH%"
SET EXEC_ERRORLEVEL=%ERRORLEVEL%

echo Removing SQL file from container '%CONTAINER_NAME%'...
docker exec %CONTAINER_NAME% rm %SQL_FILE_CONTAINER_PATH%
IF %ERRORLEVEL% NEQ 0 (
    echo Warning: Failed to remove SQL file from container. You may want to remove it manually: %SQL_FILE_CONTAINER_PATH%
)

IF %EXEC_ERRORLEVEL% EQU 0 (
    echo Data loaded successfully using script inside container.
) ELSE (
    echo An error occurred while loading data inside container (Exit Code: %EXEC_ERRORLEVEL%). Please check the output above.
    echo Ensure the MySQL service is running correctly within the container and the SQL file content is valid.
)

echo Script finished.
pause 