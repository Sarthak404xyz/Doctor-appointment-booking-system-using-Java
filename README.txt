DOCTOR APPOINTMENT BOOKING SYSTEM
=================================

Simple TY Engineering Java Project

TECHNOLOGIES
------------
Java
JDBC
MySQL
HTML
CSS
JavaScript

JAVA CONCEPTS USED
------------------
1. Classes and Objects
2. Encapsulation
3. Constructors
4. Methods
5. if-else
6. Loops
7. ArrayList / Collections
8. Exception Handling
9. JDBC
10. PreparedStatement

IMPORTANT:
The project is intentionally kept simple so that it is easy to understand
and explain during an external viva.

SETUP
-----

1. Install JDK 17 or newer.
2. Install MySQL.
3. Open database.sql in MySQL Workbench and run it.
4. Open:
   src/Database.java

5. Change:
   USER = "root"
   PASSWORD = "root"

   to your actual MySQL username/password.

6. Add MySQL Connector/J to your VS Code Java project.

7. Open the project folder in VS Code.

8. Run Main.java.

9. Open:
   http://localhost:8080

DOUBLE BOOKING
--------------
Before inserting an appointment, Java checks:

doctor + date + time

If the same slot already exists with status "Booked",
the appointment is rejected.

VIVA EXPLANATION
----------------
Doctor.java:
Stores doctor information.

Patient.java:
Stores patient information.

Appointment.java:
Represents one appointment and its status.

Database.java:
Creates the connection between Java and MySQL.

Main.java:
Contains the main program, database operations and server routes.

HTML/CSS/JavaScript:
Creates the simple responsive interface.

WHY JDBC?
Java uses JDBC to connect with MySQL and execute SQL queries.

WHY OOP?
Different real-world entities such as Doctor, Patient and Appointment
are represented using separate classes.

WHY PREPAREDSTATEMENT?
It allows values to be inserted safely into SQL queries.
