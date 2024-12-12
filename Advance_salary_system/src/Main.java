import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Main entry point - the Login Window
import javax.swing.*;
import java.util.Scanner;

// Abstract base class for user choice
abstract class UserChoice {
    public abstract void performAction();
}

// Concrete class for the "Yes" choice
class YesChoice extends UserChoice {
    @Override
    public void performAction() {
        System.out.println("Starting the system...");
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true)); // Initialize the GUI system
    }
}

// Concrete class for the "No" choice
class NoChoice extends UserChoice {
    @Override
    public void performAction() {
        System.out.println("Exiting the system. Goodbye!");
        System.exit(0);
    }
}

// Main entry point
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Display the welcome message and options
        System.out.println("Welcome to the Advance Salary Payment System!");
        System.out.println("Do you want to get started?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Enter your choice: ");

        // Get user input
        int choice = scanner.nextInt();

        // Determine the action based on the user's choice using polymorphism
        UserChoice userChoice;
        if (choice == 1) {
            userChoice = new YesChoice(); // "Yes" action
        } else if (choice == 2) {
            userChoice = new NoChoice(); // "No" action
        } else {
            System.out.println("Invalid choice. Please restart the system.");
            return; // Exit without doing anything
        }

        // Perform the action
        userChoice.performAction();
    }
}


// Login Window for User Authentication
class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;

    public LoginWindow() {
        setTitle("Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new GridLayout(4, 2));

        // Create and add components
        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        add(loginButton);

        messageLabel = new JLabel("", JLabel.CENTER);
        add(messageLabel);

        // Action listener for login button
        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Check the username and password against the database
        String query = "SELECT * FROM appuser WHERE username = ? AND password = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");

                // Login successful, pass role to the MainMenu
                messageLabel.setText("Login successful!");
                SwingUtilities.invokeLater(() -> new MainMenu(role).setVisible(true));
                this.dispose();  // Close the login window
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Error connecting to database.");
        }
    }
}

// Main Menu Frame where features are shown based on role
class MainMenu extends JFrame {
    private String userRole;  // To store the user's role

    public MainMenu(String role) {
        this.userRole = role;  // Store the role

        // Window properties
        setTitle("Salary Advance Payment System");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Layout and buttons
        setLayout(new FlowLayout());

        // Create buttons
        JButton viewEmployeeBtn = new JButton("View Employee");
        JButton addEmployeeBtn = new JButton("Add Employee");
        JButton checkStatusBtn = new JButton("Check Status");
        JButton viewCompanyBtn = new JButton("View Company");
        JButton addCompanyBtn = new JButton("Add Company");
        JButton viewUserBtn = new JButton("View User");
        JButton addUserBtn = new JButton("Add User");
        JButton viewAdvanceSalaryBtn = new JButton("View Advance Salary");
        JButton logoutButton = new JButton("Logout"); // Logout button

        // Add buttons to the frame
        add(viewEmployeeBtn);
        add(addEmployeeBtn);
        add(checkStatusBtn);
        add(viewCompanyBtn);
        add(addCompanyBtn);
        add(viewUserBtn);
        add(addUserBtn);
        add(viewAdvanceSalaryBtn);
        add(logoutButton); // Add logout button

        // Disable buttons based on role
        if ("user".equals(userRole)) {
            // If the user is a regular user, disable certain features
            addEmployeeBtn.setEnabled(false);
            checkStatusBtn.setEnabled(false);
            addCompanyBtn.setEnabled(false);
            addUserBtn.setEnabled(false);
            viewAdvanceSalaryBtn.setEnabled(false);
        }

        // Action listeners for each button
        viewEmployeeBtn.addActionListener(e -> new ViewEmployeeFrame().setVisible(true));
        addEmployeeBtn.addActionListener(e -> new AddEmployeeFrame().setVisible(true));
        checkStatusBtn.addActionListener(e -> new CheckStatusFrame().setVisible(true));
        viewCompanyBtn.addActionListener(e -> new ViewCompanyFrame().setVisible(true));
        addCompanyBtn.addActionListener(e -> new AddCompanyFrame().setVisible(true));
        viewUserBtn.addActionListener(e -> new ViewUserFrame().setVisible(true));
        addUserBtn.addActionListener(e -> new AddUserFrame().setVisible(true));
        viewAdvanceSalaryBtn.addActionListener(e -> new ViewAdvanceSalaryFrame().setVisible(true));

        // Logout button functionality
        logoutButton.addActionListener(e -> logout());
    }

