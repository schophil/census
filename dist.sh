#!/bin/bash

# Step 1: build client
cd ./client
npm run clean
npm install
npm run dist
cd ..

# Step 2: copy client in server
rm -rf ./server/src/main/resources/static/*
cp -R ./client/dist/* ./server/src/main/resources/static

# Step 3: package server
cd server
./mvnw clean
./mvnw package
