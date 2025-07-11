import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class Vehicle {

  Customer customer;
  String registration_number;
  String brand;
  String year;
  int kilometers;

  Vehicle(
    Customer pelatis,
    String RegistrationNumber,
    String Brand,
    String Model,
    String Year,
    int Kilometers
  ) {
    this.customer = pelatis;
    this.registration_number = RegistrationNumber;
    this.brand = Brand;
    this.year = Year;
    this.kilometers = Kilometers;
  }

  static void registerVehicle() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();

    // Create and configure the form components
    JLabel lblMobilePhone = new JLabel("Mobile Phone:*");
    JTextField txtMobilePhone = new JTextField(20);

    JLabel lblLicensePlate = new JLabel("License Plate:*");
    JTextField txtLicensePlate = new JTextField(20);

    JLabel lblBrand = new JLabel("Brand:*");
    JTextField txtBrand = new JTextField(20);

    JLabel lblCategory = new JLabel("Model:*");
    JTextField txtModel = new JTextField(20);

    JLabel lblYear = new JLabel("Year of Manufacture:");
    JTextField txtYear = new JTextField(10);

    JLabel lblMileage = new JLabel("Vehicle Mileage:*");
    JTextField txtMileage = new JTextField(10);

    // Create the panel for the form
    JPanel formPanel = new JPanel(
      new MigLayout("wrap 2", "[][]", "[]10[]10[]10[]")
    ); // Wrap after 2 components
    formPanel.add(lblMobilePhone);
    formPanel.add(txtMobilePhone);
    formPanel.add(lblLicensePlate);
    formPanel.add(txtLicensePlate);
    formPanel.add(lblBrand);
    formPanel.add(txtBrand);
    formPanel.add(lblCategory);
    formPanel.add(txtModel);
    formPanel.add(lblYear);
    formPanel.add(txtYear);
    formPanel.add(lblMileage);
    formPanel.add(txtMileage);

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
          String mobilePhone = txtMobilePhone.getText();
          String licensePlate = txtLicensePlate.getText();
          String brand = txtBrand.getText();
          String model = txtModel.getText();
          int year = 0;
          int mileage = 0;
          try {
            mileage = Integer.parseInt(txtMileage.getText());
            year = Integer.parseInt(txtYear.getText());
            // Proceed with the rest of your code using the valid mileage value
          } catch (NumberFormatException ex) {
            ex.printStackTrace();
            year = 0;
            mileage = 0;
          }
          // Validate mandatory fields
          if (
            mobilePhone.isEmpty() ||
            licensePlate.isEmpty() ||
            brand.isEmpty() ||
            model.isEmpty()
          ) {
            JOptionPane.showMessageDialog(
              frame,
              "Missing information",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
            return; // Exit the method if mobile phone or license plate is not provided
          }
          // Insert the values into the database
          try {
            // Check if the phone number already exists
            String checkPhoneQuery =
              "SELECT COUNT(*) FROM Customers WHERE mobile_phone = ?";
            PreparedStatement checkPhoneStatement = sindesi.prepareStatement(
              checkPhoneQuery
            );
            checkPhoneStatement.setString(1, mobilePhone);
            ResultSet phoneResultSet = checkPhoneStatement.executeQuery();
            phoneResultSet.next();
            int phoneCount = phoneResultSet.getInt(1);

            if (phoneCount == 0) {
              JOptionPane.showMessageDialog(
                frame,
                "Phone number does not exist"
              );
            } else {
              // Check if the license plate already exists
              String checkPlateQuery =
                "SELECT COUNT(*) FROM vehicles WHERE licence_plate = ?";
              PreparedStatement checkPlateStatement = sindesi.prepareStatement(
                checkPlateQuery
              );
              checkPlateStatement.setString(1, licensePlate);
              ResultSet plateResultSet = checkPlateStatement.executeQuery();
              plateResultSet.next();
              int plateCount = plateResultSet.getInt(1);

              if (plateCount > 0) {
                JOptionPane.showMessageDialog(
                  frame,
                  "License plate already exists"
                );
              } else {
                String query =
                  "INSERT INTO vehicles (mobile_phone, licence_plate, brand, model, year_of_manufacture, vehicle_mileage) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = sindesi.prepareStatement(
                  query
                );
                preparedStatement.setString(1, mobilePhone);
                preparedStatement.setString(2, licensePlate);
                preparedStatement.setString(3, brand);
                preparedStatement.setString(4, model);
                preparedStatement.setInt(5, year);
                preparedStatement.setInt(6, mileage);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                  JOptionPane.showMessageDialog(
                    frame,
                    "New vehicle added successfully"
                  );
                }
              }
            }
          } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
              frame,
              "Error: Unable to add vehicle"
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

  static void searchVehicle() {
    {
      JPanel mainpanel = GUI.getMainPanel();
      Connection sindesi = CONNECTION.getConnection();
      JFrame frame = GUI.getFrame();

      // Create and configure the components for searching vehicles
      JLabel lblSearchBy = new JLabel("Search by license number:");
      JTextField txtSearchInput = new JTextField(20);
      JButton btnSearch = new JButton("Search");
      JButton closeButton = new JButton("Close");
      JButton editButton = new JButton("Edit");
      editButton.setEnabled(false); // Enable edit button only if one row is selected

      // Create the panel for searching vehicles
      JPanel searchPanel = new JPanel(new MigLayout("wrap 3", "[][]", "[]10")); // Wrap after 2 components
      searchPanel.add(lblSearchBy);
      searchPanel.add(txtSearchInput, "span"); // Spanning 2 columns and expanding horizontally
      searchPanel.add(closeButton); // Aligned to the left, spanning 1 column and 2 rows
      searchPanel.add(btnSearch); // Spanning 2 columns and centered horizontally
      searchPanel.add(editButton);

      String[] paymentColumnNames = {
        "Mobile Phone",
        "Name",
        "Surname",
        "Vehicle",
        "Total Cost",
        "Paid Amount",
        "Date",
      };
      DefaultTableModel paymentTableModel = new DefaultTableModel(
        paymentColumnNames,
        0
      ) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false; // Make payment table non-editable
        }
      };
      JTable paymentResultTable = new JTable(paymentTableModel);
      JScrollPane paymentScrollPane = new JScrollPane(paymentResultTable);
      // Create a table to display search results for vehicles
      String[] columnNames = {
        "License Plate",
        "Phone Number",
        "Brand",
        "Model",
        "Year of Manufacture",
        "Vehicle Mileage",
      };
      DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
          return false; // Make payment table non-editable
        }
      };
      JTable resultTable = new JTable(tableModel);
      JScrollPane scrollPane = new JScrollPane(resultTable);
      // Function to load all vehicles
      Runnable loadAllVehicles = () -> {
        try {
          String query = "SELECT * FROM Vehicles";
          PreparedStatement preparedStatement = sindesi.prepareStatement(query);
          ResultSet resultSet = preparedStatement.executeQuery();

          tableModel.setRowCount(0); // Clear existing data

          while (resultSet.next()) {
            tableModel.addRow(
              new Object[] {
                resultSet.getString("licence_plate"),
                resultSet.getString("mobile_phone"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                resultSet.getString("year_of_manufacture"),
                resultSet.getString("vehicle_mileage"),
              }
            );
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(frame, "Error loading vehicles");
        }
      };
      // Function to load payments for a vehicle
      BiConsumer<String, DefaultTableModel> loadPayments = (
        licensePlate,
        model
      ) -> {
        try {
          model.setRowCount(0); // Clear previous payments

          if (licensePlate == null || licensePlate.isEmpty()) {
            return;
          }

          String query =
            "SELECT p.*, c.name, c.surname " +
            "FROM payments p " +
            "JOIN customers c ON p.mobile_phone = c.mobile_phone " +
            "WHERE p.licence_plate = ?";
          PreparedStatement preparedStatement = sindesi.prepareStatement(query);
          preparedStatement.setString(1, licensePlate);
          ResultSet resultSet = preparedStatement.executeQuery();

          while (resultSet.next()) {
            model.addRow(
              new Object[] {
                resultSet.getString("mobile_phone"),
                resultSet.getString("name"),
                resultSet.getString("surname"),
                resultSet.getString("licence_plate"),
                resultSet.getDouble("total_amount"),
                resultSet.getDouble("amount_paid"),
                resultSet.getString("entry_date"),
              }
            );
          }
        } catch (SQLException ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(frame, "Error loading payments");
        }
      };

      // Add double-click listener to vehicle table
      resultTable.addMouseListener(
        new MouseAdapter() {
          @Override // Add this annotation
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              int row = resultTable.rowAtPoint(e.getPoint());
              if (row >= 0) {
                resultTable.setRowSelectionInterval(row, row);
                String licensePlate = (String) tableModel.getValueAt(row, 0);
                loadPayments.accept(licensePlate, paymentTableModel);
                editButton.setEnabled(true);
              }
            }
          }
        }
      );

      closeButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Clear the search results and close the search functionality
            tableModel.setRowCount(0);
            // Switch back to the main panel
            mainpanel.remove(searchPanel); // Remove the form panel from the main panel
            mainpanel.remove(scrollPane); // Remove the form panel from the main panel
            mainpanel.revalidate(); // Refresh the layout to reflect changes
            mainpanel.repaint(); // Repaint to reflect changes
          }
        }
      );
      // Add action listener for the vehicle search button
      btnSearch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            String searchInput = txtSearchInput.getText();

            try {
              String query;

              query = "SELECT * FROM Vehicles WHERE licence_plate = ?";

              PreparedStatement preparedStatement = sindesi.prepareStatement(
                query
              );
              preparedStatement.setString(1, searchInput);

              ResultSet resultSet = preparedStatement.executeQuery();
              tableModel.setRowCount(0); // Clear previous search results
              boolean found = false;

              while (resultSet.next()) {
                Object[] rowData = {
                  resultSet.getString("licence_plate"),
                  resultSet.getString("mobile_phone"),
                  resultSet.getString("brand"),
                  resultSet.getString("model"),
                  resultSet.getString("year_of_manufacture"),
                  resultSet.getString("vehicle_mileage"),
                };
                tableModel.addRow(rowData);
                found = true;

                String queryPayments1 =
                  "SELECT * FROM payments WHERE licence_plate = ?";
                PreparedStatement preparedStatementPayments = sindesi.prepareStatement(
                  queryPayments1
                );
                preparedStatementPayments.setString(1, searchInput);

                ResultSet resultSetPayments = preparedStatementPayments.executeQuery();
                String firstName = null;
                String lastName = null;
                while (resultSetPayments.next()) {
                  String mobilePhone = resultSetPayments.getString(
                    "mobile_phone"
                  );

                  String licensePlate = resultSetPayments.getString(
                    "licence_plate"
                  );
                  double totalAmount = resultSetPayments.getDouble(
                    "total_amount"
                  );
                  double amountPaid = resultSetPayments.getDouble(
                    "amount_paid"
                  );
                  String paymentDate = resultSetPayments.getString(
                    "entry_date"
                  );
                  String queryPayments2 =
                    "SELECT name,surname FROM customers WHERE mobile_phone = ?";
                  PreparedStatement preparedStatementPayments2 = sindesi.prepareStatement(
                    queryPayments2
                  );
                  preparedStatementPayments2.setString(1, mobilePhone);

                  ResultSet resultSetPayments2 = preparedStatementPayments2.executeQuery();
                  while (resultSetPayments2.next()) {
                    firstName = resultSetPayments2.getString("name");
                    lastName = resultSetPayments2.getString("surname");
                  }
                  paymentTableModel.addRow(
                    new Object[] {
                      mobilePhone,
                      firstName,
                      lastName,
                      licensePlate,
                      totalAmount,
                      amountPaid,
                      paymentDate,
                    }
                  );
                }
              }

              if (!found) {
                JOptionPane.showMessageDialog(
                  null,
                  "No matching records found.",
                  "Search Results",
                  JOptionPane.INFORMATION_MESSAGE
                );
              }
              int selectedRowCount = tableModel.getRowCount();
              if (selectedRowCount == 1) {
                editButton.setEnabled(true); // Enable edit button only if one row is selected
              } else {
                editButton.setEnabled(false); // Enable edit button only if one row is selected
              }
            } catch (SQLException ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(
                null,
                "Error: Unable to perform search"
              );
            }
          }
        }
      );

      editButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Get the selected row index

            int selectedRow = tableModel.getRowCount();
            // Check if a row is selected
            if (selectedRow == 1) {
              // Retrieve data from the selected row
              selectedRow -= 1;
              String licencePlate = tableModel
                .getValueAt(selectedRow, 0)
                .toString();
              String brand = tableModel.getValueAt(selectedRow, 2).toString();
              String model = tableModel.getValueAt(selectedRow, 3).toString();
              String yearOfManufacture = tableModel
                .getValueAt(selectedRow, 4)
                .toString();
              String vehicleMileage = tableModel
                .getValueAt(selectedRow, 5)
                .toString();

              // Populate a form with the retrieved data
              JTextField txtLicensePlate = new JTextField(licencePlate);
              JTextField txtBrand = new JTextField(brand);
              JTextField txtModel = new JTextField(model); // Assuming model is the correct variable name
              JTextField txtYearOfManufacture = new JTextField(
                yearOfManufacture
              );
              JTextField txtVehicleMileage = new JTextField(vehicleMileage);

              // Construct a JPanel to contain the form components
              JPanel editFormPanel = new JPanel(new GridLayout(6, 2)); // Increase the row count to accommodate additional fields
              editFormPanel.add(new JLabel("License Plate:*"));
              editFormPanel.add(txtLicensePlate);
              editFormPanel.add(new JLabel("Brand:*"));
              editFormPanel.add(txtBrand);
              editFormPanel.add(new JLabel("Model:*"));
              editFormPanel.add(txtModel);
              editFormPanel.add(new JLabel("Year of Manufacture:"));
              editFormPanel.add(txtYearOfManufacture);
              editFormPanel.add(new JLabel("Vehicle Mileage:*"));
              editFormPanel.add(txtVehicleMileage);

              // Show the form in a dialog
              int option = JOptionPane.showConfirmDialog(
                frame,
                editFormPanel,
                "Edit Customer",
                JOptionPane.OK_CANCEL_OPTION
              );

              // If the user clicks OK, update the record in the database
              if (option == JOptionPane.OK_OPTION) {
                try {
                  String newlicensePlate = txtLicensePlate.getText();
                  brand = txtBrand.getText();
                  model = txtModel.getText();
                  yearOfManufacture = txtYearOfManufacture.getText();
                  vehicleMileage = txtVehicleMileage.getText();
                  if (
                    newlicensePlate.isEmpty() ||
                    brand.isEmpty() ||
                    model.isEmpty() ||
                    vehicleMileage.isEmpty()
                  ) {
                    JOptionPane.showMessageDialog(
                      frame,
                      "All fields are mandatory. Please fill in all the fields.",
                      "Missing Information",
                      JOptionPane.ERROR_MESSAGE
                    );
                  } else {
                    // Check if the new license plate already exists
                    String checkPlateQuery =
                      "SELECT COUNT(*) FROM Vehicles WHERE licence_plate = ?";
                    PreparedStatement checkPlateStatement = sindesi.prepareStatement(
                      checkPlateQuery
                    );
                    checkPlateStatement.setString(1, newlicensePlate);
                    ResultSet plateResultSet = checkPlateStatement.executeQuery();
                    plateResultSet.next();
                    int plateCount = plateResultSet.getInt(1);

                    if (
                      (plateCount > 0) &&
                      (!newlicensePlate.equals(licencePlate))
                    ) {
                      JOptionPane.showMessageDialog(
                        frame,
                        "New license plate already exists"
                      );
                    } else {
                      String updateQuery =
                        "UPDATE Vehicles SET licence_plate=?, brand=?, model=?, year_of_manufacture=?, vehicle_mileage=? WHERE licence_plate=?";

                      // Perform the database update using the edited data
                      PreparedStatement updateStatement = sindesi.prepareStatement(
                        updateQuery
                      );
                      updateStatement.setString(1, newlicensePlate); // Updated license plate
                      updateStatement.setString(2, brand); // Updated brand
                      updateStatement.setString(3, model); // Updated vehicle category
                      updateStatement.setString(4, yearOfManufacture); // Updated year of manufacture
                      updateStatement.setString(5, vehicleMileage); // Updated vehicle mileage
                      updateStatement.setString(6, licencePlate); // Existing mobile phone number to identify the record to update

                      int rowsUpdated = updateStatement.executeUpdate();
                      if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(
                          frame,
                          "Customer updated successfully."
                        );
                        // Refresh the table to reflect the changes
                        // You can implement a method to fetch and display the updated data
                        // Or you can directly update the table model with the new data
                      } else {
                        JOptionPane.showMessageDialog(
                          frame,
                          "Failed to update customer."
                        );
                      }
                    }
                  }
                } catch (SQLException ex) {
                  ex.printStackTrace();
                  JOptionPane.showMessageDialog(
                    frame,
                    "Error: Unable to update customer."
                  );
                }
              }
            } else {
              JOptionPane.showMessageDialog(
                frame,
                "Please select a row to edit."
              );
            }
          }
        }
      );
      // Display the search panel and result table in the main panel
      mainpanel.removeAll(); // Clear previous components from the main panel
      // Create a panel to hold the search form and the two tables side by side
      JPanel contentPanel = new JPanel(new BorderLayout());

      // Create a panel to hold the search form
      JPanel searchPanelWrapper = new JPanel(new BorderLayout());
      searchPanelWrapper.add(searchPanel, BorderLayout.NORTH); // Add the search panel to the top

      // Create a panel to hold the two tables side by side
      JPanel tablePanel = new JPanel(new GridLayout(2, 1)); // 1 row, 2 columns
      tablePanel.add(scrollPane); // Add the first table to the left column
      tablePanel.add(paymentScrollPane); // Add the second table to the right column

      // Add the table panel to the content panel
      contentPanel.add(tablePanel, BorderLayout.CENTER);

      // Add the search panel wrapper and the content panel to the main panel
      mainpanel.removeAll(); // Clear previous components from the main panel
      mainpanel.setLayout(new BorderLayout());
      mainpanel.add(searchPanelWrapper, BorderLayout.NORTH); // Add the search panel to the top
      mainpanel.add(contentPanel, BorderLayout.CENTER); // Add the content panel below the search panel
      mainpanel.revalidate(); // Refresh the layout to display the new components
      mainpanel.repaint(); // Repaint to reflect changes
      loadAllVehicles.run();
    }
  }
}