    private void logout() {
        // Dispose of the current menu and go back to login screen
        dispose();
        new LoginWindow().setVisible(true); // Show the login window again
    }

    // View Employee Frame
    class ViewEmployeeFrame extends JFrame {
        public ViewEmployeeFrame() {
            setTitle("View Employee");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            JTextField employeeIdField = new JTextField(15);
            JButton viewButton = new JButton("View");

            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter Employee ID:"));
            panel.add(employeeIdField);
            panel.add(viewButton);
            add(panel, BorderLayout.CENTER);

            JTextArea employeeDetailsArea = new JTextArea();
            employeeDetailsArea.setEditable(false);
            add(new JScrollPane(employeeDetailsArea), BorderLayout.SOUTH);

            viewButton.addActionListener(e -> {
                int employeeId = Integer.parseInt(employeeIdField.getText());
                displayEmployeeDetails(employeeId, employeeDetailsArea);
            });
        }

        private void displayEmployeeDetails(int employeeId, JTextArea employeeDetailsArea) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                employeeDetailsArea.setText("Failed to connect to the database.");
                return;
            }

            try {
                String selectQuery = "SELECT * FROM employee WHERE employee_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String details = "Employee ID: " + resultSet.getInt("employee_id") + "\n" +
                            "First Name: " + resultSet.getString("first_name") + "\n" +
                            "Last Name: " + resultSet.getString("last_name") + "\n" +
                            "Salary: " + resultSet.getDouble("salary") + "\n" +
                            "Status: " + resultSet.getString("status");
                    employeeDetailsArea.setText(details);
                } else {
                    employeeDetailsArea.setText("No employee found with ID: " + employeeId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                employeeDetailsArea.setText("Error fetching employee details.");
            }
        }
    }

    // Add Employee Frame
    class AddEmployeeFrame extends JFrame {
        public AddEmployeeFrame() {
            setTitle("Add Employee");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new GridLayout(7, 2));

            JTextField firstNameField = new JTextField(15);
            JTextField lastNameField = new JTextField(15);
            JTextField salaryField = new JTextField(15);
            JTextField statusField = new JTextField(15);
            JTextField phoneNumberField = new JTextField(15);
            JTextField accountNumberField = new JTextField(15);
            JTextField companyIdField = new JTextField(15);

            add(new JLabel("First Name:"));
            add(firstNameField);
            add(new JLabel("Last Name:"));
            add(lastNameField);
            add(new JLabel("Salary:"));
            add(salaryField);
            add(new JLabel("Status:"));
            add(statusField);
            add(new JLabel("Phone Number:"));
            add(phoneNumberField);
            add(new JLabel("Account Number:"));
            add(accountNumberField);
            add(new JLabel("Company ID:"));
            add(companyIdField);

            JButton addButton = new JButton("Add Employee");
            add(addButton);

            addButton.addActionListener(e -> addEmployee(firstNameField, lastNameField, salaryField, statusField, phoneNumberField, accountNumberField, companyIdField));
        }

        private void addEmployee(JTextField firstNameField, JTextField lastNameField, JTextField salaryField,
                                 JTextField statusField, JTextField phoneNumberField, JTextField accountNumberField,
                                 JTextField companyIdField) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
                return;
            }

            try {
                String insertQuery = "INSERT INTO employee (first_name, last_name, salary, status, phone_number, account_number, company_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                preparedStatement.setString(1, firstNameField.getText());
                preparedStatement.setString(2, lastNameField.getText());
                preparedStatement.setDouble(3, Double.parseDouble(salaryField.getText()));
                preparedStatement.setString(4, statusField.getText());
                preparedStatement.setString(5, phoneNumberField.getText());
                preparedStatement.setString(6, accountNumberField.getText());
                preparedStatement.setInt(7, Integer.parseInt(companyIdField.getText()));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding employee.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding employee.");
            }
        }
    }

    // Check Status Frame
    class CheckStatusFrame extends JFrame {
        public CheckStatusFrame() {
            setTitle("Check Status");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new FlowLayout());

            JTextField employeeIdField = new JTextField(15);
            JButton checkStatusButton = new JButton("Check Status");
            JTextArea statusArea = new JTextArea(5, 30);
            statusArea.setEditable(false);

            add(new JLabel("Enter Employee ID:"));
            add(employeeIdField);
            add(checkStatusButton);
            add(new JScrollPane(statusArea));

            checkStatusButton.addActionListener(e -> {
                int employeeId = Integer.parseInt(employeeIdField.getText());
                checkStatus(employeeId, statusArea);
            });
        }

        private void checkStatus(int employeeId, JTextArea statusArea) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                statusArea.setText("Failed to connect to the database.");
                return;
            }

            try {
                String selectQuery = "SELECT status FROM employee WHERE employee_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    statusArea.setText("Status: " + resultSet.getString("status"));
                } else {
                    statusArea.setText("No employee found with ID: " + employeeId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                statusArea.setText("Error fetching status.");
            }
        }
    }

    // View Company Frame
    class ViewCompanyFrame extends JFrame {
        public ViewCompanyFrame() {
            setTitle("View Company");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            JTextField companyIdField = new JTextField(15);
            JButton viewButton = new JButton("View");

            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter Company ID:"));
            panel.add(companyIdField);
            panel.add(viewButton);
            add(panel, BorderLayout.CENTER);

            JTextArea companyDetailsArea = new JTextArea();
            companyDetailsArea.setEditable(false);
            add(new JScrollPane(companyDetailsArea), BorderLayout.SOUTH);

            viewButton.addActionListener(e -> {
                int companyId = Integer.parseInt(companyIdField.getText());
                displayCompanyDetails(companyId, companyDetailsArea);
            });
        }

        private void displayCompanyDetails(int companyId, JTextArea companyDetailsArea) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                companyDetailsArea.setText("Failed to connect to the database.");
                return;
            }

            try {
                String selectQuery = "SELECT * FROM company WHERE company_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, companyId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String details = "Company ID: " + resultSet.getInt("company_id") + "\n" +
                            "Name: " + resultSet.getString("name") + "\n" +
                            "Location: " + resultSet.getString("location");
                    companyDetailsArea.setText(details);
                } else {
                    companyDetailsArea.setText("No company found with ID: " + companyId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                companyDetailsArea.setText("Error fetching company details.");
            }
        }
    }

    // Add Company Frame
    class AddCompanyFrame extends JFrame {
        public AddCompanyFrame() {
            setTitle("Add Company");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new GridLayout(4, 2)); // Updated to accommodate an additional row for phone number

            JTextField nameField = new JTextField(15);
            JTextField locationField = new JTextField(15);
            JTextField phoneNumberField = new JTextField(15); // New text field for phone number

            add(new JLabel("Company Name:"));
            add(nameField);
            add(new JLabel("Location:"));
            add(locationField);
            add(new JLabel("Phone Number:")); // Label for phone number
            add(phoneNumberField);

            JButton addButton = new JButton("Add Company");
            add(addButton);

            addButton.addActionListener(e -> addCompany(nameField, locationField, phoneNumberField));
        }

        private void addCompany(JTextField nameField, JTextField locationField, JTextField phoneNumberField) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
                return;
            }

            try {
                String insertQuery = "INSERT INTO company (name, location, phoneNumber) VALUES (?, ?, ?)"; // Updated query
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                preparedStatement.setString(1, nameField.getText());
                preparedStatement.setString(2, locationField.getText());
                preparedStatement.setString(3, phoneNumberField.getText()); // Set phone number value

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Company added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding company.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding company.");
            }
        }
    }


    // View User Frame
    class ViewUserFrame extends JFrame {
        public ViewUserFrame() {
            setTitle("View User");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            JTextField userIdField = new JTextField(15);
            JButton viewButton = new JButton("View");

            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter User ID:"));
            panel.add(userIdField);
            panel.add(viewButton);
            add(panel, BorderLayout.CENTER);

            JTextArea userDetailsArea = new JTextArea();
            userDetailsArea.setEditable(false);
            add(new JScrollPane(userDetailsArea), BorderLayout.SOUTH);

            viewButton.addActionListener(e -> {
                int userId = Integer.parseInt(userIdField.getText());
                displayUserDetails(userId, userDetailsArea);
            });
        }

        private void displayUserDetails(int userId, JTextArea userDetailsArea) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                userDetailsArea.setText("Failed to connect to the database.");
                return;
            }

            try {
                String selectQuery = "SELECT * FROM appuser WHERE user_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String details = "User ID: " + resultSet.getInt("user_id") + "\n" +
                            "Username: " + resultSet.getString("username") + "\n" +
                            "Role: " + resultSet.getString("role");
                    userDetailsArea.setText(details);
                } else {
                    userDetailsArea.setText("No user found with ID: " + userId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                userDetailsArea.setText("Error fetching user details.");
            }
        }
    }

    // Add User Frame
    class AddUserFrame extends JFrame {
        public AddUserFrame() {
            setTitle("Add User");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new GridLayout(3, 2));

            JTextField usernameField = new JTextField(15);
            JTextField roleField = new JTextField(15);

            add(new JLabel("Username:"));
            add(usernameField);
            add(new JLabel("Role:"));
            add(roleField);

            JButton addButton = new JButton("Add User");
            add(addButton);

            addButton.addActionListener(e -> addUser(usernameField, roleField));
        }

        private void addUser(JTextField usernameField, JTextField roleField) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
                return;
            }

            try {
                String insertQuery = "INSERT INTO appuser (username, role) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                preparedStatement.setString(1, usernameField.getText());
                preparedStatement.setString(2, roleField.getText());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "User added successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error adding user.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding user.");
            }
        }
    }

    // View Advance Salary Frame
    class ViewAdvanceSalaryFrame extends JFrame {
        public ViewAdvanceSalaryFrame() {
            setTitle("View Advance Salary");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            // Create text field for entering employee ID
            JTextField employeeIdField = new JTextField(15);
            JButton viewButton = new JButton("View");

            // Panel for employee ID input and view button
            JPanel panel = new JPanel();
            panel.add(new JLabel("Enter Employee ID:"));
            panel.add(employeeIdField);
            panel.add(viewButton);
            add(panel, BorderLayout.CENTER);

            // Text area to display advance salary details
            JTextArea advanceSalaryArea = new JTextArea();
            advanceSalaryArea.setEditable(false);
            add(new JScrollPane(advanceSalaryArea), BorderLayout.SOUTH);

            // Action listener for the "View" button
            viewButton.addActionListener(e -> {
                int employeeId = Integer.parseInt(employeeIdField.getText());
                displayAdvanceSalary(employeeId, advanceSalaryArea);
            });
        }

        private void displayAdvanceSalary(int employeeId, JTextArea advanceSalaryArea) {
            Connection connection = DBConnection.getConnection();
            if (connection == null) {
                advanceSalaryArea.setText("Failed to connect to the database.");
                return;
            }

            try {
                // SQL query to fetch status from advance_salary table
                String selectQuery = "SELECT employee_id, advance_amount, balance,companyId, date_issued, status " +
                        "FROM advance_salary " +
                        "WHERE employee_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                preparedStatement.setInt(1, employeeId);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Check if the query returned any results
                if (resultSet.next()) {
                    // Format and display the fetched data in the JTextArea
                    String details = "Employee ID: " + resultSet.getInt("employee_id") + "\n" +
                            "Advance Salary: " + resultSet.getDouble("advance_amount") + "\n" +
                            "Date Issued: " + resultSet.getDate("date_issued") + "\n" +
                            "Status: " + resultSet.getString("status");
                    advanceSalaryArea.setText(details);
                } else {
                    advanceSalaryArea.setText("No advance salary found for employee ID: " + employeeId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                advanceSalaryArea.setText("Error fetching advance salary details.");
            }
        }
    }
}