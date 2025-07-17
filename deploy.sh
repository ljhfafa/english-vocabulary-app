#!/bin/bash

# 设置环境变量

export DEEPSEEK_API_KEY="sk-ed0162dab1984855a10a14bbd7a6a93c"

export MYSQL_PASSWORD="Lijiahong123"

# 后端构建和启动

echo "Building backend..."

cd backend

mvn clean package -DskipTests

nohup java -jar target/backend-0.0.1-SNAPSHOT.jar > backend.log 2>&1 &

# 前端构建

echo "Building frontend..."

cd ../frontend

npm install

npm run build

# 配置nginx（稍后会做）

echo "Deployment started!"
