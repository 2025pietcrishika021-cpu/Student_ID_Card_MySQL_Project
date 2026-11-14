PIET Student ID Card Request & Generation System
================================================

1. Open your existing IntelliJ project.
2. Back up your old StudentIDCardApp.java first.
3. Replace src/StudentIDCardApp.java with this version.
4. Put DBConnection.java in the same src folder.
5. Open DBConnection.java and replace YOUR_PASSWORD with your MySQL root password.
6. In MySQL Workbench, open and run database_setup.sql (or verify the two tables already match).
7. Ensure mysql-connector-j JAR is already added to IntelliJ module dependencies.
8. Run StudentIDCardApp.java.

Admin login:
Username: admin
Password: admin123

Important:
- Database name: student_id_card_db
- students table uses student_name (not name).
- id_card_requests table uses request_id, student_id, request_date, request_type, status.
- All registration, request, status, dashboard and card lookup operations use MySQL.
- ID card is displayed in a printable text preview dialog; it is not a plastic-card printer integration.
