# Money Manager

A Java-based desktop application for personal finance management with MySQL database integration.

## Features

- **User Authentication**: Secure login and registration system
- **Transaction Management**: Add, view, and delete income/expense transactions
- **Real-time Balance**: Automatic calculation of current balance
- **Category Organization**: Predefined categories for better expense tracking
- **User-friendly Interface**: Clean and intuitive Swing-based GUI
- **Data Validation**: Input validation for secure data entry
- **Database Integration**: MySQL database for persistent data storage

## Project Structure

```
MoneyManager/
│
├── src/com/moneymanager/
│   ├── Main.java                    # Application entry point
│   │
│   ├── dao/                         # Data Access Objects
│   │   ├── DatabaseConnection.java  # Database connection utility
│   │   └── UserDAO.java            # User data operations
│   │
│   ├── model/                       # Data models
│   │   └── User.java               # User entity class
│   │
│   ├── ui/                         # User Interface components
│   │   ├── LoginFrame.java         # Login and registration interface
│   │   └── DashboardFrame.java     # Main application dashboard
│   │
│   └── util/                       # Utility classes
│       └── ValidationUtil.java     # Input validation utilities
│
├── lib/                            # External libraries
│   └── mysql-connector-j-9.4.0.jar # MySQL JDBC driver
│
├── database/                       # Database schema
│   └── money_manager.sql           # Database creation script
│
└── README.md                       # This file
```

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **MySQL Server**: Version 5.7 or higher
- **MySQL JDBC Driver**: Included in the `lib/` folder

## Setup Instructions

### 1. Database Setup

1. Install and start MySQL Server
2. Open MySQL command line or MySQL Workbench
3. Execute the database script:
   ```sql
   source /path/to/MoneyManager/database/money_manager.sql
   ```
   Or copy and paste the contents of `money_manager.sql` into your MySQL client

### 2. Database Configuration

Update the database connection settings in `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/money_manager";
private static final String USERNAME = "root";
private static final String PASSWORD = "your_mysql_password";
```

### 3. Compilation

Navigate to the project directory and compile the Java files:

```bash
# Windows
javac -cp "lib\mysql-connector-j-9.4.0.jar;." -d . src\com\moneymanager\*.java src\com\moneymanager\dao\*.java src\com\moneymanager\model\*.java src\com\moneymanager\ui\*.java src\com\moneymanager\util\*.java

# Linux/Mac
javac -cp "lib/mysql-connector-j-9.4.0.jar:." -d . src/com/moneymanager/*.java src/com/moneymanager/dao/*.java src/com/moneymanager/model/*.java src/com/moneymanager/ui/*.java src/com/moneymanager/util/*.java
```

### 4. Running the Application

```bash
# Windows
java -cp "lib\mysql-connector-j-9.4.0.jar;." com.moneymanager.Main

# Linux/Mac
java -cp "lib/mysql-connector-j-9.4.0.jar:." com.moneymanager.Main
```

## Usage

### First Time Setup

1. Run the application
2. Click "Register" to create a new account
3. Fill in your details and click "Register"
4. Login with your new credentials

### Managing Transactions

1. **Adding Transactions**:
   - Enter amount, description, select type (Income/Expense) and category
   - Click "Add Transaction"

2. **Viewing Transactions**:
   - All transactions are displayed in the main table
   - Balance is automatically calculated and displayed

3. **Deleting Transactions**:
   - Select a transaction from the table
   - Click "Delete Selected"

## Default Test Data

The database script includes sample data:
- **Demo User**: 
  - Username: `demo_user`
  - Password: `demo123`
- **Admin User**:
  - Username: `admin`
  - Password: `password123`

## Database Schema

### Tables

1. **users**: User account information
2. **transactions**: Financial transactions
3. **categories**: Transaction categories (predefined)
4. **budgets**: Budget management (for future enhancement)

### Views

1. **user_balance**: Real-time balance calculation
2. **monthly_summary**: Monthly transaction summaries

## Security Features

- Input validation for all user inputs
- SQL injection prevention using PreparedStatements
- Password validation requirements
- Username uniqueness validation

## Future Enhancements

- Budget management and tracking
- Financial reports and charts
- Data export functionality
- Multi-currency support
- Backup and restore features
- Dark mode theme
- Transaction search and filtering

## Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Verify MySQL server is running
   - Check database credentials in `DatabaseConnection.java`
   - Ensure the `money_manager` database exists

2. **ClassNotFoundException for MySQL Driver**:
   - Verify the MySQL connector JAR is in the `lib/` folder
   - Check the classpath when running the application

3. **Compilation Errors**:
   - Ensure all source files are present
   - Check Java version compatibility
   - Verify classpath includes the MySQL connector

### Performance Tips

- Regularly backup your database
- Monitor database size for large numbers of transactions
- Consider indexing for better query performance (already included in schema)

## Development

### Code Structure

- **MVC Pattern**: Model-View-Controller architecture
- **DAO Pattern**: Data Access Object for database operations
- **Swing GUI**: Java Swing for user interface
- **Input Validation**: Comprehensive validation utilities

### Extending the Application

1. **Adding New Features**:
   - Create new classes in appropriate packages
   - Follow existing naming conventions
   - Update database schema if needed

2. **Modifying UI**:
   - Update relevant classes in the `ui` package
   - Maintain consistent styling and layout

## License

This project is for educational purposes. Feel free to use and modify as needed.

## Support

For issues or questions, please review the troubleshooting section or check the source code comments for detailed implementation information.