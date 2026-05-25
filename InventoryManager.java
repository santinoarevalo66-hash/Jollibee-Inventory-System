package org.example;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;

public class InventoryManager {

    private final Repository repo;
    private final Scanner scanner;

    public InventoryManager(Repository repo, Scanner scanner) {
        this.repo = repo;
        this.scanner = scanner;
    }

    // ─────────────────────────────────────────
    // ADD PRODUCT
    // ─────────────────────────────────────────

    public void addProduct() {

        System.out.println("\n===== ADD PRODUCT =====");

        System.out.print("Product Name: ");
        String name = scanner.nextLine();

        repo.printCategoriesTable();

        System.out.print("Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        if (!repo.categoryExists(categoryId)) {
            System.out.println("Category does not exist.");
            return;
        }

        System.out.print("Current Stock: ");
        int currentStock = Integer.parseInt(scanner.nextLine());

        System.out.print("Minimum Stock: ");
        int minimumStock = Integer.parseInt(scanner.nextLine());

        System.out.print("Unit: ");
        String unit = scanner.nextLine();

        System.out.print("Cost Price: ");
        double costPrice = Double.parseDouble(scanner.nextLine());

        repo.insertProduct(
                name,
                categoryId,
                currentStock,
                minimumStock,
                unit,
                costPrice,
                "active"
        );
    }

    // ─────────────────────────────────────────
    // EDIT PRODUCT
    // ─────────────────────────────────────────

    public void editProduct() {

        repo.printProductsTable();

        System.out.print("Enter Product ID to edit: ");
        int productId = Integer.parseInt(scanner.nextLine());

        if (!repo.productExists(productId)) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("New Product Name: ");
        String name = scanner.nextLine();

        repo.printCategoriesTable();

        System.out.print("New Category ID: ");
        int categoryId = Integer.parseInt(scanner.nextLine());

        System.out.print("New Minimum Stock: ");
        int minimumStock = Integer.parseInt(scanner.nextLine());

        System.out.print("New Unit: ");
        String unit = scanner.nextLine();

        System.out.print("New Cost Price: ");
        double costPrice = Double.parseDouble(scanner.nextLine());

        repo.updateProduct(
                productId,
                name,
                categoryId,
                minimumStock,
                unit,
                costPrice
        );
    }

    // ─────────────────────────────────────────
    // DELETE PRODUCT
    // ─────────────────────────────────────────

    public void deleteProduct() {

        repo.printProductsTable();

        System.out.print("Enter Product ID to delete: ");
        int productId = Integer.parseInt(scanner.nextLine());

        if (!repo.productExists(productId)) {
            System.out.println("Product not found.");
            return;
        }

        repo.deactivateProduct(productId);
    }

    // ─────────────────────────────────────────
    // ADD CATEGORY
    // ─────────────────────────────────────────

    public void addCategory() {

        System.out.println("\n===== ADD CATEGORY =====");

        System.out.print("Category Name: ");
        String name = scanner.nextLine();

        System.out.print("Description: ");
        String description = scanner.nextLine();

        repo.insertCategory(name, description);
    }

    // ─────────────────────────────────────────
    // STOCK IN
    // ─────────────────────────────────────────

    public void recordStockIn(int userId) {

        repo.printProductsTable();

        System.out.print("Product ID: ");
        int productId = Integer.parseInt(scanner.nextLine());

        if (!repo.productExists(productId)) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Quantity to add: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        int current = repo.getCurrentStock(productId);

        repo.updateStock(productId, current + quantity);

        repo.insertTransaction(
                productId,
                userId,
                "stock_in",
                quantity,
                reason,
                LocalDate.now().toString()
        );

        System.out.println("Stock-in recorded successfully.");
    }

    // ─────────────────────────────────────────
    // STOCK OUT
    // ─────────────────────────────────────────

    public void recordStockOut(int userId) {

        repo.printProductsTable();

        System.out.print("Product ID: ");
        int productId = Integer.parseInt(scanner.nextLine());

        if (!repo.productExists(productId)) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Quantity to deduct: ");
        int quantity = Integer.parseInt(scanner.nextLine());

        int current = repo.getCurrentStock(productId);

        if (quantity > current) {
            System.out.println("Not enough stock.");
            return;
        }

        System.out.print("Reason: ");
        String reason = scanner.nextLine();

        repo.updateStock(productId, current - quantity);

        repo.insertTransaction(
                productId,
                userId,
                "stock_out",
                quantity,
                reason,
                LocalDate.now().toString()
        );

        System.out.println("Stock-out recorded successfully.");
    }

    // ─────────────────────────────────────────
    // SEARCH PRODUCT
    // ─────────────────────────────────────────

    public void searchProduct() {

        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine();

        ResultSet rs = repo.searchProducts(keyword);

        try {

            boolean found = false;

            while (rs != null && rs.next()) {

                found = true;

                System.out.println("\n========================");
                System.out.println("Product ID : " + rs.getInt("productId"));
                System.out.println("Name       : " + rs.getString("productName"));
                System.out.println("Category   : " + rs.getString("categoryName"));
                System.out.println("Stock      : " + rs.getInt("currentStock"));
                System.out.println("Unit       : " + rs.getString("unit"));
                System.out.println("========================");
            }

            if (!found) {
                System.out.println("No products found.");
            }

        } catch (SQLException e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // VIEW MY TRANSACTIONS
    // ─────────────────────────────────────────

    public void viewMyTransactions(int userId) {

        ResultSet rs = repo.getTransactionsByUser(userId);

        try {

            boolean found = false;

            while (rs != null && rs.next()) {

                found = true;

                System.out.println("\n========================");
                System.out.println("Transaction ID : " + rs.getInt("transactionId"));
                System.out.println("Product        : " + rs.getString("productName"));
                System.out.println("Type           : " + rs.getString("transactionType"));
                System.out.println("Quantity       : " + rs.getInt("quantity"));
                System.out.println("Reason         : " + rs.getString("reason"));
                System.out.println("Date           : " + rs.getString("transactionDate"));
                System.out.println("========================");
            }

            if (!found) {
                System.out.println("No transaction history.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
