package demo;

import java.io.IOException;
import java.util.*;
import java.util.DoubleSummaryStatistics;

//Demo application that loads CSV data and executes all analysis methods, printing formatted results to console.
public class SalesAnalysisDemo {
    
    public static void main(String[] args) {
        System.out.println("=== Sales Data Analysis Demo ===\n");
        
        // Load data from CSV
        List<SalesRecord> records;
        try {
            String csvPath = "data/sales_data.csv";
            records = CSVReader.read(csvPath);
            System.out.println("Loaded " + records.size() + " sales records from " + csvPath + "\n");
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return;
        }
        
        // Create analyzer
        SalesDataAnalyzer analyzer = new SalesDataAnalyzer(records);
        
        // Execute all analyses and print results
        printBasicAggregations(analyzer);
        printGroupingOperations(analyzer);
        printTopNQueries(analyzer);
        printFilteredAggregations(analyzer);
        printAdditionalAnalyses(analyzer);
        
        System.out.println("\n=== Analysis Complete ===");
    }
    
    // Print basic aggregation results
    private static void printBasicAggregations(SalesDataAnalyzer analyzer) {
        System.out.println("--- BASIC AGGREGATIONS ---");
        
        double totalSales = analyzer.calculateTotalSales();
        System.out.printf("Total Sales Revenue: $%,.2f%n", totalSales);
        
        double avgOrderValue = analyzer.calculateAverageOrderValue();
        System.out.printf("Average Order Value: $%,.2f%n", avgOrderValue);
        
        long totalTransactions = analyzer.getTotalTransactions();
        System.out.printf("Total Transactions: %,d%n", totalTransactions);
        
        SalesRecord highest = analyzer.getHighestTransaction();
        if (highest != null) {
            System.out.printf("Highest Transaction: %s - $%,.2f%n", 
                highest.getProduct(), highest.getTotalRevenue());
        }
        
        SalesRecord lowest = analyzer.getLowestTransaction();
        if (lowest != null) {
            System.out.printf("Lowest Transaction: %s - $%,.2f%n", 
                lowest.getProduct(), lowest.getTotalRevenue());
        }
        
        DoubleSummaryStatistics stats = analyzer.getSalesStatistics();
        System.out.println("\nSales Statistics Summary:");
        System.out.printf("  Count: %,d%n", stats.getCount());
        System.out.printf("  Sum: $%,.2f%n", stats.getSum());
        System.out.printf("  Min: $%,.2f%n", stats.getMin());
        System.out.printf("  Max: $%,.2f%n", stats.getMax());
        System.out.printf("  Average: $%,.2f%n", stats.getAverage());
        
        System.out.println();
    }
    
    // Print grouping operation results
    private static void printGroupingOperations(SalesDataAnalyzer analyzer) {
        System.out.println("--- GROUPING OPERATIONS ---");
        
        // Sales by Category
        Map<String, Double> salesByCategory = analyzer.getSalesByCategory();
        System.out.println("\nSales by Category:");
        salesByCategory.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("  %s: $%,.2f%n", entry.getKey(), entry.getValue())
            );
        
        // Average Sales by Region
        Map<String, Double> avgByRegion = analyzer.getAverageSalesByRegion();
        System.out.println("\nAverage Sales by Region:");
        avgByRegion.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("  %s: $%,.2f%n", entry.getKey(), entry.getValue())
            );
        
        // Transaction Count by Month
        Map<String, Long> countByMonth = analyzer.getTransactionCountByMonth();
        System.out.println("\nTransaction Count by Month:");
        countByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                System.out.printf("  %s: %,d transactions%n", entry.getKey(), entry.getValue())
            );
        
        System.out.println();
    }
    
    // Print Top N query results
    private static void printTopNQueries(SalesDataAnalyzer analyzer) {
        System.out.println("--- TOP N QUERIES ---");
        
        // Top 5 Products by Revenue
        List<Map.Entry<String, Double>> topProducts = analyzer.getTopProductsByRevenue(5);
        System.out.println("\nTop 5 Products by Revenue:");
        for (int i = 0; i < topProducts.size(); i++) {
            Map.Entry<String, Double> entry = topProducts.get(i);
            System.out.printf("  %d. %s: $%,.2f%n", i + 1, entry.getKey(), entry.getValue());
        }
        
        // Top 3 Regions by Sales
        List<Map.Entry<String, Double>> topRegions = analyzer.getTopRegionsBySales(3);
        System.out.println("\nTop 3 Regions by Sales:");
        for (int i = 0; i < topRegions.size(); i++) {
            Map.Entry<String, Double> entry = topRegions.get(i);
            System.out.printf("  %d. %s: $%,.2f%n", i + 1, entry.getKey(), entry.getValue());
        }
        
        System.out.println();
    }
    
    // Print filtered aggregation results
    private static void printFilteredAggregations(SalesDataAnalyzer analyzer) {
        System.out.println("--- FILTERED AGGREGATIONS ---");
        
        // High-value transactions (> $1000)
        double threshold = 1000.0;
        double highValueTotal = analyzer.getHighValueTransactionsTotal(threshold);
        System.out.printf("\nTotal Sales for Transactions > $%,.2f: $%,.2f%n", 
            threshold, highValueTotal);
        
        // Average order value by category
        String[] categories = {"Electronics", "Furniture", "Office Supplies"};
        System.out.println("\nAverage Order Value by Category:");
        for (String category : categories) {
            double avg = analyzer.getAverageOrderValueByCategory(category);
            System.out.printf("  %s: $%,.2f%n", category, avg);
        }
        
        // Sales statistics by region
        String[] regions = {"West", "East", "South", "North"};
        System.out.println("\nSales Statistics by Region:");
        for (String region : regions) {
            DoubleSummaryStatistics stats = analyzer.getSalesStatisticsByRegion(region);
            System.out.printf("  %s: Count=%,d, Sum=$%,.2f, Avg=$%,.2f, Min=$%,.2f, Max=$%,.2f%n",
                region, stats.getCount(), stats.getSum(), stats.getAverage(), 
                stats.getMin(), stats.getMax());
        }
        
        System.out.println();
    }
    
    // Print additional analysis results
    private static void printAdditionalAnalyses(SalesDataAnalyzer analyzer) {
        System.out.println("--- ADDITIONAL ANALYSES ---");
        
        int totalQuantity = analyzer.getTotalQuantitySold();
        System.out.printf("Total Quantity Sold: %,d units%n", totalQuantity);
        
        double avgQuantity = analyzer.getAverageQuantityPerTransaction();
        System.out.printf("Average Quantity per Transaction: %.2f units%n", avgQuantity);
        
        long uniqueProducts = analyzer.getUniqueProductCount();
        System.out.printf("Number of Unique Products: %,d%n", uniqueProducts);
        
        Map<String, Double> salesByPerson = analyzer.getSalesBySalesperson();
        System.out.println("\nSales by Salesperson:");
        salesByPerson.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> 
                System.out.printf("  %s: $%,.2f%n", entry.getKey(), entry.getValue())
            );
        
        System.out.println();
    }
}

