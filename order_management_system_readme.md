# Order Management System (Java JDBC & MySQL)

A menu-driven console application built with **Java 21**, **JDBC**, and **MySQL** to manage e-commerce customer orders using parameterized queries (`PreparedStatement`), safe resource management (`try-with-resources`), and Java Collections (`ArrayList`).

---

## Project Structure

```text
OrderManagement/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── orders/
                    ├── Order.java       # Model class representing an order record
                    ├── OrderDAO.java    # Data Access Object handling JDBC & SQL logic
                    └── OrderApp.java    # Menu-driven console entry point
```

---

## Database Schema

**Database:** `ecommerce_db`  
**Table:** `Orders`

| Column | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `order_id` | `INT` | `PRIMARY KEY` | Unique identifier for each order |
| `customer_name` | `VARCHAR(100)` | `NOT NULL` | Name of the customer |
| `product_name` | `VARCHAR(100)` | `NOT NULL` | Name of the ordered product |
| `quantity` | `INT` | `NOT NULL` | Quantity ordered |
| `unit_price` | `DOUBLE` | `NOT NULL` | Price per unit |
| `status` | `VARCHAR(30)` | `NOT NULL` | Order status (`Pending`, `Shipped`, `Delivered`, `Cancelled`) |

### SQL Setup Script

```sql
CREATE DATABASE IF NOT EXISTS ecommerce_db;
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS Orders (
    order_id INT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    status VARCHAR(30) NOT NULL
);
```

---

## Features Implemented

1. **Insert Order Records (`PreparedStatement`)**
   - Establishes a JDBC connection with MySQL and inserts initial and user-defined order records.
   - Catches `SQLIntegrityConstraintViolationException` to gracefully handle duplicate `order_id` entries.
2. **Conditional Retrieval & Sorting (`List<Order>`)**
   - Filters orders by a user-specified `status` where `quantity > 2`.
   - Stores matching `ResultSet` records inside an `ArrayList<Order>` sorted by `unit_price` in descending order.
3. **Update Order Quantity**
   - Increments the existing quantity of a given `order_id` by a user-provided amount and notifies if the order ID does not exist.
4. **Search & Modify Order Status**
   - Retrieves existing order details, validates the new status (`Pending`, `Shipped`, `Delivered`, `Cancelled`), updates the record, and displays the updated details.
5. **Aggregate Query & Conditional Deletion**
   - Calculates the total inventory/order value using `SUM(quantity * unit_price)`.
   - Deletes a record only if its status is `Cancelled`, displaying an informative message if deletion is disallowed or the order does not exist.

---

## How to Run

1. **Configure MySQL Password** (PowerShell):
   ```powershell
   $env:DB_PASSWORD = "your_mysql_root_password"
   ```
2. **Compile & Run using Maven**:
   ```powershell
   mvn compile exec:java -Dexec.mainClass="com.orders.OrderApp"
   ```
   *(Or open `OrderApp.java` in VS Code / IntelliJ / Eclipse and click **Run**.)*

---

## Tech Stack
- **Language:** Java 21
- **Database:** MySQL 8+ / 9+
- **Connectivity:** JDBC (`mysql-connector-j`)
- **Build Tool:** Apache Maven