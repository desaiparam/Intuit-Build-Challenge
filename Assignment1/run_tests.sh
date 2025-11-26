#!/bin/bash

# Test runner script for Producer-Consumer project

echo "=== Producer-Consumer Test Suite ==="
echo ""

# Compile first
echo "Compiling..."
javac -d bin src/*.java src/test/*.java src/demo/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "=== Running Fixed Queue Tests ==="
java -cp bin -ea BoundedBlockingQueueTest

echo ""
echo "=== Running Dynamic Queue Tests ==="
java -cp bin -ea DynamicBoundedBlockingQueueTest

echo ""
echo "=== Running Integration Tests ==="
java -cp bin -ea ProducerConsumerIntegrationTest

echo ""
echo "=== All Tests Complete ==="

