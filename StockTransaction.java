package org.example;

public class StockTransaction {

    private int    transactionId;
    private int    productId;
    private int    userId;
    private String transactionType; // "stock_in" | "stock_out"
    private int    quantity;
    private String reason;
    private String transactionDate;

    public StockTransaction() {}

    public StockTransaction(int transactionId, int productId, int userId,
                            String transactionType, int quantity,
                            String reason, String transactionDate) {
        this.transactionId   = transactionId;
        this.productId       = productId;
        this.userId          = userId;
        this.transactionType = transactionType;
        this.quantity        = quantity;
        this.reason          = reason;
        this.transactionDate = transactionDate;
    }

    public void display() {
        System.out.println("Transaction ID   : " + transactionId);
        System.out.println("Product ID       : " + productId);
        System.out.println("Staff ID         : " + userId);
        System.out.println("Type             : " + transactionType);
        System.out.println("Quantity         : " + quantity);
        System.out.println("Reason           : " + reason);
        System.out.println("Date             : " + transactionDate);
        System.out.println("---------------------------");
    }

    // Getters & Setters
    public int    getTransactionId()                              { return transactionId; }
    public void   setTransactionId(int transactionId)            { this.transactionId = transactionId; }

    public int    getProductId()                                  { return productId; }
    public void   setProductId(int productId)                     { this.productId = productId; }

    public int    getUserId()                                     { return userId; }
    public void   setUserId(int userId)                           { this.userId = userId; }

    public String getTransactionType()                            { return transactionType; }
    public void   setTransactionType(String transactionType)      { this.transactionType = transactionType; }

    public int    getQuantity()                                   { return quantity; }
    public void   setQuantity(int quantity)                       { this.quantity = quantity; }

    public String getReason()                                     { return reason; }
    public void   setReason(String reason)                        { this.reason = reason; }

    public String getTransactionDate()                            { return transactionDate; }
    public void   setTransactionDate(String transactionDate)      { this.transactionDate = transactionDate; }
}
