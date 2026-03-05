# SENTINEL – System Activity Monitoring & Alert Platform

## Overview

SENTINEL is a system designed to monitor user activities within an application and detect suspicious behavior. The platform records login attempts, system events, and administrative actions while generating alerts when predefined security policies are violated.

The goal of the project is to demonstrate the practical implementation of database management concepts such as relational schema design, activity logging, and security monitoring.

## Technologies Used

Frontend: React (planned)
Backend: Java (Servlets)
Database: MariaDB
Database Access: JDBC
Version Control: Git

## Current Progress

The following components have been completed so far:

### 1. Database Design

A complete relational schema has been designed for the SENTINEL system including entities for users, sessions, events, alerts, and security policies.

### 2. Database Implementation

The database has been created in MariaDB and all required tables have been implemented.

### 3. Database Export

The database schema has been exported to a SQL file:

database/sentinel_db.sql

This file allows the database structure to be recreated easily.

### 4. Backend Setup

The backend project folder has been created with the following structure:

backend/
├── src/
│   ├── db/
│   │   └── DBConnection.java
│   └── TestConnection.java

### 5. Database Connection

A Java utility class `DBConnection.java` has been implemented to establish a connection with the MariaDB database using JDBC.

This class handles:

* loading the MariaDB JDBC driver
* connecting to the database
* returning a connection object for use in other backend classes

### 6. Connection Testing

A test class (`TestConnection.java`) has been created to verify that the Java backend can successfully connect to the MariaDB database.

## Next Steps

The next stages of the project will include:

* Creating DAO classes for database operations
* Implementing servlet endpoints for backend logic
* Developing the React frontend interface
* Connecting frontend requests to backend APIs
* Implementing event logging and alert generation

## Authors

Maharshi Dindoliwala
Manan Khanna
Mansi Uttamchandani
Ojas Barve
