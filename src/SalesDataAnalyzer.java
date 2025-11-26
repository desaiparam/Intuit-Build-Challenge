import java.util.*;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;

//Main analysis class demonstrating Stream API operations
public class SalesDataAnalyzer {
    private final List<SalesRecord> records;
    
    // Constructor to initialize analyzer with sales records

    public SalesDataAnalyzer(List<SalesRecord> records) {
        // Create defensive copy to ensure immutability
        this.records = new ArrayList<>(records);
    }
    
    // ==================== DATA AGGREGATION METHODS ====================
    
     // Calculate total sales revenue across all transactions using mapToDouble().sum()
    public double calculateTotalSales() {
        return records.stream()
                .mapToDouble(SalesRecord::getTotalRevenue)
                .sum();
    }
    
    // Calculate average order value across all transactions using mapToDouble().average()
    public double calculateAverageOrderValue() {
        return records.stream()
                .mapToDouble(SalesRecord::getTotalRevenue)
                .average()
                .orElse(0.0);
    }
    
    // Get total number of transactions using count()
    public long getTotalTransactions() {
        return records.stream()
                .count();
    }
    
    // Get the highest value transaction using max() with Comparator
    public SalesRecord getHighestTransaction() {
        return records.stream()
                .max(Comparator.comparingDouble(SalesRecord::getTotalRevenue))
                .orElse(null);
    }
    
    // Get the lowest value transaction using min() with Comparator
    public SalesRecord getLowestTransaction() {
        return records.stream()
                .min(Comparator.comparingDouble(SalesRecord::getTotalRevenue))
                .orElse(null);
    }
    
    // Get comprehensive sales statistics using summaryStatistics()
    public DoubleSummaryStatistics getSalesStatistics() {
        return records.stream()
                .mapToDouble(SalesRecord::getTotalRevenue)
                .summaryStatistics();
    }
    
    // ==================== GROUPING OPERATIONS ====================
    
    // Get total sales grouped by product category using Collectors.groupingBy() and summingDouble()
    public Map<String, Double> getSalesByCategory() {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getCategory,
                        Collectors.summingDouble(SalesRecord::getTotalRevenue)
                ));
    }
    
    // Get average sales grouped by region using Collectors.groupingBy() and averagingDouble()
    public Map<String, Double> getAverageSalesByRegion() {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.averagingDouble(SalesRecord::getTotalRevenue)
                ));
    }
    
    // Get transaction count grouped by month using Collectors.groupingBy() and counting()
    public Map<String, Long> getTransactionCountByMonth() {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getMonth,
                        Collectors.counting()
                ));
    }
    
    // ==================== TOP N QUERIES ====================
    
    // Get top N products by total revenue using Grouping, sum aggregation, sorting, and limiting
    public List<Map.Entry<String, Double>> getTopProductsByRevenue(int n) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getProduct,
                        Collectors.summingDouble(SalesRecord::getTotalRevenue)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
    
    // ==================== FILTERED AGGREGATIONS ====================
    
    // Get total sales for high-value transactions above a threshold using Filtering with predicate lambda, then sum aggregation
    public double getHighValueTransactionsTotal(double threshold) {
        return records.stream()
                .filter(record -> record.getTotalRevenue() > threshold)
                .mapToDouble(SalesRecord::getTotalRevenue)
                .sum();
    }
    
    // Get average order value for a specific category using Filtering with predicate lambda, then average aggregation
    public double getAverageOrderValueByCategory(String category) {
        return records.stream()
                .filter(record -> record.getCategory().equalsIgnoreCase(category))
                .mapToDouble(SalesRecord::getTotalRevenue)
                .average()
                .orElse(0.0);
    }
    
    // Get total quantity sold across all transactions using Sum aggregation on integer field using mapToInt().sum()
    public int getTotalQuantitySold() {
        return records.stream()
                .mapToInt(SalesRecord::getQuantity)
                .sum();
    }
    
    // Get average quantity per transaction using Average aggregation on integer field using mapToInt().average()
    public double getAverageQuantityPerTransaction() {
        return records.stream()
                .mapToInt(SalesRecord::getQuantity)
                .average()
                .orElse(0.0);
    }
    
    // Get number of unique products using Distinct operation with count aggregation
    public long getUniqueProductCount() {
        return records.stream()
                .map(SalesRecord::getProduct)
                .distinct()
                .count();
    }
    
    // Get sales grouped by salesperson with total revenue using Grouping with sum aggregation
    public Map<String, Double> getSalesBySalesperson() {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getSalesperson,
                        Collectors.summingDouble(SalesRecord::getTotalRevenue)
                ));
    }
    
    // Get top N regions by total sales using Grouping, sum aggregation, sorting, and limiting
    public List<Map.Entry<String, Double>> getTopRegionsBySales(int n) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.summingDouble(SalesRecord::getTotalRevenue)
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }
    
    // Get sales statistics for a specific region using Filtering with predicate lambda, then summaryStatistics aggregation
    public DoubleSummaryStatistics getSalesStatisticsByRegion(String region) {
        return records.stream()
                .filter(record -> record.getRegion().equalsIgnoreCase(region))
                .mapToDouble(SalesRecord::getTotalRevenue)
                .summaryStatistics();
    }
}

