#!/bin/bash

# Compile script for Sales Data Analysis project
# Compiles all Java files from src/ directory to bin/ directory

echo "Compiling Java files..."
javac -d bin src/*.java src/demo/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilation successful!"
    echo "Compiled files are in bin/ directory"
else
    echo "✗ Compilation failed!"
    exit 1
fi

