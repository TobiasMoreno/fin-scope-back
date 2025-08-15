#!/bin/sh

# Docker startup script for FinScope Spring Boot application

# Set default JVM options if not provided
if [ -z "$JAVA_OPTS" ]; then
    export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"
fi

# Set default server port if not provided
if [ -z "$SERVER_PORT" ]; then
    export SERVER_PORT=8081
fi

# Set default Spring profile if not provided
if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
    export SPRING_PROFILES_ACTIVE=prod
fi

# Wait for database to be ready (if using docker-compose)
if [ -n "$SPRING_DATASOURCE_URL" ] && echo "$SPRING_DATASOURCE_URL" | grep -q "mysql_db"; then
    echo "Waiting for MySQL database to be ready..."
    while ! nc -z mysql_db 3306; do
        sleep 2
    done
    echo "MySQL database is ready!"
fi

# Wait for Redis to be ready (if using docker-compose)
if [ -n "$REDIS_HOST" ] && [ "$REDIS_HOST" = "redis" ]; then
    echo "Waiting for Redis to be ready..."
    while ! nc -z redis 6379; do
        sleep 2
    done
    echo "Redis is ready!"
fi

# Start the application
echo "Starting FinScope application on port $SERVER_PORT with profile $SPRING_PROFILES_ACTIVE"
echo "JVM Options: $JAVA_OPTS"

exec java $JAVA_OPTS -jar app.jar
