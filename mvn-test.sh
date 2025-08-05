#!/bin/bash

# Set JAVA_HOME to Java 21
export JAVA_HOME=/Users/yasinsezgin/Library/Java/JavaVirtualMachines/openjdk-21.0.1/Contents/Home

echo "Using Java version:"
java -version

echo ""
echo "Running Maven tests..."
mvn test

echo ""
echo "Running code quality checks..."
mvn checkstyle:check pmd:pmd spotbugs:spotbugs

echo ""
echo "Running full build..."
mvn clean install 