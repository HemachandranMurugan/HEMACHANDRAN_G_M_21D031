# BillingSystemModel
This system integrates sales tracking, invoice generation, payment monitoring and stock management by maintaining precise and current product records into a unified, user-friendly platform. It also integrates customer management with detailed profiles including contact details, purchase histories, and preferences, thereby enabling personalized service and strengthened customer relations.Together, it offers a cohesive system that enhances efficiency, accuracy, and customer satisfaction in store management.

## Features

- **Admin Frame:**
  - Add new product
  - Update product quantity
  - Fetch item sales analysis
  - Get BIll Summary by date
  - Get Customer Report by Customer Id
 
- **Employee Frame:**
  - Generate invoices
  - Fetch all invoices
  - Check customer balance
  - Check paid and unpaid invoices


## Installation

1. Clone the repository:

2. Set up your MySQL database and configure JDBC connection details in the project.

3. Compile and run the application.

## Database Schema
The system uses three main tables: Customer, Product, and Bill.

#### Table 1: Customer Table

| Field Name      | Data Type | Description                   |
|-----------------|-----------|-------------------------------|
| customer_phone  | VARCHAR   | Phone number of the customer  |
| customer_name   | VARCHAR   | Name of the customer          |
| membership_points | DOUBLE | Points accumulated by the customer |
| customer_balance | DOUBLE  | Balance amount for the customer |

#### Table 2: Product Table

| Field Name   | Data Type | Description                       |
|--------------|-----------|-----------------------------------|
| product_id   | INT       | Unique identifier for products    |
| product_name | VARCHAR   | Name of the product               |
| product_type | VARCHAR   | Type of the product               |
| unit_price   | DOUBLE    | Price per unit of the product     |
| quantity     | INT       | Available quantity in stock       |

#### Table 3: Bill Table

| Field Name  | Data Type | Description                            |
|-------------|-----------|----------------------------------------|
| Bill_No     | VARCHAR   | Unique identifier for each bill        |
| Bill_Date   | DATE      | Date when the bill was generated       |
| Bill_amt    | DOUBLE    | Total amount of the bill               |
| Customer_id | INT       | ID of the customer associated with the bill |

#### Table 4: ItemSales Table

| Field Name     | Data Type | Description                            |
|----------------|-----------|----------------------------------------|
| product_id     | INT       | ID of the product sold                 |
| Bill_No        | VARCHAR   | Bill number associated with the sale    |
| Bill_Quantity  | INT       | Quantity of the product sold in the bill |


## HOW TO RUN
- Java JDK: Version 11 or above.
- MySQL: Version 8.0 or above (or other supported database).
- JDBC Driver: For MySQL, you need the mysql-connector-java driver.

**DataBase Setup**
1. Install MySQL
2. Create a database
3. Create Tables

**JDBC Configuration**
1. Add JDBC Driver: Download the mysql-connector-java JAR file from MySQL’s official website.
   Place this JAR file in your project’s lib directory or add it as a dependency in your build tool (e.g., Maven or Gradle).
2. Configure Database Connection: Update the JDBC connection settings in your Java code.
   Typically, you will have a class for anaging database connections. 
3. Update Connection Details: Replace your_database_name, your_username, and your_password with your actual MySQL database details.
  ![Screenshot 2024-07-19 003907](https://github.com/user-attachments/assets/73b64b49-5851-487b-9afd-de738db29849)











