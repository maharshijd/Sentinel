# SENTINEL – Cybersecurity Monitoring System

SENTINEL is a console-based cybersecurity monitoring system built using **Java, JDBC, and MySQL**.  
It simulates real-world security monitoring operations such as event logging, alert generation, rule-based threat detection, device tracking, and role-based access control.

The system follows a layered architecture:

Model → DAO → Service → Main


# Features

## Authentication System
User registration and login using secure password hashing (SHA-256).

Supports role-based access control:
- Admin users
- Normal users

Each login session is recorded with IP address.


## Security Event Monitoring
Security events can be logged with:
- event type
- threat score
- severity level

Recent events can be viewed in tabular format.


## Rule-Based Alert Engine
Admin can create security rules based on:
- event score thresholds
- event type matching

When an event satisfies a rule condition, an alert is automatically generated.

Example:
If event_score > 70 → create HIGH severity alert.


## Alert Management
System stores alerts generated from suspicious activity.

Admin can:
- view alerts
- resolve alerts
- record remediation actions

System prevents resolving the same alert multiple times.


## Device Tracking
Tracks devices connected to the system using:
- IP address
- device fingerprint
- operating system info

Admin can view all connected devices.
Normal users can view only their own devices.

Devices can be disconnected.


## Session Tracking
Each login creates a session record.

Stores:
- user id
- IP address
- login timestamp

Users can view their session history.


## File Access Monitoring
Simulates file access permissions.

Logs events when:
- file access is allowed
- unauthorized access attempt occurs

Unauthorized attempts can generate alerts through rule engine.


# Role Based Access Control (RBAC)

## Admin privileges
- View all alerts  
- Resolve alerts  
- Create security rules  
- Enable or disable rules  
- View all connected devices  
- Disconnect any device  
- View all device details  
- Simulate security events  


## Normal user privileges
- View alerts  
- View event logs  
- Track own devices  
- Disconnect own devices  
- View own session history  


# Technologies Used

- Java
- JDBC
- MySQL
- SHA-256 password hashing
- Console-based interface


# Project Structure
Sentinel
│
├── config
│ DBconnection.java
│
├── model
│ Alert.java
│ ConnectedDevice.java
│ DeviceDetails.java
│ EventLog.java
│ SecurityRule.java
│ SessionDetails.java
│ User.java
│
├── dao
│ AlertDAO.java
│ DeviceDAO.java
│ DeviceDetailsDAO.java
│ EventDAO.java
│ FilePermissionsDAO.java
│ SecurityRuleDAO.java
│ SessionDetailsDAO.java
│ UserDAO.java
│
├── service
│ AuthService.java
│ FileSecurityService.java
│
├── main
│ Main.java




# Database Tables

- user  
- role  
- department  
- alerts  
- event_logs  
- security_rules  
- connected_devices  
- device_details  
- session_details  
- file_permissions  
- remediation  


# How the system works

User logs in  
Session is created  
User performs action  
Event is logged  
Rules are evaluated  
Alert is generated if rule condition matches  


# Example Workflow

Admin creates rule:
EVENT_SCORE > 70 → HIGH alert

User performs suspicious action:
event_score = 85

System automatically:
- logs event  
- checks rules  
- generates alert  

Admin resolves alert with remediation note.


# How to Run

1. Create MySQL database
2. Update DB credentials in:

config/DBconnection.java

3. Compile project
4. Run Main.java


# Learning Objectives

- Understanding DAO pattern  
- Implementing RBAC (Role Based Access Control)  
- Using JDBC with MySQL  
- Password hashing using SHA-256  
- Designing rule-based detection systems  
- Implementing layered architecture  


# Future Improvements

- Web interface (Spring Boot)
- Real-time alert dashboard
- Email notifications
- JWT authentication
- REST API integration
- Advanced rule engine
- Machine learning anomaly detection
