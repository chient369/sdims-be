#!/bin/bash
# Script to load data_dump.sql into the MySQL database by executing commands inside the Docker container
# This script assumes that Docker Compose is running and the MySQL service is accessible.
# It also requires the Docker and MySQL command-line clients to be installed and in the PATH.

# MySQL Connection details (used by mysql client inside the container)
MYSQL_USER="root"
MYSQL_PASSWORD="Admin@123"
DATABASE_NAME="dev_sdims"

# Docker container details
CONTAINER_NAME="mysql" # As defined in docker-compose.yml container_name
SQL_FILE_CONTAINER_PATH="/tmp/data_dump.sql" # Temporary path for the SQL file inside the container

# Determine the directory where the script is located on the host
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SQL_FILE_HOST_PATH="${SCRIPT_DIR}/data_dump.sql"

echo "Checking for Docker CLI..."
if ! command -v docker &> /dev/null; then
    echo "Docker CLI (docker) is not found in your system's PATH."
    echo "Please install Docker."
    exit 1
fi

echo "Checking if container '${CONTAINER_NAME}' is running..."
if ! docker ps -f "name=^${CONTAINER_NAME}$" --format "{{.Names}}" | grep -q "^${CONTAINER_NAME}$"; then
    echo "Error: Container '${CONTAINER_NAME}' is not running."
    echo "Please start your services using 'docker-compose up -d'."
    exit 1
fi

echo "Checking for MySQL client (inside container, assumed to be present)..."
# We assume mysql client is available in the mysql container

if [ ! -f "${SQL_FILE_HOST_PATH}" ]; then
    echo "Error: SQL dump file not found at ${SQL_FILE_HOST_PATH}"
    exit 1
fi

echo "Copying SQL file to container '${CONTAINER_NAME}' at '${SQL_FILE_CONTAINER_PATH}'..."
docker cp "${SQL_FILE_HOST_PATH}" "${CONTAINER_NAME}:${SQL_FILE_CONTAINER_PATH}"
if [ $? -ne 0 ]; then
    echo "Error: Failed to copy SQL file to container ${CONTAINER_NAME}."
    exit 1
fi

echo "Attempting to load data from '${SQL_FILE_CONTAINER_PATH}' (inside container) into database '${DATABASE_NAME}'..."
echo "Using user: ${MYSQL_USER}"

# Execute the SQL script inside the container
# Using -e "source file_path" is a clean way to execute a script with mysql client
docker exec "${CONTAINER_NAME}" mysql -u "${MYSQL_USER}" -p"${MYSQL_PASSWORD}" "${DATABASE_NAME}" -e "source ${SQL_FILE_CONTAINER_PATH}"
EXEC_EXIT_CODE=$?

echo "Removing SQL file from container '${CONTAINER_NAME}'..."
docker exec "${CONTAINER_NAME}" rm "${SQL_FILE_CONTAINER_PATH}"
if [ $? -ne 0 ]; then
    echo "Warning: Failed to remove SQL file from container. You may want to remove it manually: ${SQL_FILE_CONTAINER_PATH}"
fi

if [ ${EXEC_EXIT_CODE} -eq 0 ]; then
    echo "Data loaded successfully using script inside container."
else
    echo "An error occurred while loading data inside container (Exit Code: ${EXEC_EXIT_CODE}). Please check the output above."
    echo "Ensure the MySQL service is running correctly within the container and the SQL file content is valid."
fi

echo "Script finished."
exit ${EXEC_EXIT_CODE} 