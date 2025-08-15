# 🐳 FinScope Docker Documentation

## 📋 Overview

This document provides comprehensive instructions for running the FinScope application using Docker and Docker Compose.

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)
- At least 2GB of available RAM

### Start the Application
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app
```

### Access the Application
- **API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **MySQL**: localhost:3307
- **Redis**: localhost:6379

## 🏗️ Architecture

The application consists of three main services:

### 1. Spring Boot Application (`app`)
- **Port**: 8081
- **Image**: Custom built from Dockerfile
- **Dependencies**: MySQL, Redis

### 2. MySQL Database (`mysql_db`)
- **Port**: 3307
- **Image**: mysql:8.0
- **Database**: fin_scope_db
- **User**: fin_scope_user

### 3. Redis Cache (`redis`)
- **Port**: 6379
- **Image**: redis:7-alpine
- **Purpose**: Session storage and caching

## 📁 File Structure

```
fin-scope-back/
├── Dockerfile              # Multi-stage build for Spring Boot app
├── docker-compose.yml      # Orchestration of all services
├── docker-start.sh         # Startup script for the application
├── .dockerignore          # Files to exclude from Docker build
├── env                    # Environment variables
└── mysql/
    ├── conf/              # MySQL configuration
    └── init/              # Database initialization scripts
```

## 🔧 Configuration

### Environment Variables

Key environment variables are defined in the `env` file:

```bash
# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8081

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://mysql_db:3306/fin_scope_db
SPRING_DATASOURCE_USERNAME=fin_scope_user
SPRING_DATASOURCE_PASSWORD=fin_scope_password_secure

# JWT
JWT_SECRET=your-secret-key-change-in-production
JWT_EXPIRATION=86400000

# MySQL
MYSQL_ROOT_PASSWORD=root_password_secure
MYSQL_DATABASE=fin_scope_db
MYSQL_USER=fin_scope_user
MYSQL_PASSWORD=fin_scope_password_secure
```

### Port Configuration

- **Application**: 8081 (changed from 8080 for development)
- **MySQL**: 3307 (mapped from container port 3306)
- **Redis**: 6379

## 🛠️ Docker Commands

### Build and Run
```bash
# Build the application image
docker-compose build

# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d mysql_db

# View running containers
docker-compose ps
```

### Management
```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart services
docker-compose restart

# View logs
docker-compose logs -f app
docker-compose logs -f mysql_db
docker-compose logs -f redis
```

### Development
```bash
# Rebuild without cache
docker-compose build --no-cache

# Execute commands in running container
docker-compose exec app sh
docker-compose exec mysql_db mysql -u root -p

# Copy files from/to container
docker cp container_name:/path/to/file ./local/path
```

## 🔍 Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using the port
netstat -ano | findstr :8081

# Kill the process or change the port in docker-compose.yml
```

#### 2. Database Connection Issues
```bash
# Check if MySQL is running
docker-compose ps mysql_db

# Check MySQL logs
docker-compose logs mysql_db

# Test database connection
docker-compose exec mysql_db mysql -u fin_scope_user -pfin_scope_password_secure -e "SELECT 1;"
```

#### 3. Application Won't Start
```bash
# Check application logs
docker-compose logs app

# Check if all dependencies are ready
docker-compose ps

# Restart the application
docker-compose restart app
```

#### 4. Memory Issues
```bash
# Check container resource usage
docker stats

# Increase memory limits in docker-compose.yml
```

### Health Checks

The application includes health checks:

```bash
# Check application health
curl http://localhost:8081/actuator/health

# Check container health
docker-compose ps
```

## 🔒 Security Considerations

### Production Deployment

1. **Change Default Passwords**
   - Update `MYSQL_ROOT_PASSWORD`
   - Update `MYSQL_PASSWORD`
   - Update `JWT_SECRET`

2. **Use Secrets Management**
   - Store sensitive data in Docker secrets
   - Use environment-specific configuration

3. **Network Security**
   - Use custom Docker networks
   - Restrict port exposure
   - Implement proper firewall rules

4. **Container Security**
   - Run as non-root user (already configured)
   - Keep base images updated
   - Scan images for vulnerabilities

## 📊 Monitoring

### Logs
```bash
# Application logs
docker-compose logs -f app

# Database logs
docker-compose logs -f mysql_db

# All logs
docker-compose logs -f
```

### Metrics
- Application metrics: http://localhost:8081/actuator/metrics
- Health status: http://localhost:8081/actuator/health

## 🚀 Deployment

### Production Checklist

- [ ] Update all passwords and secrets
- [ ] Configure proper logging
- [ ] Set up monitoring and alerting
- [ ] Configure backup strategy
- [ ] Test disaster recovery
- [ ] Set up CI/CD pipeline
- [ ] Configure SSL/TLS certificates
- [ ] Set up load balancing

### Scaling
```bash
# Scale application instances
docker-compose up -d --scale app=3

# Scale with specific configuration
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [MySQL Docker Image](https://hub.docker.com/_/mysql)
- [Redis Docker Image](https://hub.docker.com/_/redis)

## 🤝 Support

For issues related to:
- **Docker configuration**: Check this documentation
- **Application issues**: Check application logs
- **Database issues**: Check MySQL logs and connectivity
- **Performance issues**: Monitor resource usage with `docker stats`
