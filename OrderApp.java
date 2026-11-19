package com.orders;

import java.util.List;
import java.util.Scanner;

public class OrderApp {

    public static void main(String[] args) {
        OrderDAO dao = new OrderDAO();
        Scanner sc = new Scanner(System.in);

        // (a) Insert at least three initial order records (handles duplicate IDs safely)
        System.out.println("--- Seeding Initial Orders ---");
        dao.insertOrder(new Order(101, "Aarav Sharma", "Laptop", 3, 55000.0, "Pending"));
        dao.insertOrder(new Order(102, "Priya Verma", "Wireless Mouse", 5, 800.0, "Pending"));
        dao.insertOrder(new Order(103, "Rohan Gupta", "Office Chair", 1, 4500.0, "Shipped"));
        dao.insertOrder(new Order(104, "Neha Singh", "USB-C Hub", 4, 1500.0, "Cancelled"));

        int choice;
        do {
            System.out.println("\n===== ORDER MANAGEMENT MENU =====");
            System.out.println("1. Insert New Order");
            System.out.println("2. View Orders by Status (Quantity > 2, Unit Price Desc)");
            System.out.println("3. Increase Order Quantity");
            System.out.println("4. Search & Update Order Status");
            System.out.println("5. View Total Order Value & Delete Cancelled Order");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Order ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Customer Name: ");
                    String customer = sc.nextLine();
                    System.out.print("Enter Product Name: ");
                    String product = sc.nextLine();
                    System.out.print("Enter Quantity: ");
                    int qty = sc.nextInt();
                    System.out.print("Enter Unit Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter Status (Pending / Shipped / Delivered / Cancelled): ");
                    String status = sc.nextLine();
                    dao.insertOrder(new Order(id, customer, product, qty, price, status));
                }
                case 2 -> {
                    System.out.print("Enter Status to Filter (e.g., Pending): ");
                    String status = sc.nextLine();
                    List<Order> results = dao.getOrdersByStatusAndMinQty(status);
                    if (results.isEmpty()) {
                        System.out.println("No orders found with status '" + status + "' and quantity > 2.");
                    } else {
                        System.out.println("--- Matching Orders (Unit Price Desc) ---");
                        for (Order o : results) {
                            System.out.println(o);
                        }
                    }
                }
                case 3 -> {
                    System.out.print("Enter Order ID: ");
                    int id = sc.nextInt();
                    System.out.print("Enter Additional Quantity: ");
                    int addQty = sc.nextInt();
                    dao.increaseOrderQuantity(id, addQty);
                }
                case 4 -> {
                    System.out.print("Enter Order ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter New Status (Pending / Shipped / Delivered): ");
                    String newStatus = sc.nextLine();
                    dao.updateOrderStatus(id, newStatus);
                }
                case 5 -> {
                    dao.displayTotalOrderValue();
                    System.out.print("Enter Order ID to Delete (only if Cancelled): ");
                    int id = sc.nextInt();
                    dao.deleteCancelledOrder(id);
                }
                case 6 -> System.out.println("Exiting Order Management System.");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);

        sc.close();
    }
}