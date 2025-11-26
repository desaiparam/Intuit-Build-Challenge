import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//Utility class for reading and parsing CSV files into SalesRecord objects.
public class CSVReader {
    

    //Read a CSV file and parse it into a list of SalesRecord objects.
    public static List<SalesRecord> read(String filePath) throws IOException {
        List<SalesRecord> records = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Skip header row
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }
            
            String line;
            int lineNumber = 1; // Start at 1 since we skipped header
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                try {
                    SalesRecord record = parseLine(line);
                    records.add(record);
                } catch (Exception e) {
                    System.err.println("Warning: Skipping invalid line " + lineNumber + ": " + e.getMessage());
                }
            }
        }
        
        return records;
    }
    
    //Parse a single CSV line into a SalesRecord object.
    private static SalesRecord parseLine(String line) {
        String[] parts = line.split(",");
        
        if (parts.length != 7) {
            throw new IllegalArgumentException("Expected 7 columns, found " + parts.length);
        }
        
        try {
            String date = parts[0].trim();
            String product = parts[1].trim();
            String category = parts[2].trim();
            int quantity = Integer.parseInt(parts[3].trim());
            double price = Double.parseDouble(parts[4].trim());
            String region = parts[5].trim();
            String salesperson = parts[6].trim();
            
            return new SalesRecord(date, product, category, quantity, price, region, salesperson);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + e.getMessage());
        }
    }
}

