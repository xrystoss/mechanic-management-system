# Mechanic Management System

A Java-based desktop application for managing a mechanic shop, including customer, vehicle, service, and parts management.

## Features

- **Customer Management**: Add, edit, and search customers
- **Vehicle Management**: Register and track vehicles
- **Service Management**: Record services and maintenance
- **Parts Inventory**: Track parts availability and costs
- **Payment Tracking**: Manage pending payments and invoices

## Technologies

- Java 8+
- MySQL Database
- Swing GUI
- MigLayout (for flexible UI layouts)
- JDatePicker (for date selection)

## Prerequisites

- Java JDK 8 or later
- MySQL Server 5.7+
- Maven (for dependency management)

## Installation

1. **Database Setup**:
   ```sql
   CREATE DATABASE MECHANIC;
   USE MECHANIC;
   -- Run the SQL scripts from the /sql folder to create tables
2. **Configuration**:
Update database credentials in CONNECTION.java

con = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/MECHANIC",
    "your_username",
    "your_password"
);
