# Library Management System

This is a simple Library Management System built using Java with Swing (GUI) and MySQL for database storage. The project is developed in NetBeans and demonstrates basic CRUD operations along with Object-Oriented Programming concepts.

## Description

The application allows users to:

* Add, delete, and view books
* Add, delete, and view users
* Create and view loan records

All data is stored in a MySQL database and accessed using JDBC.

## Technologies Used

* Java (OOP)
* Java Swing (GUI)
* MySQL
* JDBC
* NetBeans IDE

## Project Structure

The project folder contains:

* Source code files (.java)
* `librarymanagement.sql` → database export file for phpMyAdmin
* `MySqlJdbcDriver/` → contains `mysql-connector-j-8.3.0.jar`
* `jcalendar-1.4/` → contains `lib/jcalendar-1.4.jar` and related files

## Database Setup

1. Open phpMyAdmin

2. Create a new database named:

   librarymanagement

3. Import the file:

   librarymanagement.sql

This will automatically create all required tables.

## Libraries Setup (NetBeans)

Make sure the following libraries are added to your project:

1. MySQL JDBC Driver
   Path:
   MySqlJdbcDriver/mysql-connector-j-8.3.0.jar

2. JCalendar Library
   Path:
   jcalendar-1.4/lib/jcalendar-1.4.jar

In NetBeans:

* Right click project → Properties → Libraries → Add JAR/Folder
* Add both .jar files above

## Database Configuration

Ensure your database connection matches your MySQL setup:

* URL: jdbc:mysql://localhost:3308/librarymanagement
* User: root
* Password: (your MySQL password)

Update this in your database connection class if needed.

## How to Run

1. Open the project in NetBeans
2. Ensure MySQL server is running on port 3308
3. Ensure database is imported via phpMyAdmin
4. Make sure all required libraries are added
5. Run the main file:

   LibraryManagementSystemGUI.java

## Notes

* Loan currently uses simple linking between book and user
* Input validation is basic
* GUI is built using Swing (not JavaFX)
* JCalendar is included for date handling

## Purpose

This project is created for learning:

* Java Object-Oriented Programming
* GUI development using Swing
* Database integration using JDBC

## Author

Student project for academic purposes.