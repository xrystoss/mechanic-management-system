import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;


import net.miginfocom.swing.MigLayout;

public class Customer {

  String type;
  String phone;
  String name;
  String surname;

  Customer(String type, String phone, String name, String surname) {
    this.type = type;
    this.phone = phone;
    this.name = name;
    this.surname = surname;
  }

  static void addCustomer() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();
    // Create and configure the form components
    JLabel lblCustomerType = new JLabel("Customer Type: *");
    String[] customerTypes = { "Citizen", "Company" };
    JComboBox<String> cmbCustomerType = new JComboBox<>(customerTypes);

    JLabel lblMobilePhone = new JLabel("Mobile Phone: *");
    JTextField txtMobilePhone = new JTextField(20);

    JLabel lblName = new JLabel("Name: *");
    JTextField txtName = new JTextField(20);

    JLabel lblSurname = new JLabel("Surname:");
    JTextField txtSurname = new JTextField(20);

    // Create the panel for the form
    JPanel formPanel = new JPanel(
      new MigLayout("wrap 2", "[][]", "[]10[]10[]10[]")
    ); // Wrap after 2 components
    formPanel.add(lblCustomerType);
    formPanel.add(cmbCustomerType);
    formPanel.add(lblMobilePhone);
    formPanel.add(txtMobilePhone);
    formPanel.add(lblName);
    formPanel.add(txtName);
    formPanel.add(lblSurname);
    formPanel.add(txtSurname);

    // Create and configure the close button
    JButton closeButton = new JButton("Close");
    JButton insertButton = new JButton("Save"); // Button to insert values into database

    // Add the close button to the form panel
    formPanel.add(insertButton, "span 1"); // Span 2 cells and center the button
    formPanel.add(closeButton, "span 1"); // Span 2 cells and center the button

    closeButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mainpanel.remove(formPanel); // Remove the form panel from the main panel
          mainpanel.revalidate(); // Refresh the layout to reflect changes
          mainpanel.repaint(); // Repaint to reflect changes
        }
      }
    );

    insertButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String customerType = cmbCustomerType.getSelectedItem().toString();
          String mobilePhone = txtMobilePhone.getText();
          String name = txtName.getText();
          String surname = txtSurname.getText();

          // Check if customer type, mobile phone, and name fields are empty
          if (
            customerType.isEmpty() || mobilePhone.isEmpty() || name.isEmpty()
          ) {
            JOptionPane.showMessageDialog(
              frame,
              "Please fill in all required fields."
            );
            return; // Exit the method without inserting into the database
          }
          // Check if the phone number already exists
          String checkQuery =
            "SELECT COUNT(*) FROM Customers WHERE mobile_phone = ?";
          try (
            PreparedStatement checkStatement = sindesi.prepareStatement(
              checkQuery
            )
          ) {
            checkStatement.setString(1, mobilePhone);
            ResultSet resultSet = checkStatement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);

            if (count > 0) {
              JOptionPane.showMessageDialog(
                frame,
                "Phone number already exists"
              );
            } else {
              // Insert the values into the database
              try {
                String query =
                  "INSERT INTO Customers (mobile_phone, surname, name, customer_type) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = sindesi.prepareStatement(
                  query
                );
                preparedStatement.setString(1, mobilePhone);
                preparedStatement.setString(2, surname);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, customerType);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                  JOptionPane.showMessageDialog(
                    frame,
                    "New customer added successfully"
                  );
                }
              } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                  frame,
                  "Error: Unable to add customer"
                );
              }
            }
          } catch (HeadlessException | SQLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(
              frame,
              "Error: Unable to add customer"
            );
          }
        }
      }
    );

    // Display the form in the main panel
    mainpanel.removeAll(); // Clear previous components from the main panel
    mainpanel.add(formPanel, BorderLayout.CENTER);
    mainpanel.revalidate(); // Refresh the layout to display the new components
    mainpanel.repaint(); // Repaint to reflect changes
  }

static void searchcustomer() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();
    
    // Create a table to display search results for customers
    String[] columnNames = {
        "Mobile Phone",
        "Surname",
        "Name",
        "Customer Type",
    };
DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Make all cells non-editable
    }
};    JTable resultTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(resultTable);

    // Create a table to display search results for vehicles
    String[] columnNamesVehicles = {
        "License Plate",
        "Brand",
        "Model",
        "Year of Manufacture",
        "Vehicle Mileage",
    };
    DefaultTableModel tableModelVehicles = new DefaultTableModel(columnNamesVehicles, 0);
    JTable resultTableVehicles = new JTable(tableModelVehicles);
    JScrollPane scrollPaneVehicles = new JScrollPane(resultTableVehicles);

    // Create and configure the form components
    JLabel lblMobilePhoneSearch = new JLabel("Enter Mobile Phone:");
    JTextField txtMobilePhoneSearch = new JTextField(20);
    JLabel lblNameSearch = new JLabel("Enter Name:");
    JTextField txtNameSearch = new JTextField(20);
    JLabel lblSurnameSearch = new JLabel("Enter Surname:");
    JTextField txtSurnameSearch = new JTextField(20);

    JButton searchButton = new JButton("Search");
    JButton closeButton = new JButton("Close");
    JButton editButton = new JButton("Edit");
    editButton.setEnabled(false); // Initially disabled

    // Add the search button to the form panel
    JPanel searchPanel = new JPanel(new MigLayout("wrap 3", "[][]", "[]10"));
    searchPanel.add(lblNameSearch);
    searchPanel.add(txtNameSearch,"span");
    searchPanel.add(lblSurnameSearch);
    searchPanel.add(txtSurnameSearch,"span");
    searchPanel.add(lblMobilePhoneSearch);
    searchPanel.add(txtMobilePhoneSearch,"span");
    searchPanel.add(closeButton);
    searchPanel.add(searchButton);
    searchPanel.add(editButton);

    // Create a panel to contain the search form and the tables
    JPanel searchResultPanel = new JPanel(new BorderLayout());
    searchResultPanel.add(searchPanel, BorderLayout.NORTH);
    
    // Create a panel to contain the tables
    JPanel tablePanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
    tablePanel.add(scrollPane); // Add the customer table to the top row
    tablePanel.add(scrollPaneVehicles); // Add the vehicles table to the bottom row

    // Add the table panel to the search result panel
    searchResultPanel.add(tablePanel, BorderLayout.CENTER);

    // Function to load all customers
    Runnable loadAllCustomers = () -> {
        try {
            String query = "SELECT * FROM Customers";
            PreparedStatement preparedStatement = sindesi.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            tableModel.setRowCount(0); // Clear previous data

            while (resultSet.next()) {
                String mobilePhone = resultSet.getString("mobile_phone");
                String customerType = resultSet.getString("customer_type");
                String name = resultSet.getString("name");
                String surname = resultSet.getString("surname");

                tableModel.addRow(new Object[]{mobilePhone, surname, name, customerType});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading customers");
        }
    };

    // Function to load vehicles for a customer
    BiConsumer<String, DefaultTableModel> loadVehicles = (mobilePhone, model) -> {
        try {
            model.setRowCount(0); // Clear previous vehicles
            
            if (mobilePhone == null || mobilePhone.isEmpty()) {
                return;
            }

            String query = "SELECT * FROM Vehicles WHERE mobile_phone=?";
            PreparedStatement preparedStatement = sindesi.prepareStatement(query);
            preparedStatement.setString(1, mobilePhone);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String licensePlate = resultSet.getString("licence_plate");
                String brand = resultSet.getString("brand");
                String modelName = resultSet.getString("model");
                String year = resultSet.getString("year_of_manufacture");
                String miles = resultSet.getString("vehicle_mileage");

                model.addRow(new Object[]{licensePlate, brand, modelName, year, miles});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading vehicles");
        }
    };

    // Add double-click listener to customer table
  // Add mouse listener to handle row selection and double-click
resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int row = resultTable.rowAtPoint(e.getPoint());
            if (row >= 0) {
                resultTable.setRowSelectionInterval(row, row); // Select the row
                String mobilePhone = (String) tableModel.getValueAt(row, 0);
                loadVehicles.accept(mobilePhone, tableModelVehicles);
                editButton.setEnabled(true);
            }
        }
    }
});
    editButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = resultTable.getSelectedRow();
            if (selectedRow >= 0) {
                String oldMobilePhone = tableModel.getValueAt(selectedRow, 0).toString();
                String surname = tableModel.getValueAt(selectedRow, 1).toString();
                String name = tableModel.getValueAt(selectedRow, 2).toString();
                String customerType = tableModel.getValueAt(selectedRow, 3).toString();

                // Populate a form with the retrieved data
                JTextField txtMobilePhone = new JTextField(oldMobilePhone);
                JTextField txtSurname = new JTextField(surname);
                JTextField txtName = new JTextField(name);
                JTextField txtCustomerType = new JTextField(customerType);

                // Construct a JPanel to contain the form components
                JPanel editFormPanel = new JPanel(new GridLayout(4, 2));
                editFormPanel.add(new JLabel("Mobile Phone:*"));
                editFormPanel.add(txtMobilePhone);
                editFormPanel.add(new JLabel("Surname:"));
                editFormPanel.add(txtSurname);
                editFormPanel.add(new JLabel("Name:*"));
                editFormPanel.add(txtName);
                editFormPanel.add(new JLabel("Customer Type:*"));
                editFormPanel.add(txtCustomerType);

                // Show the form in a dialog
                int option = JOptionPane.showConfirmDialog(
                    frame,
                    editFormPanel,
                    "Edit Customer",
                    JOptionPane.OK_CANCEL_OPTION
                );
                
                String newMobilePhone = txtMobilePhone.getText();
                surname = txtSurname.getText();
                name = txtName.getText();
                customerType = txtCustomerType.getText();
                
                if (name.isEmpty() || customerType.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Missing information", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (option == JOptionPane.OK_OPTION) {
                    try {
                        // Check if the new phone number already exists
                        String checkQuery = "SELECT COUNT(*) FROM Customers WHERE mobile_phone = ?";
                        PreparedStatement checkStatement = sindesi.prepareStatement(checkQuery);
                        checkStatement.setString(1, newMobilePhone);
                        ResultSet resultSet = checkStatement.executeQuery();
                        resultSet.next();
                        int count = resultSet.getInt(1);

                        if ((count > 0) && (!newMobilePhone.equals(oldMobilePhone))) {
                            JOptionPane.showMessageDialog(frame, "New phone number already exists");
                        } else if (newMobilePhone.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Phone number field cannot be empty");
                        } else {
                            String updateQuery = "UPDATE Customers SET mobile_phone=?, surname=?, name=?, customer_type=? WHERE mobile_phone=?";

                            PreparedStatement updateStatement = sindesi.prepareStatement(updateQuery);
                            updateStatement.setString(1, newMobilePhone);
                            updateStatement.setString(2, surname);
                            updateStatement.setString(3, name);
                            updateStatement.setString(4, customerType);
                            updateStatement.setString(5, oldMobilePhone);

                            int rowsUpdated = updateStatement.executeUpdate();
                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(frame, "Customer updated successfully.");
                                // Refresh the customer list
                                loadAllCustomers.run();
                                // Clear vehicles table
                                tableModelVehicles.setRowCount(0);
                            } else {
                                JOptionPane.showMessageDialog(frame, "Failed to update customer.");
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: Unable to update customer.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a customer to edit.");
            }
        }
    });

    closeButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Clear the search results and close the search functionality
            tableModel.setRowCount(0);
            tableModelVehicles.setRowCount(0);
            // Switch back to the main panel
            mainpanel.removeAll();
            mainpanel.revalidate();
            mainpanel.repaint();
        }
    });

    // Add action listener for the search button
    searchButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String mobilePhoneSearch = txtMobilePhoneSearch.getText();
            String nameSearch = txtNameSearch.getText();
            String surnameSearch = txtSurnameSearch.getText();

            try {
                String query = "SELECT * FROM Customers WHERE ";
                List<String> conditions = new ArrayList<>();
                if (!mobilePhoneSearch.isEmpty()) {
                    conditions.add("mobile_phone LIKE ?");
                }
                if (!nameSearch.isEmpty()) {
                    conditions.add("name LIKE ?");
                }
                if (!surnameSearch.isEmpty()) {
                    conditions.add("surname LIKE ?");
                }

                if (conditions.isEmpty()) {
                    loadAllCustomers.run();
                    return;
                }

                query += String.join(" AND ", conditions);
                PreparedStatement preparedStatement = sindesi.prepareStatement(query);

                int parameterIndex = 1;
                if (!mobilePhoneSearch.isEmpty()) {
                    preparedStatement.setString(parameterIndex++, "%" + mobilePhoneSearch + "%");
                }
                if (!nameSearch.isEmpty()) {
                    preparedStatement.setString(parameterIndex++, "%" + nameSearch + "%");
                }
                if (!surnameSearch.isEmpty()) {
                    preparedStatement.setString(parameterIndex, "%" + surnameSearch + "%");
                }

                ResultSet resultSet = preparedStatement.executeQuery();
                tableModel.setRowCount(0);

                while (resultSet.next()) {
                    String mobilePhone = resultSet.getString("mobile_phone");
                    String customerType = resultSet.getString("customer_type");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");

                    tableModel.addRow(new Object[]{mobilePhone, surname, name, customerType});
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(frame, "No matching customers found.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error searching for customers");
            }
        }
    });

    // Display the search form and the tables in the main panel
    mainpanel.removeAll();
    mainpanel.add(searchResultPanel, BorderLayout.CENTER);
    mainpanel.revalidate();
    mainpanel.repaint();
    
    // Load all customers initially
    loadAllCustomers.run();
}

}
