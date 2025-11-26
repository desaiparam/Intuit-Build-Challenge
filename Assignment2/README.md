# Sales Data Analysis Application

## Overview

This project implements a **Sales Data Analysis application** in Java, demonstrating proficiency with the Streams API by performing various aggregation and grouping operations on sales data provided in CSV format. The program reads data from a CSV file and executes multiple analytical queries using functional programming paradigms.

## Features

- **CSV Data Loading** - Reads and parses sales data from CSV files
- **Data Aggregations** - Sum, average, count, min, max, and summary statistics
- **Grouping Operations** - Group by category, region, month with various aggregations
- **Top N Queries** - Find top products and regions by revenue
- **Filtered Aggregations** - Apply filters and perform aggregations on subsets
- **Functional Programming** - Pure functions, immutable data, Stream API operations

## Setup Instructions

### Requirements
- Java JDK 8 or higher
- No external dependencies (pure Java)

### Compile
```bash
cd Project2
./compile.sh
```

Or manually:
```bash
javac -d bin src/*.java src/demo/*.java
```

### Run Demo
```bash
java -cp bin SalesAnalysisDemo
```

Note: The demo expects `data/sales_data.csv` to be in the Project2 directory. Run from the Project2 directory.

## Project Structure

```
Project2/
├── src/                          # Source files
│   ├── SalesRecord.java          # Data model (POJO)
│   ├── CSVReader.java            # CSV parsing utility
│   ├── SalesDataAnalyzer.java    # Main analysis class
│   └── demo/                     # Demo application
│       └── SalesAnalysisDemo.java
├── data/                         # CSV data files
│   └── sales_data.csv
├── bin/                          # Compiled files
├── compile.sh                    # Compilation script
├── README.md
└── DESIGN_DECISIONS.md           # Detailed design documentation
```

## CSV Data Format

The CSV file should have the following structure:

```csv
date,product,category,quantity,price,region,salesperson
2024-01-15,Laptop Pro,Electronics,2,1299.99,West,Alice Johnson
2024-01-18,Wireless Mouse,Electronics,5,29.99,East,Bob Smith
...
```

**Columns:**
- `date`: Date of sale (format: YYYY-MM-DD)
- `product`: Product name
- `category`: Product category
- `quantity`: Number of units sold
- `price`: Price per unit
- `region`: Sales region
- `salesperson`: Name of salesperson

## Analysis Methods

### Basic Aggregations
- `calculateTotalSales()` - Total revenue across all transactions
- `calculateAverageOrderValue()` - Average order value
- `getTotalTransactions()` - Total number of transactions
- `getHighestTransaction()` - Transaction with maximum revenue
- `getLowestTransaction()` - Transaction with minimum revenue
- `getSalesStatistics()` - Comprehensive statistics (min, max, avg, sum, count)

### Grouping Operations
- `getSalesByCategory()` - Total sales grouped by product category
- `getAverageSalesByRegion()` - Average sales grouped by region
- `getTransactionCountByMonth()` - Transaction count grouped by month

### Top N Queries
- `getTopProductsByRevenue(int n)` - Top N products by total revenue
- `getTopRegionsBySales(int n)` - Top N regions by total sales

### Filtered Aggregations
- `getHighValueTransactionsTotal(double threshold)` - Total sales for transactions above threshold
- `getAverageOrderValueByCategory(String category)` - Average order value for specific category
- `getSalesStatisticsByRegion(String region)` - Statistics for specific region

### Additional Analyses
- `getTotalQuantitySold()` - Total quantity sold
- `getAverageQuantityPerTransaction()` - Average quantity per transaction
- `getUniqueProductCount()` - Number of unique products
- `getSalesBySalesperson()` - Sales grouped by salesperson

## Sample Output

```
=== Sales Data Analysis Demo ===

Loaded 138 sales records from data/sales_data.csv

--- BASIC AGGREGATIONS ---
Total Sales Revenue: $75,930.65
Average Order Value: $550.22
Total Transactions: 138
Highest Transaction: Executive Desk Ultra - $2,999.99
Lowest Transaction: Desk Reference Guide - $26.97

Sales Statistics Summary:
  Count: 138
  Sum: $75,930.65
  Min: $26.97
  Max: $2,999.99
  Average: $550.22

--- GROUPING OPERATIONS ---

Sales by Category:
  Furniture: $40,723.29
  Electronics: $28,958.06
  Office Supplies: $6,249.30

Average Sales by Region:
  East: $601.33
  South: $562.75
  West: $528.66
  North: $507.28

Transaction Count by Month:
  2024-01: 6 transactions
  2024-02: 12 transactions
  2024-03: 12 transactions
  ...

--- TOP N QUERIES ---

Top 5 Products by Revenue:
  1. Executive Desk Ultra: $2,999.99
  2. Laptop Pro: $2,599.98
  3. Executive Office Ultra: $2,499.99
  4. Executive Office Set: $1,999.99
  5. Tablet Max: $1,999.98

Top 3 Regions by Sales:
  1. East: $21,046.67
  2. South: $19,133.46
  3. West: $18,503.02

--- FILTERED AGGREGATIONS ---

Total Sales for Transactions > $1,000.00: $36,099.71

Average Order Value by Category:
  Electronics: $590.98
  Furniture: $969.60
  Office Supplies: $132.96

...
```

## Testing Objectives Demonstrated

The demo application (`SalesAnalysisDemo`) exercises all analysis methods on the CSV data, demonstrating:

-  **Functional Programming** - Pure functions, immutability
-  **Stream Operations** - filter, map, collect, reduce, distinct
-  **Data Aggregation** - sum, average, count, min, max, summaryStatistics
-  **Lambda Expressions** - Predicates, functions, comparators

All analysis methods are executed and results are printed to the console, providing comprehensive demonstration of the Stream API operations and functional programming paradigms.

## Key Design Decisions

For detailed design information, see [DESIGN_DECISIONS.md](DESIGN_DECISIONS.md).

## Assumptions

- CSV file format matches the expected structure (7 columns)
- Date format is YYYY-MM-DD
- All numeric fields (quantity, price) are valid numbers
- CSV file exists and is readable
- No null values in required fields
- All analysis methods handle empty data gracefully

