import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class Part {

  String name;
  String brand;
  String quantity;
  double cost;
  String description;

  Part(
    String name,
    String brand,
    String quantity,
    double cost,
    String description
  ) {
    this.name = name;
    this.brand = brand;
    this.quantity = quantity;
    this.cost = cost;
    this.description = description;
  }

  public static void refreshPartsTable(DefaultTableModel tableModel) {
    Connection sindesi = CONNECTION.getConnection();
    // Clear existing rows
    tableModel.setRowCount(0);
    // Populate the table with data from the parts table
    try {
      Statement statement = sindesi.createStatement();
      ResultSet resultSet = statement.executeQuery("SELECT * FROM parts");
      while (resultSet.next()) {
        String code = resultSet.getString("code");
        String name = resultSet.getString("name");
        String category = resultSet.getString("category");
        double price = resultSet.getDouble("price");
        int availability = resultSet.getInt("availability");
        String description = resultSet.getString("description");

        // Add a row to the table model
        tableModel.addRow(
          new Object[] {
            code,
            name,
            category,
            price,
            availability,
            description,
          }
        );
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  static void addPart() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();
    // Create and configure the table model for the parts table
    DefaultTableModel tableModel = new DefaultTableModel();
    JTable partsTable = new JTable(tableModel);
    JScrollPane tableScrollPane = new JScrollPane(partsTable);

    // Add columns to the table model
    tableModel.addColumn("Code");
    tableModel.addColumn("Name");
    tableModel.addColumn("Brand");
    tableModel.addColumn("Price");
    tableModel.addColumn("Availability");
    tableModel.addColumn("Description");

    // Populate the table with data from the parts table
    refreshPartsTable(tableModel);

    JLabel lblCategory = new JLabel("Brand:");
    JTextField txtCategory = new JTextField(20);


    JLabel lblName = new JLabel("Name:*");
    JTextField txtName = new JTextField(20);

    JLabel lblPrice = new JLabel("Price:");
    JTextField txtPrice = new JTextField(20);

    JLabel lblAvailability = new JLabel("Availability:*");
    JTextField availabilityOptions = new JTextField(20);

    JLabel lblDescription = new JLabel("Description:");
    JTextField txtDescription = new JTextField(20);

    // Create the panel for the form
    JPanel formPanel = new JPanel(
      new MigLayout("wrap 2", "[][]", "[]10[]10[]10[]10[]10[]10[]")
    ); // Wrap after 2 components

    formPanel.add(lblCategory);
    formPanel.add(txtCategory);
    formPanel.add(lblName);
    formPanel.add(txtName);
    formPanel.add(lblPrice);
    formPanel.add(txtPrice);
    formPanel.add(lblAvailability);
    formPanel.add(availabilityOptions);
    formPanel.add(lblDescription);
    formPanel.add(txtDescription);
    // Create panel for the table
    JPanel tablePanel = new JPanel(new BorderLayout());
    tablePanel.add(new JLabel("Parts Table"), BorderLayout.NORTH);
    tablePanel.add(tableScrollPane, BorderLayout.CENTER);
    // Create and configure the close button
    JButton closeButton = new JButton("Close");
    JButton insertButton = new JButton("Insert"); // Button to insert values into database
    formPanel.add(insertButton);
    formPanel.add(closeButton);

    // Create panel for the form and table
    JPanel mainFormPanel = new JPanel(new BorderLayout());
    mainFormPanel.add(formPanel, BorderLayout.NORTH);
    mainFormPanel.add(tablePanel, BorderLayout.CENTER);

    closeButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mainpanel.remove(mainFormPanel); // Remove the form panel from the main panel
          mainpanel.revalidate(); // Refresh the layout to reflect changes
          mainpanel.repaint(); // Repaint to reflect changes
        }
      }
    );

    insertButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String category = txtCategory.getText();
          String name = txtName.getText();
          int availability = 0;
          String description = txtDescription.getText();
          Double price;

          try {
            price = Double.parseDouble(txtPrice.getText());
          } catch (NumberFormatException ex) {
            // Handle the case where the price text is not a valid number
            price = 0.0;
          }

          try {
            availability = Integer.parseInt(availabilityOptions.getText());
          } catch (NumberFormatException ex) {
            // Handle the case where the price text is not a valid number
            availability = 1;
          }

          if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
              frame,
              "Please enter the name of the part."
            );
            return; // Exit method if name is not provided
          }

          

          // Check if amount is selected
          if (availability == 0) {
            JOptionPane.showMessageDialog(
              frame,
              "Please select the availability of the part."
            );
            return; // Exit method if amount is not selected
          }
          // Insert the values into the database
          try {
            String query =
              "INSERT INTO parts ( name, category, price, availability, description) VALUES ( ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = sindesi.prepareStatement(
              query
            );
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, category);
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, availability);
            preparedStatement.setString(5, description);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
              JOptionPane.showMessageDialog(
                frame,
                "New part added successfully"
              );
              // Refresh the table
              refreshPartsTable(tableModel);
            }
          } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error: Unable to add part");
          }
        }
      }
    );

    
    // Display the form and table in the main panel
    mainpanel.removeAll(); // Clear previous components from the main panel
    mainpanel.add(mainFormPanel, BorderLayout.CENTER);
    mainpanel.revalidate(); // Refresh the layout to display the new components
    mainpanel.repaint(); // Repaint to reflect changes
  }

  static void searchPart() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();
    String[] columnNamesProducts = {
      "Code",
      "Name",
      "Brand",
      "Price",
      "Availability",
      "Description",
    };
    DefaultTableModel tableModelProducts = new DefaultTableModel(
      columnNamesProducts,
      0
    );
    JTable resultTableProducts = new JTable(tableModelProducts);
    JScrollPane scrollPaneProducts = new JScrollPane(resultTableProducts);

    try {
      String query = "SELECT * FROM parts";
      PreparedStatement preparedStatement = sindesi.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String productCode = resultSet.getString("code");
        String productName = resultSet.getString("name");
        String category = resultSet.getString("category");
        double price = resultSet.getDouble("price");
        int availability = resultSet.getInt("availability");
        String description = resultSet.getString("description");

        tableModelProducts.addRow(
          new Object[] {
            productCode,
            productName,
            category,
            price,
            availability,
            description,
          }
        );
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "Error: Unable to fetch products");
    }

    JLabel lblCodeSearch = new JLabel("Enter Product Code:");
    JTextField txtCodeSearch = new JTextField(20);
    JLabel lblNameSearch = new JLabel("Enter Product Name:");
    JTextField txtNameSearch = new JTextField(20);
    JLabel lblCategorySearch = new JLabel("Enter Brand:");
    JTextField txtCategorySearch = new JTextField(20);

    JButton searchButtonProducts = new JButton("Search");
    JButton closeButtonProducts = new JButton("Close");
    JButton editButton = new JButton("Edit");
    editButton.setEnabled(false);
    JButton refreshButton = new JButton("Refresh");

    searchButtonProducts.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String productCodeSearch = txtCodeSearch.getText();
          String productNameSearch = txtNameSearch.getText();
          String categorySearch = txtCategorySearch.getText();

          tableModelProducts.setRowCount(0);

          try {
            String query = "SELECT * FROM parts WHERE 1=1";
            if (!productCodeSearch.isEmpty()) {
              query += " AND code=?";
            }
            if (!productNameSearch.isEmpty()) {
              query += " AND name=?";
            }
            if (!categorySearch.isEmpty()) {
              query += " AND category=?";
            }

            PreparedStatement preparedStatement = sindesi.prepareStatement(
              query
            );

            int parameterIndex = 1;
            if (!productCodeSearch.isEmpty()) {
              preparedStatement.setString(parameterIndex++, productCodeSearch);
            }
            if (!productNameSearch.isEmpty()) {
              preparedStatement.setString(parameterIndex++, productNameSearch);
            }
            if (!categorySearch.isEmpty()) {
              preparedStatement.setString(parameterIndex++, categorySearch);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean productFound = false;

            while (resultSet.next()) {
              String code = resultSet.getString("code");
              String productName = resultSet.getString("name");
              String category = resultSet.getString("category");
              double price = resultSet.getDouble("price");
              int availability = resultSet.getInt("availability");
              String description = resultSet.getString("description");

              tableModelProducts.addRow(
                new Object[] {
                  code,
                  productName,
                  category,
                  price,
                  availability,
                  description,
                }
              );
              productFound = true;
            }

            if (!productFound) {
              JOptionPane.showMessageDialog(
                frame,
                "No products found matching the criteria",
                "Product Not Found",
                JOptionPane.WARNING_MESSAGE
              );
            }
            int selectedRowCount = tableModelProducts.getRowCount();
            if (selectedRowCount == 1) {
              editButton.setEnabled(true); // Enable edit button only if one row is selected
            } else {
              editButton.setEnabled(false);
            } // Enable edit button only if one row is selected
          } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
              frame,
              "Error: Unable to search for products"
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

          int selectedRow = tableModelProducts.getRowCount();
          // Check if a row is selected
          if (selectedRow == 1) {
            // Retrieve data from the selected row
            selectedRow -= 1;
            String code = (String) tableModelProducts.getValueAt(
              selectedRow,
              0
            );
            String name = (String) tableModelProducts
              .getValueAt(selectedRow, 1)
              .toString();
            String category = (String) tableModelProducts
              .getValueAt(selectedRow, 2)
              .toString();
            String price = (String) tableModelProducts
              .getValueAt(selectedRow, 3)
              .toString();
            String availability = (String) tableModelProducts
              .getValueAt(selectedRow, 4)
              .toString();
            String description = (String) tableModelProducts
              .getValueAt(selectedRow, 5)
              .toString();

            // Populate a form with the retrieved data
            JTextField txtName = new JTextField(name);
            JTextField txtCategory = new JTextField(category);
            JTextField txtPrice = new JTextField(price);
            JTextField txtAvailability = new JTextField(availability);
            JTextField txtDescription = new JTextField(description);

            // Construct a JPanel to contain the form components
            JPanel editFormPanel = new JPanel(new GridLayout(6, 2)); // Increase the row count to accommodate additional fields
            editFormPanel.add(new JLabel("Name:*"));
            editFormPanel.add(txtName);
            editFormPanel.add(new JLabel("Brand:"));
            editFormPanel.add(txtCategory);
            editFormPanel.add(new JLabel("Price:"));
            editFormPanel.add(txtPrice);
            editFormPanel.add(new JLabel("Availability:*"));
            editFormPanel.add(txtAvailability);
            editFormPanel.add(new JLabel("Description:"));
            editFormPanel.add(txtDescription);

            // Show the form in a dialog
            int option = JOptionPane.showConfirmDialog(
              frame,
              editFormPanel,
              "Edit Part",
              JOptionPane.OK_CANCEL_OPTION
            );
            name = txtName.getText();
            category = txtCategory.getText();
            price = txtPrice.getText();
            availability = txtAvailability.getText();
            description = txtDescription.getText();

            // If the user clicks OK, update the record in the database
            if (option == JOptionPane.OK_OPTION) {
              if(name.isEmpty() || availability.isEmpty()){
                JOptionPane.showMessageDialog(
                    frame,
                    "Missing information."
                  );
                  return ;
              }
              try {
                String updateQuery =
                  "UPDATE parts SET name=?, category=?, price=?, availability=?, description=? WHERE code=?";

                // Perform the database update using the edited data
                PreparedStatement updateStatement = sindesi.prepareStatement(
                  updateQuery
                );
                updateStatement.setString(1, name); // Updated license plate
                updateStatement.setString(2, category); // Updated brand
                updateStatement.setString(3, price); // Updated vehicle category
                updateStatement.setString(4, availability); // Updated year of manufacture
                updateStatement.setString(5, description); // Updated vehicle mileage
                updateStatement.setString(6, code); // Existing mobile phone number to identify the record to update

                int rowsUpdated = updateStatement.executeUpdate();
                if (rowsUpdated > 0) {
                  JOptionPane.showMessageDialog(
                    frame,
                    "Part updated successfully."
                  );
                  // Refresh the table to reflect the changes
                  // You can implement a method to fetch and display the updated data
                  // Or you can directly update the table model with the new data
                } else {
                  JOptionPane.showMessageDialog(
                    frame,
                    "Failed to update part."
                  );
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
    JPanel searchPanelProducts = new JPanel(
      new MigLayout("wrap 4", "[][]", "[]10")
    );
    searchPanelProducts.add(lblCodeSearch);
    searchPanelProducts.add(txtCodeSearch,"span 3");
    searchPanelProducts.add(lblNameSearch);
    searchPanelProducts.add(txtNameSearch,"span 3");
    searchPanelProducts.add(lblCategorySearch);
    searchPanelProducts.add(txtCategorySearch,"span 3");
    searchPanelProducts.add(searchButtonProducts);
    searchPanelProducts.add(refreshButton);
    searchPanelProducts.add(editButton);
    searchPanelProducts.add(closeButtonProducts);


    JPanel searchResultPanelProducts = new JPanel(new BorderLayout());
    searchResultPanelProducts.add(searchPanelProducts, BorderLayout.NORTH);
    searchResultPanelProducts.add(scrollPaneProducts, BorderLayout.CENTER);
    closeButtonProducts.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          tableModelProducts.setRowCount(0);
          mainpanel.remove(searchPanelProducts);
          mainpanel.remove(searchResultPanelProducts);
          mainpanel.revalidate();
          mainpanel.repaint();
        }
      }
    );

    // Add ActionListener to the refresh button
    refreshButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Call the refreshPartsTable function and pass the table model
          refreshPartsTable(tableModelProducts);
        }
      }
    );
    mainpanel.removeAll();
    mainpanel.add(searchResultPanelProducts, BorderLayout.CENTER);
    mainpanel.revalidate();
    mainpanel.repaint();
  }
}
