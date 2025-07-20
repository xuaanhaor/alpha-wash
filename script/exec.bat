@echo off
echo 🚀 Starting Alpha Wash App with Docker Compose...
echo --------------------------------------------------

:: Di chuyển vào thư mục chứa docker-compose.yml
cd /d "%~dp0"

:: Khởi chạy docker compose với build nếu cần
docker-compose up

pause