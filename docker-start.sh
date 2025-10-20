#!/bin/bash
# Docker Quick Start Script for Lost and Found System

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "================================"
echo "Lost and Found - Docker Setup"
echo "================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo -e "${RED}[ERROR] Docker is not installed${NC}"
    echo "Please install Docker from: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}[ERROR] Docker Compose is not installed${NC}"
    exit 1
fi

echo -e "${GREEN}Docker is installed and ready!${NC}"
echo ""

show_menu() {
    echo "Choose an option:"
    echo "1. Build and Start Application"
    echo "2. Stop Application"
    echo "3. View Logs"
    echo "4. Rebuild Application"
    echo "5. Clean Up (Remove containers and volumes)"
    echo "6. Check Status"
    echo "7. Exit"
    echo ""
    read -p "Enter your choice (1-7): " choice
}

start_app() {
    echo ""
    echo -e "${YELLOW}[INFO] Building and starting the application...${NC}"
    if docker-compose up -d --build; then
        echo ""
        echo -e "${GREEN}[SUCCESS] Application started successfully!${NC}"
        echo "Access the application at: http://localhost:8080"
        echo "Health check: http://localhost:8080/actuator/health"
        echo ""
        echo "Default credentials:"
        echo "Username: admin"
        echo "Password: admin123"
    else
        echo -e "${RED}[ERROR] Failed to start the application${NC}"
    fi
    echo ""
    read -p "Press Enter to continue..."
}

stop_app() {
    echo ""
    echo -e "${YELLOW}[INFO] Stopping the application...${NC}"
    if docker-compose stop; then
        echo -e "${GREEN}[SUCCESS] Application stopped successfully!${NC}"
    else
        echo -e "${RED}[ERROR] Failed to stop the application${NC}"
    fi
    echo ""
    read -p "Press Enter to continue..."
}

view_logs() {
    echo ""
    echo -e "${YELLOW}[INFO] Showing logs (Press Ctrl+C to exit)...${NC}"
    docker-compose logs -f
}

rebuild_app() {
    echo ""
    echo -e "${YELLOW}[INFO] Rebuilding the application...${NC}"
    docker-compose down
    docker-compose build --no-cache
    if docker-compose up -d; then
        echo -e "${GREEN}[SUCCESS] Application rebuilt and started successfully!${NC}"
    else
        echo -e "${RED}[ERROR] Failed to rebuild the application${NC}"
    fi
    echo ""
    read -p "Press Enter to continue..."
}

cleanup() {
    echo ""
    echo -e "${YELLOW}[WARNING] This will remove all containers, volumes, and images${NC}"
    read -p "Are you sure? (y/n): " confirm
    if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
        echo -e "${YELLOW}[INFO] Cleaning up...${NC}"
        docker-compose down -v --rmi all
        echo -e "${GREEN}[SUCCESS] Cleanup completed!${NC}"
    else
        echo -e "${YELLOW}[INFO] Cleanup cancelled${NC}"
    fi
    echo ""
    read -p "Press Enter to continue..."
}

check_status() {
    echo ""
    echo -e "${YELLOW}[INFO] Checking application status...${NC}"
    docker-compose ps
    echo ""
    echo -e "${YELLOW}[INFO] Container health:${NC}"
    health=$(docker inspect --format="{{.State.Health.Status}}" lost-and-found-app 2>/dev/null || echo "not available")
    echo "Health status: $health"
    echo ""
    read -p "Press Enter to continue..."
}

# Main loop
while true; do
    clear
    echo "================================"
    echo "Lost and Found - Docker Setup"
    echo "================================"
    echo ""
    show_menu
    
    case $choice in
        1) start_app ;;
        2) stop_app ;;
        3) view_logs ;;
        4) rebuild_app ;;
        5) cleanup ;;
        6) check_status ;;
        7) 
            echo ""
            echo "Thank you for using Lost and Found System!"
            exit 0
            ;;
        *) 
            echo -e "${RED}Invalid option. Please try again.${NC}"
            sleep 2
            ;;
    esac
done
