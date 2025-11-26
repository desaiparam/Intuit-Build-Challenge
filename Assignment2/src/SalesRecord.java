//Data model representing a single sales transaction.
public class SalesRecord {
    private String date;
    private String product;
    private String category;
    private int quantity;
    private double price;
    private String region;
    private String salesperson;
    
    //Constructor to create a SalesRecord with all fields.

    public SalesRecord(String date, String product, String category, 
                      int quantity, double price, String region, String salesperson) {
        this.date = date;
        this.product = product;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.region = region;
        this.salesperson = salesperson;
    }
    
    // Getters
    public String getDate() {
        return date;
    }
    
    public String getProduct() {
        return product;
    }
    
    public String getCategory() {
        return category;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public String getRegion() {
        return region;
    }
    
    public String getSalesperson() {
        return salesperson;
    }
    
    // Calculate the total revenue for this record (price * quantity).
    public double getTotalRevenue() {
        return price * quantity;
    }
    
    // Extract month from date string (assumes YYYY-MM-DD format).
    public String getMonth() {
        if (date != null && date.length() >= 7) {
            return date.substring(0, 7); // "YYYY-MM"
        }
        return "";
    }
    
    @Override
    public String toString() {
        return String.format("SalesRecord{date='%s', product='%s', category='%s', " +
                           "quantity=%d, price=%.2f, region='%s', salesperson='%s', revenue=%.2f}",
                           date, product, category, quantity, price, region, salesperson, getTotalRevenue());
    }
}

