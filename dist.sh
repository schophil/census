#!/bin/sh

# Step 1: build client
cd ./client
gulp dist
cd ..

# Step 2: copy client in server
cp -R ./client/target/dist/* ./server/src/main/resources/static

# Step 3: package server
cd server
./mvnw package

