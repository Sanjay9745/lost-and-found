# Docker Documentation for Lost and Found System

## 📦 Docker Setup

This document explains how to run the Lost and Found System using Docker.

## Prerequisites

- Docker Engine 20.10+ installed
- Docker Compose 2.0+ installed
- At least 1GB of free disk space

## Quick Start

### Option 1: Using Docker Compose (Recommended)

```bash
# Build and start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Option 2: Using Docker CLI

```bash
# Build the image
docker build -t lost-and-found:latest .

# Run the container
docker run -d \
  --name lost-and-found-app \
  -p 8080:8080 \
  -v ./data:/app/data \
  lost-and-found:latest

# View logs
docker logs -f lost-and-found-app

# Stop the container
docker stop lost-and-found-app

# Remove the container
docker rm lost-and-found-app
```

## Configuration

### Environment Variables

You can customize the application using environment variables:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - JAVA_OPTS=-Xmx512m -Xms256m
  - SERVER_PORT=8080
```

### Port Mapping

By default, the application runs on port 8080. To change it:

```yaml
ports:
  - "9090:8080"  # Access app on localhost:9090
```

### Data Persistence

The H2 database files are stored in the `./data` directory and mounted as a volume:

```yaml
volumes:
  - ./data:/app/data
```

## Building the Image

### Development Build

```bash
docker build -t lost-and-found:dev .
```

### Production Build

```bash
docker build -t lost-and-found:1.0.0 .
docker tag lost-and-found:1.0.0 lost-and-found:latest
```

### Multi-platform Build

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t lost-and-found:latest .
```

## Docker Commands Reference

### Container Management

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose stop

# Restart services
docker-compose restart

# View running containers
docker-compose ps

# View logs
docker-compose logs -f lost-and-found-app

# Execute commands in container
docker-compose exec lost-and-found-app sh
```

### Image Management

```bash
# List images
docker images | grep lost-and-found

# Remove image
docker rmi lost-and-found:latest

# Rebuild image
docker-compose build --no-cache
```

### Cleanup

```bash
# Remove stopped containers
docker-compose rm -f

# Remove unused images
docker image prune -a

# Complete cleanup
docker-compose down -v --rmi all
```

## Health Checks

The application includes health checks:

```bash
# Check container health
docker inspect --format='{{.State.Health.Status}}' lost-and-found-app

# View health check logs
docker inspect --format='{{range .State.Health.Log}}{{.Output}}{{end}}' lost-and-found-app
```

## Accessing the Application

Once running, access:

- **API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Swagger UI** (if enabled): http://localhost:8080/swagger-ui.html

## Default Credentials

- **Username**: `admin`
- **Password**: `admin123`

## Troubleshooting

### Container won't start

```bash
# Check logs
docker-compose logs lost-and-found-app

# Check container status
docker-compose ps
```

### Port already in use

```bash
# Change port in docker-compose.yml
ports:
  - "8081:8080"
```

### Database issues

```bash
# Remove data volume and restart
docker-compose down -v
docker-compose up -d
```

### Out of memory

```bash
# Increase memory limit
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m
```

## Production Deployment

### Using Docker Swarm

```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml lost-and-found

# List services
docker stack services lost-and-found

# Remove stack
docker stack rm lost-and-found
```

### Using Kubernetes

```bash
# Generate Kubernetes manifests
docker-compose config | kompose convert

# Apply manifests
kubectl apply -f .
```

## Security Best Practices

1. ✅ **Non-root user**: The container runs as a non-root user
2. ✅ **Minimal base image**: Uses Alpine Linux for smaller attack surface
3. ✅ **Multi-stage build**: Separates build and runtime dependencies
4. ✅ **Health checks**: Monitors application health
5. ⚠️ **Change default credentials** in production
6. ⚠️ **Use secrets management** for sensitive data
7. ⚠️ **Enable HTTPS** in production

## Performance Optimization

### Memory Tuning

```yaml
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100
```

### Resource Limits

```yaml
deploy:
  resources:
    limits:
      cpus: '1'
      memory: 1G
    reservations:
      cpus: '0.5'
      memory: 512M
```

## Monitoring

### Container Stats

```bash
# Real-time stats
docker stats lost-and-found-app

# Using docker-compose
docker-compose stats
```

### Logs

```bash
# Follow logs
docker-compose logs -f --tail=100

# Export logs
docker-compose logs > app.log
```

## Backup and Restore

### Backup Database

```bash
# Backup data directory
docker-compose exec lost-and-found-app tar czf /tmp/backup.tar.gz /app/data
docker cp lost-and-found-app:/tmp/backup.tar.gz ./backup-$(date +%Y%m%d).tar.gz
```

### Restore Database

```bash
# Restore data
docker cp ./backup.tar.gz lost-and-found-app:/tmp/
docker-compose exec lost-and-found-app tar xzf /tmp/backup.tar.gz -C /
docker-compose restart
```

## Support

For issues and questions:
- Check logs: `docker-compose logs -f`
- GitHub Issues: [Your Repository]
- Email: [Your Email]

## License

[Your License]
