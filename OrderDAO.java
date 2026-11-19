package com.orders;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderDAO {

    private static final List<String> VALID_STATUSES = Arrays.asList("Pending", "Shipped", "Delivered", "Cancelled");

    private Connection getConnection() throws SQLException {
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            password = "";
        }
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/ecommerce_db",
            "root",
            password
        );
    }

    // (a) Insert Order Record using PreparedStatement & handle duplicate order_id
    public void insertOrder(Order o) {
        String sql = "INSERT INTO Orders (order_id, customer_name, product_name, quantity, unit_price, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, o.getOrderId());
            ps.setString(2, o.getCustomerName());
            ps.setString(3, o.getProductName());
            ps.setInt(4, o.getQuantity());
            ps.setDouble(5, o.getUnitPrice());
            ps.setString(6, o.getStatus());

            ps.executeUpdate();
            System.out.println("Order inserted successfully: ID " + o.getOrderId() + " (" + o.getProductName() + ")");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Error: Duplicate Order ID " + o.getOrderId() + "! Order already exists.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // (b) Retrieve orders by status with quantity > 2, sorted by unit_price DESC using List<Order>
    public List<Order> getOrdersByStatusAndMinQty(String status) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE LOWER(status) = LOWER(?) AND quantity > 2 ORDER BY unit_price DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orderList.add(new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return orderList;
    }

    // (c) Increase existing quantity by additional quantity
    public void increaseOrderQuantity(int orderId, int additionalQty) {
        if (additionalQty <= 0) {
            System.out.println("Error: Additional quantity must be greater than 0.");
            return;
        }
        String sql = "UPDATE Orders SET quantity = quantity + ? WHERE order_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, additionalQty);
            ps.setInt(2, orderId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Quantity updated successfully for Order ID " + orderId + ".");
            } else {
                System.out.println("Order ID " + orderId + " does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Helper: Retrieve single order by ID
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("order_id"),
                        rs.getString("customer_name"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    // (d) Search and update status if order exists and status is valid (Pending, Shipped, Delivered, etc.)
    public void updateOrderStatus(int orderId, String newStatus) {
        Order existing = getOrderById(orderId);
        if (existing == null) {
            System.out.println("Order ID " + orderId + " does not exist.");
            return;
        }
        System.out.println("Current Order -> " + existing);

        String normalizedStatus = null;
        for (String valid : VALID_STATUSES) {
            if (valid.equalsIgnoreCase(newStatus.trim())) {
                normalizedStatus = valid;
                break;
            }
        }

        if (normalizedStatus == null) {
            System.out.println("Invalid status '" + newStatus + "'! Allowed values: Pending, Shipped, Delivered, Cancelled.");
            return;
        }

        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, normalizedStatus);
            ps.setInt(2, orderId);
            ps.executeUpdate();

            Order updated = getOrderById(orderId);
            System.out.println("Updated Order -> " + updated);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // (e1) Display total order value using SUM(quantity * unit_price)
    public void displayTotalOrderValue() {
        String sql = "SELECT COALESCE(SUM(quantity * unit_price), 0) AS total_order_value FROM Orders";
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                System.out.printf("Total Order Value: Rs. %.2f%n", rs.getDouble("total_order_value"));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // (e2) Delete order only if status is Cancelled
    public void deleteCancelledOrder(int orderId) {
        Order existing = getOrderById(orderId);
        if (existing == null) {
            System.out.println("Cannot delete: Order ID " + orderId + " does not exist.");
            return;
        }
        if (!"Cancelled".equalsIgnoreCase(existing.getStatus())) {
            System.out.println("Cannot delete: Order ID " + orderId + " has status '" + existing.getStatus()
                    + "' (only 'Cancelled' orders can be deleted).");
            return;
        }

        String sql = "DELETE FROM Orders WHERE order_id = ? AND LOWER(status) = 'cancelled'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Cancelled Order ID " + orderId + " deleted successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}