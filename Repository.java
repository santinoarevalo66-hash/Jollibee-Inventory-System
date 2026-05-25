package org.example;

import java.sql.*;

public class Repository {

    // Change path as needed for your machine
    private static final String DB_URL = "jdbc:sqlite:C:JollibeeInventory.db";
    private Connection connection;

    // ─────────────────────────────────────────
    // CONNECTION
    // ─────────────────────────────────────────

    public void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to Jollibee Inventory database.");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // CREATE TABLES
    // ─────────────────────────────────────────

    public void createTables() {
        String users = """
            CREATE TABLE IF NOT EXISTS users (
                userId    INTEGER PRIMARY KEY AUTOINCREMENT,
                name      TEXT NOT NULL,
                email     TEXT NOT NULL UNIQUE,
                password  TEXT NOT NULL,
                role      TEXT NOT NULL DEFAULT 'staff'
            );
            """;

        String categories = """
            CREATE TABLE IF NOT EXISTS categories (
                categoryId   INTEGER PRIMARY KEY AUTOINCREMENT,
                categoryName TEXT NOT NULL UNIQUE,
                description  TEXT
            );
            """;

        String products = """
            CREATE TABLE IF NOT EXISTS products (
                productId    INTEGER PRIMARY KEY AUTOINCREMENT,
                productName  TEXT NOT NULL,
                categoryId   INTEGER NOT NULL,
                currentStock INTEGER NOT NULL DEFAULT 0,
                minimumStock INTEGER NOT NULL DEFAULT 10,
                unit         TEXT NOT NULL DEFAULT 'pcs',
                costPrice    REAL NOT NULL DEFAULT 0.0,
                status       TEXT NOT NULL DEFAULT 'active',
                FOREIGN KEY (categoryId) REFERENCES categories(categoryId)
            );
            """;

        String transactions = """
            CREATE TABLE IF NOT EXISTS stock_transactions (
                transactionId   INTEGER PRIMARY KEY AUTOINCREMENT,
                productId       INTEGER NOT NULL,
                userId          INTEGER NOT NULL,
                transactionType TEXT NOT NULL,
                quantity        INTEGER NOT NULL,
                reason          TEXT,
                transactionDate TEXT NOT NULL,
                FOREIGN KEY (productId) REFERENCES products(productId),
                FOREIGN KEY (userId)    REFERENCES users(userId)
            );
            """;

        String config = """
            CREATE TABLE IF NOT EXISTS app_config (
                key   TEXT PRIMARY KEY,
                value TEXT NOT NULL
            );
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(users);
            stmt.execute(categories);
            stmt.execute(products);
            stmt.execute(transactions);
            stmt.execute(config);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // SEED CATEGORIES
    // ─────────────────────────────────────────

    public void seedCategories() {
        String check = "SELECT COUNT(*) FROM categories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(check)) {
            if (rs.next() && rs.getInt(1) == 0) {
                insertCategory("Chicken",       "Fried and grilled chicken products");
                insertCategory("Burgers",        "Burger patties and buns");
                insertCategory("Beverages",      "Drinks and juices");
                insertCategory("Rice",           "Rice products and packaging");
                insertCategory("Condiments",     "Sauces, ketchup, and seasonings");
                insertCategory("Packaging",      "Boxes, wrappers, and containers");
                insertCategory("Fries & Sides",  "French fries and side products");
                insertCategory("Desserts",       "Ice cream, pies, and sweet items");
                System.out.println("Sample categories added.");
            }
        } catch (SQLException e) {
            System.out.println("Error seeding categories: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // SEED PRODUCTS
    // ─────────────────────────────────────────

    public void seedProducts() {
        String check = "SELECT COUNT(*) FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(check)) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Chicken (categoryId=1)
                insertProduct("Chicken Breast (Raw)",  1, 120, 30, "pcs",  45.00, "active");
                insertProduct("Chicken Thigh (Raw)",   1, 100, 30, "pcs",  40.00, "active");
                insertProduct("Chicken Drumstick",     1,  80, 20, "pcs",  35.00, "active");
                insertProduct("Breading Mix (5kg bag)",1,  20,  5, "bag", 350.00, "active");
                insertProduct("Cooking Oil (18L)",     1,  15,  3, "can", 900.00, "active");

                // Burgers (categoryId=2)
                insertProduct("Burger Patty (Frozen)", 2,  90, 25, "pcs",  55.00, "active");
                insertProduct("Burger Bun",            2, 100, 30, "pcs",  12.00, "active");
                insertProduct("Lettuce (kg)",          2,   8,  2, "kg",   80.00, "active");
                insertProduct("Cheese Slice",          2,  60, 20, "pcs",   8.00, "active");

                // Beverages (categoryId=3)
                insertProduct("Coke 12oz Cup",         3, 200, 50, "pcs",   8.00, "active");
                insertProduct("Sprite 12oz Cup",       3, 150, 50, "pcs",   8.00, "active");
                insertProduct("Pineapple Juice Syrup", 3,  10,  2, "bottle",400.00, "active");

                // Rice (categoryId=4)
                insertProduct("Rice (25kg sack)",      4,  30,  5, "sack", 1200.00, "active");
                insertProduct("Rice Packaging Box",    4, 200, 50, "pcs",   2.50, "active");

                // Condiments (categoryId=5)
                insertProduct("Jollibee Gravy (pack)", 5,  25,  8, "pack", 120.00, "active");
                insertProduct("Ketchup Sachet",        5, 500, 100,"pcs",   1.50, "active");
                insertProduct("Salt (1kg)",            5,  10,  3, "pack",  25.00, "active");

                // Packaging (categoryId=6)
                insertProduct("Chickenjoy Box",        6, 300, 80, "pcs",   4.00, "active");
                insertProduct("Burger Wrapper",        6, 200, 60, "pcs",   2.00, "active");
                insertProduct("Paper Bag (Large)",     6, 150, 40, "pcs",   6.00, "active");
                insertProduct("Drinking Straw",        6, 400, 100,"pcs",   0.50, "active");

                // Fries & Sides (categoryId=7)
                insertProduct("French Fries (2kg bag)",7,  40, 10, "bag", 280.00, "active");
                insertProduct("Corn Cup",              7, 100, 30, "pcs",  15.00, "active");
                insertProduct("Mashed Potato Mix",     7,  18,  5, "pack", 180.00, "active");

                // Desserts (categoryId=8)
                insertProduct("Ice Cream Mix (1L)",    8,  25,  6, "box",  200.00, "active");
                insertProduct("Peach Mango Pie",       8,  60, 15, "pcs",  22.00, "active");

                System.out.println("Sample products added.");
            }
        } catch (SQLException e) {
            System.out.println("Error seeding products: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // USER METHODS
    // ─────────────────────────────────────────

    public void insertUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.out.println("Error: Email already registered. Please use a different email.");
            } else {
                System.out.println("Error inserting user: " + e.getMessage());
            }
        }
    }

    public ResultSet getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
            return null;
        }
    }

    public String getAdminPasswordHash() {
        String sql = "SELECT value FROM app_config WHERE key = 'admin_password_hash'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getString("value");
        } catch (SQLException e) {
            System.out.println("Error fetching admin password: " + e.getMessage());
        }
        return null;
    }

    public void setAdminPasswordHash(String hash) {
        String sql = "INSERT INTO app_config (key, value) VALUES ('admin_password_hash', ?) " +
                "ON CONFLICT(key) DO UPDATE SET value = excluded.value";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving admin password: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // CATEGORY METHODS
    // ─────────────────────────────────────────

    public void insertCategory(String name, String description) {
        String sql = "INSERT INTO categories (categoryName, description) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting category: " + e.getMessage());
        }
    }

    public ResultSet getAllCategories() {
        String sql = "SELECT * FROM categories ORDER BY categoryId";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error retrieving categories: " + e.getMessage());
            return null;
        }
    }

    public boolean categoryExists(int categoryId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE categoryId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking category: " + e.getMessage());
        }
        return false;
    }

    // ─────────────────────────────────────────
    // PRODUCT METHODS
    // ─────────────────────────────────────────

    public void insertProduct(String name, int categoryId, int currentStock,
                              int minimumStock, String unit, double costPrice, String status) {
        String sql = "INSERT INTO products (productName, categoryId, currentStock, minimumStock, unit, costPrice, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setInt(3, currentStock);
            ps.setInt(4, minimumStock);
            ps.setString(5, unit);
            ps.setDouble(6, costPrice);
            ps.setString(7, status);
            ps.executeUpdate();
            System.out.println("Product added: " + name);
        } catch (SQLException e) {
            System.out.println("Error inserting product: " + e.getMessage());
        }
    }

    public ResultSet getAllProducts() {
        String sql = """
            SELECT p.*, c.categoryName
            FROM products p
            LEFT JOIN categories c ON p.categoryId = c.categoryId
            ORDER BY c.categoryName, p.productName
            """;
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error retrieving products: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getProductById(int productId) {
        String sql = """
            SELECT p.*, c.categoryName
            FROM products p
            LEFT JOIN categories c ON p.categoryId = c.categoryId
            WHERE p.productId = ?
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, productId);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
            return null;
        }
    }

    public ResultSet searchProducts(String keyword) {
        String sql = """
            SELECT p.*, c.categoryName
            FROM products p
            LEFT JOIN categories c ON p.categoryId = c.categoryId
            WHERE p.productName LIKE ? OR c.categoryName LIKE ?
            ORDER BY p.productName
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
            return null;
        }
    }

    public boolean productExists(int productId) {
        String sql = "SELECT COUNT(*) FROM products WHERE productId = ? AND status = 'active'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking product: " + e.getMessage());
        }
        return false;
    }

    public int getCurrentStock(int productId) {
        String sql = "SELECT currentStock FROM products WHERE productId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("currentStock");
        } catch (SQLException e) {
            System.out.println("Error retrieving stock: " + e.getMessage());
        }
        return 0;
    }

    public void updateStock(int productId, int newStock) {
        String sql = "UPDATE products SET currentStock = ? WHERE productId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
    }

    public void updateProduct(int productId, String name, int categoryId,
                              int minStock, String unit, double costPrice) {
        String sql = "UPDATE products SET productName=?, categoryId=?, minimumStock=?, unit=?, costPrice=? " +
                "WHERE productId=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, categoryId);
            ps.setInt(3, minStock);
            ps.setString(4, unit);
            ps.setDouble(5, costPrice);
            ps.setInt(6, productId);
            ps.executeUpdate();
            System.out.println("Product updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    public void deactivateProduct(int productId) {
        String sql = "UPDATE products SET status = 'inactive' WHERE productId = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
            System.out.println("Product deactivated.");
        } catch (SQLException e) {
            System.out.println("Error deactivating product: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // TRANSACTION METHODS
    // ─────────────────────────────────────────

    public void insertTransaction(int productId, int userId, String type,
                                  int quantity, String reason, String date) {
        String sql = "INSERT INTO stock_transactions (productId, userId, transactionType, quantity, reason, transactionDate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, userId);
            ps.setString(3, type);
            ps.setInt(4, quantity);
            ps.setString(5, reason);
            ps.setString(6, date);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error inserting transaction: " + e.getMessage());
        }
    }

    public ResultSet getTransactionsByUser(int userId) {
        String sql = """
            SELECT t.*, p.productName
            FROM stock_transactions t
            LEFT JOIN products p ON t.productId = p.productId
            WHERE t.userId = ?
            ORDER BY t.transactionId DESC
            """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getAllTransactions() {
        String sql = """
            SELECT t.*, p.productName, u.name AS staffName
            FROM stock_transactions t
            LEFT JOIN products p ON t.productId = p.productId
            LEFT JOIN users u    ON t.userId = u.userId
            ORDER BY t.transactionId DESC
            """;
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Error retrieving all transactions: " + e.getMessage());
            return null;
        }
    }

    // ─────────────────────────────────────────
    // TABLE DISPLAY METHODS
    // ─────────────────────────────────────────

    public void printProductsTable() {
        String sql = """
            SELECT p.*, c.categoryName
            FROM products p
            LEFT JOIN categories c ON p.categoryId = c.categoryId
            WHERE p.status = 'active'
            ORDER BY c.categoryName, p.productName
            """;
        System.out.println();
        System.out.println("╔════════╦══════════════════════════════╦══════════════════╦═══════════════╦══════════════╦════════╦══════════════════╗");
        System.out.println("║ Prod # ║ Product Name                 ║ Category         ║ Current Stock ║ Min Stock    ║ Unit   ║ Cost Price       ║");
        System.out.println("╠════════╬══════════════════════════════╬══════════════════╬═══════════════╬══════════════╬════════╬══════════════════╣");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                int    curr  = rs.getInt("currentStock");
                int    min   = rs.getInt("minimumStock");
                String alert = curr <= min ? " ⚠" : "  ";
                System.out.printf("║ %-6d ║ %-28s ║ %-16s ║ %-11d%s ║ %-12d ║ %-6s ║ PHP %-12.2f ║%n",
                        rs.getInt("productId"),
                        truncate(rs.getString("productName"), 28),
                        truncate(rs.getString("categoryName"), 16),
                        curr, alert,
                        min,
                        truncate(rs.getString("unit"), 6),
                        rs.getDouble("costPrice"));
            }
            if (!found) System.out.println("║                                       No products found.                                              ║");
        } catch (SQLException e) {
            System.out.println("Error printing products: " + e.getMessage());
        }
        System.out.println("╚════════╩══════════════════════════════╩══════════════════╩═══════════════╩══════════════╩════════╩══════════════════╝");
        System.out.println("  ⚠ = Below minimum stock level");
    }

    public void printCategoriesTable() {
        System.out.println();
        System.out.println("╔═════════════╦══════════════════════════╦══════════════════════════════════════════╗");
        System.out.println("║ Category ID ║ Category Name            ║ Description                              ║");
        System.out.println("╠═════════════╬══════════════════════════╬══════════════════════════════════════════╣");
        String sql = "SELECT * FROM categories ORDER BY categoryId";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("║ %-11d ║ %-24s ║ %-40s ║%n",
                        rs.getInt("categoryId"),
                        truncate(rs.getString("categoryName"), 24),
                        truncate(rs.getString("description"), 40));
            }
            if (!found) System.out.println("║                        No categories found.                                       ║");
        } catch (SQLException e) {
            System.out.println("Error printing categories: " + e.getMessage());
        }
        System.out.println("╚═════════════╩══════════════════════════╩══════════════════════════════════════════╝");
    }

    public void printUsersTable() {
        System.out.println();
        System.out.println("╔══════════╦══════════════════════════╦══════════════════════════════╦══════════╗");
        System.out.println("║ User ID  ║ Name                     ║ Email                        ║ Role     ║");
        System.out.println("╠══════════╬══════════════════════════╬══════════════════════════════╬══════════╣");
        String sql = "SELECT * FROM users ORDER BY role, name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("║ %-8d ║ %-24s ║ %-28s ║ %-8s ║%n",
                        rs.getInt("userId"),
                        truncate(rs.getString("name"), 24),
                        truncate(rs.getString("email"), 28),
                        truncate(rs.getString("role"), 8));
            }
            if (!found) System.out.println("║                           No users found.                                    ║");
        } catch (SQLException e) {
            System.out.println("Error printing users: " + e.getMessage());
        }
        System.out.println("╚══════════╩══════════════════════════╩══════════════════════════════╩══════════╝");
    }

    public void printTransactionsTable() {
        System.out.println();
        System.out.println("╔══════╦══════════════════════════════╦════════════╦══════════╦══════════╦════════════════════════════╦══════════════╗");
        System.out.println("║  ID  ║ Product                      ║ Staff      ║ Type     ║ Quantity ║ Reason                     ║ Date         ║");
        System.out.println("╠══════╬══════════════════════════════╬════════════╬══════════╬══════════╬════════════════════════════╬══════════════╣");
        String sql = """
            SELECT t.transactionId, p.productName, u.name AS staffName,
                   t.transactionType, t.quantity, t.reason, t.transactionDate
            FROM stock_transactions t
            LEFT JOIN products p ON t.productId = p.productId
            LEFT JOIN users u    ON t.userId = u.userId
            ORDER BY t.transactionId DESC
            """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("║ %-4d ║ %-28s ║ %-10s ║ %-8s ║ %-8d ║ %-26s ║ %-12s ║%n",
                        rs.getInt("transactionId"),
                        truncate(rs.getString("productName"), 28),
                        truncate(rs.getString("staffName"), 10),
                        truncate(rs.getString("transactionType"), 8),
                        rs.getInt("quantity"),
                        truncate(rs.getString("reason"), 26),
                        truncate(rs.getString("transactionDate"), 12));
            }
            if (!found) System.out.println("║                                       No transactions found.                                              ║");
        } catch (SQLException e) {
            System.out.println("Error printing transactions: " + e.getMessage());
        }
        System.out.println("╚══════╩══════════════════════════════╩════════════╩══════════╩══════════╩════════════════════════════╩══════════════╝");
    }

    public void printLowStockAlerts() {
        String sql = """
            SELECT p.*, c.categoryName
            FROM products p
            LEFT JOIN categories c ON p.categoryId = c.categoryId
            WHERE p.currentStock <= p.minimumStock AND p.status = 'active'
            ORDER BY (p.currentStock - p.minimumStock)
            """;
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ⚠  LOW STOCK ALERTS  ⚠                            ║");
        System.out.println("╠════════╦════════════════════════════╦══════════╦══════════╦════════════╣");
        System.out.println("║ Prod # ║ Product Name               ║ Current  ║ Minimum  ║ Unit       ║");
        System.out.println("╠════════╬════════════════════════════╬══════════╬══════════╬════════════╣");
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("║ %-6d ║ %-26s ║ %-8d ║ %-8d ║ %-10s ║%n",
                        rs.getInt("productId"),
                        truncate(rs.getString("productName"), 26),
                        rs.getInt("currentStock"),
                        rs.getInt("minimumStock"),
                        truncate(rs.getString("unit"), 10));
            }
            if (!found) {
                System.out.println("║          ✅  All products are sufficiently stocked!               ║");
            }
        } catch (SQLException e) {
            System.out.println("Error checking low stock: " + e.getMessage());
        }
        System.out.println("╚════════╩════════════════════════════╩══════════╩══════════╩════════════╝");
    }

    public void printInventoryReport() {
        String sql = """
            SELECT
                COUNT(*) AS totalProducts,
                SUM(currentStock) AS totalUnits,
                SUM(currentStock * costPrice) AS totalValue,
                SUM(CASE WHEN currentStock <= minimumStock THEN 1 ELSE 0 END) AS lowStockCount
            FROM products WHERE status = 'active'
            """;
        String inSql  = "SELECT COALESCE(SUM(quantity), 0) AS totalIn  FROM stock_transactions WHERE transactionType = 'stock_in'";
        String outSql = "SELECT COALESCE(SUM(quantity), 0) AS totalOut FROM stock_transactions WHERE transactionType = 'stock_out'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int    totalProducts  = rs.getInt("totalProducts");
                int    totalUnits     = rs.getInt("totalUnits");
                double totalValue     = rs.getDouble("totalValue");
                int    lowStockCount  = rs.getInt("lowStockCount");

                int totalIn = 0, totalOut = 0;
                try (Statement s2 = connection.createStatement();
                     ResultSet r2 = s2.executeQuery(inSql)) {
                    if (r2.next()) totalIn = r2.getInt("totalIn");
                }
                try (Statement s3 = connection.createStatement();
                     ResultSet r3 = s3.executeQuery(outSql)) {
                    if (r3.next()) totalOut = r3.getInt("totalOut");
                }

                System.out.println();
                System.out.println("╔══════════════════════════════════════════════════════╗");
                System.out.println("║            📊  INVENTORY REPORT                     ║");
                System.out.println("╠══════════════════════════════════════════════════════╣");
                System.out.printf( "║  Total Active Products    : %-23d ║%n", totalProducts);
                System.out.printf( "║  Total Units in Stock     : %-23d ║%n", totalUnits);
                System.out.printf( "║  Estimated Inventory Value: PHP %-19.2f ║%n", totalValue);
                System.out.printf( "║  Products Below Min Stock : %-23d ║%n", lowStockCount);
                System.out.println("╠══════════════════════════════════════════════════════╣");
                System.out.printf( "║  Total Stock-In  (all time): %-22d ║%n", totalIn);
                System.out.printf( "║  Total Stock-Out (all time): %-22d ║%n", totalOut);
                System.out.println("╚══════════════════════════════════════════════════════╝");
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 1) + "…";
    }
}
