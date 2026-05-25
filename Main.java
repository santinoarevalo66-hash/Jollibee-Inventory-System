package org.example;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner;
    private static final Repository repo;
    private static final InventoryManager inventoryManager;
    private static final AuthenticationService authService;

    private static int    loggedInUserId;
    private static String loggedInUserName;
    private static String loggedInUserRole;

    // ─────────────────────────────────────────
    // ENTRY POINT
    // ─────────────────────────────────────────

    public static void main(String[] args) {
        repo.connect();
        repo.createTables();
        repo.seedCategories();
        repo.seedProducts();

        if (!authService.adminPasswordExists()) {
            authService.setAdminPassword("admin123");
            System.out.println("Default admin password initialized: admin123");
        }

        int choice;
        do {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println(  "║       JOLLIBEE INVENTORY SYSTEM        ║");
            System.out.println(  "╠════════════════════════════════════════╣");
            System.out.println(  "║  1. Staff Login / Register             ║");
            System.out.println(  "║  2. Admin Login                        ║");
            System.out.println(  "║  0. Exit                               ║");
            System.out.println(  "╚════════════════════════════════════════╝");
            System.out.print("Enter choice: ");
            choice = getValidChoice(0, 2);

            switch (choice) {
                case 0:
                    System.out.println("Thank you for using Jollibee Inventory System. Goodbye!");
                    break;
                case 1:
                    staffMenu();
                    break;
                case 2:
                    adminLogin();
                    break;
            }
        } while (choice != 0);

        repo.close();
    }

    // ─────────────────────────────────────────
    // INPUT HELPER
    // ─────────────────────────────────────────

    static int getValidChoice(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (input >= min && input <= max) return input;
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                System.out.print("Enter choice: ");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                System.out.print("Enter choice: ");
            }
        }
    }

    // ─────────────────────────────────────────
    // STAFF MENU
    // ─────────────────────────────────────────

    private static void staffMenu() {
        int choice;
        do {
            if (loggedInUserId == -1) {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println(  "║           STAFF ACCESS                 ║");
                System.out.println(  "╠════════════════════════════════════════╣");
                System.out.println(  "║  1. Register                           ║");
                System.out.println(  "║  2. Login                              ║");
                System.out.println(  "║  0. Back                               ║");
                System.out.println(  "╚════════════════════════════════════════╝");
                System.out.print("Enter choice: ");
                choice = getValidChoice(0, 2);

                switch (choice) {
                    case 0:
                        return;

                    case 1:
                        System.out.println("\n===== REGISTER STAFF =====");
                        System.out.print("Full Name: ");
                        String regName = scanner.nextLine();

                        String regEmail;
                        while (true) {
                            System.out.print("Email (@gmail.com): ");
                            regEmail = scanner.nextLine();
                            if (authService.isValidGmail(regEmail)) break;
                            System.out.println("Warning: " + authService.getEmailWarning(regEmail));
                        }

                        System.out.print("Password: ");
                        String regPassword = scanner.nextLine();

                        System.out.println("\n--- Registration Summary ---");
                        System.out.println("Name     : " + regName);
                        System.out.println("Email    : " + regEmail);
                        System.out.println("Password : " + "*".repeat(regPassword.length()));
                        System.out.println("----------------------------");
                        System.out.println("1. Proceed  2. Cancel");
                        System.out.print("Enter choice: ");
                        if (getValidChoice(1, 2) == 1) {
                            authService.registerUser(regName, regEmail, regPassword, "staff");
                        } else {
                            System.out.println("Registration cancelled.");
                        }
                        break;

                    case 2:
                        System.out.println("\n===== STAFF LOGIN =====");
                        String loginEmail;
                        while (true) {
                            System.out.print("Email: ");
                            loginEmail = scanner.nextLine();
                            if (authService.isValidGmail(loginEmail)) break;
                            System.out.println("Warning: " + authService.getEmailWarning(loginEmail));
                        }
                        System.out.print("Password: ");
                        String loginPassword = scanner.nextLine();
                        int result = authService.loginUser(loginEmail, loginPassword);
                        if (result != -1) {
                            loggedInUserId   = result;
                            loggedInUserName = authService.getNameByEmail(loginEmail);
                            loggedInUserRole = authService.getRoleByEmail(loginEmail);
                        }
                        break;

                    default:
                        choice = -1;
                }

            } else {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println(  "║  STAFF MENU — " + padRight(loggedInUserName, 24) + "║");
                System.out.println(  "╠════════════════════════════════════════╣");
                System.out.println(  "║  1. View All Products                  ║");
                System.out.println(  "║  2. View Low-Stock Alerts              ║");
                System.out.println(  "║  3. Record Stock-In (Restock)          ║");
                System.out.println(  "║  4. Record Stock-Out (Usage/Sold)      ║");
                System.out.println(  "║  5. View My Transaction History        ║");
                System.out.println(  "║  6. Search Product                     ║");
                System.out.println(  "║  0. Log Out                            ║");
                System.out.println(  "╚════════════════════════════════════════╝");
                System.out.print("Enter choice: ");
                choice = getValidChoice(0, 6);

                switch (choice) {
                    case 1:
                        repo.printProductsTable();
                        break;
                    case 2:
                        repo.printLowStockAlerts();
                        break;
                    case 3:
                        inventoryManager.recordStockIn(loggedInUserId);
                        break;
                    case 4:
                        inventoryManager.recordStockOut(loggedInUserId);
                        break;
                    case 5:
                        inventoryManager.viewMyTransactions(loggedInUserId);
                        break;
                    case 6:
                        inventoryManager.searchProduct();
                        break;
                }

                if (choice == 6) {
                }
            }
        } while (choice != 0);
    }

    // ─────────────────────────────────────────
    // ADMIN
    // ─────────────────────────────────────────

    private static void adminLogin() {
        System.out.print("\nEnter Admin Password: ");
        String input = scanner.nextLine();
        if (authService.verifyAdminPassword(input)) {
            System.out.println("Access granted. Welcome, Admin!");
            adminMenu();
        } else {
            System.out.println("Incorrect password. Access denied.");
        }
    }

    private static void adminMenu() {
        int choice;
        do {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println(  "║           ADMIN MENU                   ║");
            System.out.println(  "╠════════════════════════════════════════╣");
            System.out.println(  "║  1. View All Products                  ║");
            System.out.println(  "║  2. Add Product                        ║");
            System.out.println(  "║  3. Edit Product                       ║");
            System.out.println(  "║  4. Delete Product                     ║");
            System.out.println(  "║  5. View All Categories                ║");
            System.out.println(  "║  6. Add Category                       ║");
            System.out.println(  "║  7. View All Transactions              ║");
            System.out.println(  "║  8. View All Staff                     ║");
            System.out.println(  "║  9. Inventory Report                   ║");
            System.out.println(  "║  10. Low-Stock Alerts                  ║");
            System.out.println(  "║  11. Change Admin Password             ║");
            System.out.println(  "║  0. Log Out                            ║");
            System.out.println(  "╚════════════════════════════════════════╝");
            System.out.print("Enter choice: ");
            choice = getValidChoice(0, 11);

            switch (choice) {
                case 0:  System.out.println("Returning to main menu..."); break;
                case 1:  repo.printProductsTable(); break;
                case 2:  inventoryManager.addProduct(); break;
                case 3:  inventoryManager.editProduct(); break;
                case 4:  inventoryManager.deleteProduct(); break;
                case 5:  repo.printCategoriesTable(); break;
                case 6:  inventoryManager.addCategory(); break;
                case 7:  repo.printTransactionsTable(); break;
                case 8:  repo.printUsersTable(); break;
                case 9:  repo.printInventoryReport(); break;
                case 10: repo.printLowStockAlerts(); break;
                case 11: changeAdminPassword(); break;
            }
        } while (choice != 0);
    }

    private static void changeAdminPassword() {
        System.out.print("Enter current password: ");
        String current = scanner.nextLine();
        if (authService.verifyAdminPassword(current)) {
            System.out.print("Enter new password: ");
            String newPass = scanner.nextLine();
            System.out.print("Confirm new password: ");
            String confirm = scanner.nextLine();
            if (newPass.equals(confirm)) {
                authService.setAdminPassword(newPass);
            } else {
                System.out.println("Passwords do not match. Password not changed.");
            }
        } else {
            System.out.println("Current password incorrect.");
        }
    }

    // ─────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    // ─────────────────────────────────────────
    // STATIC INITIALIZER
    // ─────────────────────────────────────────

    static {
        scanner          = new Scanner(System.in);
        repo             = new Repository();
        inventoryManager = new InventoryManager(repo, scanner);
        authService      = new AuthenticationService(repo);
        loggedInUserId   = -1;
        loggedInUserName = "";
        loggedInUserRole = "";
    }
}
