package org.example;

public class Product {

    private int    productId;
    private String productName;
    private int    categoryId;
    private String categoryName; // joined field
    private int    currentStock;
    private int    minimumStock;
    private String unit;
    private double costPrice;
    private String status;       // "active" | "inactive"

    // ─────────────────────────────────────────
    // CONSTRUCTORS
    // ─────────────────────────────────────────

    public Product() {}

    public Product(int productId, String productName, int categoryId,
                   int currentStock, int minimumStock,
                   String unit, double costPrice, String status) {
        this.productId    = productId;
        this.productName  = productName;
        this.categoryId   = categoryId;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.unit         = unit;
        this.costPrice    = costPrice;
        this.status       = status;
    }

    // ─────────────────────────────────────────
    // DISPLAY
    // ─────────────────────────────────────────

    public void display() {
        System.out.println("Product ID    : " + productId);
        System.out.println("Product Name  : " + productName);
        System.out.println("Category      : " + (categoryName != null ? categoryName : categoryId));
        System.out.println("Current Stock : " + currentStock + " " + unit);
        System.out.println("Minimum Stock : " + minimumStock + " " + unit);
        System.out.printf( "Cost Price    : PHP %.2f%n", costPrice);
        System.out.println("Status        : " + status);
        System.out.println("---------------------------");
    }

    public boolean isLowStock() {
        return currentStock <= minimumStock;
    }

    // ─────────────────────────────────────────
    // GETTERS & SETTERS
    // ─────────────────────────────────────────

    public int    getProductId()                        { return productId; }
    public void   setProductId(int productId)           { this.productId = productId; }

    public String getProductName()                      { return productName; }
    public void   setProductName(String productName)    { this.productName = productName; }

    public int    getCategoryId()                       { return categoryId; }
    public void   setCategoryId(int categoryId)         { this.categoryId = categoryId; }

    public String getCategoryName()                     { return categoryName; }
    public void   setCategoryName(String categoryName)  { this.categoryName = categoryName; }

    public int    getCurrentStock()                     { return currentStock; }
    public void   setCurrentStock(int currentStock)     { this.currentStock = currentStock; }

    public int    getMinimumStock()                     { return minimumStock; }
    public void   setMinimumStock(int minimumStock)     { this.minimumStock = minimumStock; }

    public String getUnit()                             { return unit; }
    public void   setUnit(String unit)                  { this.unit = unit; }

    public double getCostPrice()                        { return costPrice; }
    public void   setCostPrice(double costPrice)        { this.costPrice = costPrice; }

    public String getStatus()                           { return status; }
    public void   setStatus(String status)              { this.status = status; }
}
