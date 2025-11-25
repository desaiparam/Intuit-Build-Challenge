# Design Decisions Documentation

This document details key design choices, trade-offs, and implementation reasoning for the Sales Data Analysis application.

## Table of Contents

1. [Language and Build System](#1-language-and-build-system)
2. [Data Model Design](#2-data-model-design)
3. [CSV Reading Strategy](#3-csv-reading-strategy)
4. [Stream API vs Alternatives](#4-stream-api-vs-alternatives)
5. [Aggregation Strategy](#5-aggregation-strategy)
6. [Functional Programming Approach](#6-functional-programming-approach)
7. [Testing Strategy](#7-testing-strategy)
8. [Project Organization](#8-project-organization)
9. [CSV Data Structure](#9-csv-data-structure)


## 1. Language and Build System

**Decision**: Java only, no Maven/Gradle - pure Java classes

**Rationale**: 
- Assignment specifically mentions "Stream operations" which refers to Java Streams API
- No external dependencies simplifies setup and portability
- Focus on core Stream API concepts without framework overhead

**Implementation**:
- Compilation: `./compile.sh` or `javac -d bin src/*.java src/demo/*.java`
- Execution: `java -cp bin <ClassName>`
- No external dependencies

## 2. Data Model Design

**Decision**: Simple  Java Objects and  helper methods

**Rationale**:
- Encapsulates all sales data fields in a single object
- Immutable design 
- Helper methods (`getTotalRevenue()`, `getMonth()`) reduce duplication in analysis code
- Clear separation of data model from business logic

**Implementation**:
- `SalesRecord` class with 7 fields: date, product, category, quantity, price, region, salesperson
- All fields are private with public getters
- `getTotalRevenue()` calculates price * quantity
- `getMonth()` extracts month from date string
- `toString()` for debugging

## 3. CSV Reading Strategy

**Decision**: Custom CSV reader using BufferedReader and String.split()

**Rationale**:
- No external dependencies 
- Simple CSV format doesn't require complex parsing
- Handles header row and basic validation
- Graceful error handling 

**Implementation**:
- `CSVReader.read()` static method
- Skips header row
- Parses each line using `split(",")`
- Validates column count and numeric formats
- Returns `List<SalesRecord>`


## 4. Stream API vs Alternatives

**Decision**: Use Java Streams API exclusively for all data operations

**Rationale**:
- Assignment explicitly requires "Stream operations"
- Demonstrates functional programming paradigms
- Clean, declarative code
- Efficient lazy evaluation

**Implementation**:
- All aggregations use Stream operations
- Terminal operations: `sum()`, `average()`, `count()`, `min()`, `max()`, `summaryStatistics()`, `collect()`
- Intermediate operations: `filter()`, `map()`, `distinct()`, `sorted()`, `limit()`
- Collectors: `groupingBy()`, `summingDouble()`, `averagingDouble()`, `counting()`

## 5. Aggregation Strategy

**Decision**: Comprehensive coverage of all aggregation types

**Rationale**:
- Assignment requires demonstrating "data aggregation"
- Covers all major aggregation operations: sum, average, count, min, max, summaryStatistics
- Shows different aggregation patterns (simple, grouped, filtered)

**Implementation**:

**Simple Aggregations:**
- `sum()` - Total sales, total quantity
- `average()` - Average order value, average quantity
- `count()` - Total transactions, unique products
- `min()` / `max()` - Lowest/highest transaction
- `summaryStatistics()` - Comprehensive statistics

**Grouped Aggregations:**
- `groupingBy()` with `summingDouble()` - Sales by category
- `groupingBy()` with `averagingDouble()` - Average sales by region
- `groupingBy()` with `counting()` - Transaction count by month

**Filtered Aggregations:**
- `filter()` + `sum()` - High-value transactions total
- `filter()` + `average()` - Average by category
- `filter()` + `summaryStatistics()` - Statistics by region


## 6. Functional Programming Approach

**Decision**: Pure functions, immutable data, no side effects

**Rationale**:
- Assignment requires "functional programming"
- Makes code more predictable and testable
- Aligns with Stream API philosophy
- Easier to reason about and debug

**Implementation**:
- All analyzer methods are pure functions 
- Input data is immutable
- No mutable state in analyzer class
- Lambda expressions for predicates, functions, comparators
- Method references where appropriate (`SalesRecord::getCategory`)

**Key Principles Applied**:
1. **Immutability**: Analyzer creates defensive copy of input list
2. **Pure Functions**: Methods only depend on input, no external state
3. **Function Composition**: Chain operations (filter → map → collect)
4. **Higher-Order Functions**: Methods accept functions as parameters (e.g., `Comparator`)

## 7. Testing Strategy

**Decision**: Demo application exercises all analysis methods on CSV data

**Rationale**:
- Assignment requires "Results of all analyses printed to console"
- Demo application (`SalesAnalysisDemo`) executes all analysis methods
- Real-world testing with actual CSV data demonstrates functionality
- All testing objectives are demonstrated through the demo output

**Implementation**:
- `SalesAnalysisDemo` loads CSV data and calls all analysis methods
- Results are printed to console in formatted output
- Demonstrates all Stream API operations and functional programming concepts
- Shows actual results from real sales data

**Testing Objectives Demonstrated**:
- **Functional Programming**: All methods are pure functions with immutable data
- **Stream Operations**: filter, map, collect, reduce, distinct operations shown
- **Data Aggregation**: sum, average, count, min, max, summaryStatistics demonstrated
- **Lambda Expressions**: Predicates, functions, comparators used throughout


## 8. Project Organization

**Decision**: Clear separation of concerns with core classes and demo application

**Rationale**:
- Clear organization: core classes and demo application
- Easy to navigate and understand
- Focus on demonstrating functionality

**Structure**:
```
Project2/
├── src/
│   ├── SalesRecord.java          # Data model
│   ├── CSVReader.java            # Utility
│   ├── SalesDataAnalyzer.java    # Core business logic
│   └── demo/                     # Demo application
│       └── SalesAnalysisDemo.java
├── data/                         # CSV data files
├── bin/                          # Compiled classes
├── compile.sh                    # Build script
├── README.md                     # User documentation
└── DESIGN_DECISIONS.md           # This file
```

**Key Decisions**:
1. **Core vs Demo**: Core classes in `src/`, demo in `src/demo/`
2. **Data**: CSV files in `data/` directory
3. **Scripts**: Build script at root level


## 9. CSV Data Structure

**Decision**: Realistic sales dataset with 150+ records covering multiple dimensions

**Rationale**:
- Assignment says "Select or construct a CSV dataset that you feel best fits the problem"
- Need enough data for meaningful analysis
- Should cover all dimensions: dates (multiple months), products, categories, regions, salespersons
- Realistic data makes results more meaningful

**Dataset Characteristics**:
- **Size**: 138 records 
- **Time Span**: 12 months (January - December 2024)
- **Categories**: 3 categories (Electronics, Furniture, Office Supplies)
- **Regions**: 4 regions (West, East, South, North)
- **Salespersons**: 4 salespersons (Alice Johnson, Bob Smith, Carol White, David Brown)
- **Price Range**: $2.99 to $2,999.99
- **Quantity Range**: 1 to 20 units

**Field Choices**:
- **date**: YYYY-MM-DD format (ISO 8601, sortable)
- **product**: Descriptive product names
- **category**: Broad categories for meaningful grouping
- **quantity**: Varied quantities to test quantity-based aggregations
- **price**: Realistic price points
- **region**: Geographic regions for regional analysis
- **salesperson**: Individual salespersons for performance analysis


## Additional Design Considerations

### Error Handling
- CSV reader skips invalid lines with warnings (graceful degradation)
- Aggregation methods handle empty data (return 0 or null appropriately)
- No exceptions thrown for business logic (only for I/O errors)

### Performance
- Stream operations are lazy and efficient
- No unnecessary intermediate collections
- Could be parallelized with `.parallelStream()` if needed (not done for simplicity)

### Extensibility
- Easy to add new analysis methods (follow existing patterns)
- CSV structure can be extended (add columns, update parser)
- Analyzer is independent of data source (could read from database)

---

## Conclusion

This implementation demonstrates:
-  Stream API operations (filter, map, collect, reduce)
-  Functional programming (pure functions, immutability, lambdas)
-  Data aggregation (sum, average, count, min, max, summaryStatistics)
-  Lambda expressions (predicates, functions, comparators)
-  Grouping operations with various aggregations
-  Comprehensive demonstration through demo application
-  Clean, maintainable code structure

