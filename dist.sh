#!/bin/bash

# Step 1: build client
cd ./client
npm run dist
cd ..

# Step 2: copy client in server
rm -R ./server/src/main/resources/static/*
cp -R ./client/dist/* ./server/src/main/resources/static

# Step 3: package server
cd server
./mvnw package
