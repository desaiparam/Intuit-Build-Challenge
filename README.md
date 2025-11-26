# Intuit Build Challenge - Take Home Assignment

## Overview

This repository contains two Java projects demonstrating core programming competencies:

1. **Project1** - Producer-Consumer Pattern with Thread Synchronization
2. **Project2** - Sales Data Analysis using Stream API

Both projects are implemented in pure Java with no external dependencies.

---

## Project 1: Producer-Consumer Pattern

**Objective**: Implement thread synchronization using wait/notify mechanism

**Key Features**:
- Custom blocking queue implementation
- Producer and consumer threads
- Thread synchronization and communication
- Flow control mechanisms

**Quick Start**:
```bash
cd Project1
./compile.sh
java -cp bin ProducerConsumerDemo
```

**Testing Objectives**:
- Thread synchronization
- Concurrent programming
- Blocking queues
- Wait/Notify mechanism

For details, see [Project1/README.md](Project1/README.md)

---

## Project 2: Sales Data Analysis

**Objective**: Perform data analysis using Stream API on CSV data

**Key Features**:
- CSV data loading and parsing
- Data aggregations (sum, average, count, min, max)
- Grouping operations (by category, region, month)
- Top N queries and filtered aggregations

**Quick Start**:
```bash
cd Project2
./compile.sh
java -cp bin SalesAnalysisDemo
```

**Testing Objectives**:
- Functional programming
- Stream operations
- Data aggregation
- Lambda expressions

For details, see [Project2/README.md](Project2/README.md)

---

## Requirements

- Java JDK 8 or higher
- No external dependencies (pure Java)

## Project Structure

```
.
├── Project1/              # Producer-Consumer implementation
│   ├── src/              # Source code
│   ├── bin/              # Compiled classes
│   ├── README.md         # Project1 documentation
│   └── DESIGN_DECISIONS.md
├── Project2/              # Sales Data Analysis
│   ├── src/              # Source code
│   ├── data/             # CSV data files
│   ├── bin/              # Compiled classes
│   ├── README.md         # Project2 documentation
│   └── DESIGN_DECISIONS.md
└── README.md              # This file
```

## Deliverables

- Complete source code for both projects
- Unit tests (Project1)
- Demo applications with console output
- README files with setup instructions
- Design decisions documentation


