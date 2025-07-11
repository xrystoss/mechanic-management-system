import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class PendingPayment {

  static void Payment() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    JFrame frame = GUI.getFrame();

    // Create and configure the pending payments of vehicles
    JButton btnAdd = new JButton("Add New");
    JButton btnSearch = new JButton("Search Payment");
    JButton btnSortName = new JButton("Sort by Name");
    JButton btnSortCost = new JButton("Sort by Cost");
    JButton btnSortDate = new JButton("Sort by Date");
    JButton editButton = new JButton("Edit");
    editButton.setEnabled(false); // Initially disable the button

    JPanel ButtonsPanel = new JPanel(
      new MigLayout("wrap 5", "[]10[]10[]10[]10[]", "[]10")
    ); // Wrap after 2 components
    ButtonsPanel.add(btnAdd);
    ButtonsPanel.add(btnSearch, "growx"); // Spanning 2 columns and expanding horizontally
    ButtonsPanel.add(btnSortName, "growx"); // Aligned to the left, spanning 1 column and 2 rows
    ButtonsPanel.add(btnSortCost, "growx"); // Spanning 2 columns and centered horizontally
    ButtonsPanel.add(btnSortDate, "growx"); // Spanning 2 columns and centered horizontally
    ButtonsPanel.add(editButton, "growx");

    String[] columnNames = {
      "Mobile Phone",
      "Name",
      "Surname",
      "Vehicle",
      "Total Cost",
      "Paid Amount",
      "Date",
    };
    DefaultTableModel tableModelProducts = new DefaultTableModel(
      columnNames,
      0
    );
    JTable resultTableProducts = new JTable(tableModelProducts);
    JScrollPane scrollPaneProducts = new JScrollPane(resultTableProducts);

    try {
      String query =
        "SELECT p.mobile_phone, c.name, c.surname, p.licence_plate, p.total_amount, p.amount_paid,entry_date FROM payments p JOIN customers c ON p.mobile_phone = c.mobile_phone";
      PreparedStatement preparedStatement = sindesi.prepareStatement(query);
      ResultSet resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        String mobilePhone = resultSet.getString("mobile_phone");
        String firstName = resultSet.getString("name");
        String lastName = resultSet.getString("surname");
        String licensePlate = resultSet.getString("licence_plate");
        double totalAmount = resultSet.getDouble("total_amount");
        double amountPaid = resultSet.getDouble("amount_paid");
        String paymentDate = resultSet.getString("entry_date");

        tableModelProducts.addRow(
          new Object[] {
            mobilePhone,
            firstName,
            lastName,
            licensePlate,
            (double) totalAmount, // Casting to double
            (double) amountPaid, // Casting to double
            paymentDate,
          }
        );
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(frame, "Error: Unable to fetch payments");
    }

    // Display the search panel and result table in the main panel
    mainpanel.removeAll(); // Clear previous components from the main panel
    mainpanel.setLayout(new BorderLayout());
    mainpanel.add(ButtonsPanel, BorderLayout.NORTH);
    mainpanel.add(scrollPaneProducts, BorderLayout.CENTER);
    mainpanel.revalidate(); // Refresh the layout to display the new components
    mainpanel.repaint(); // Repaint to reflect changes

    TableRowSorter<TableModel> sorter = new TableRowSorter<>(
      tableModelProducts
    );
    resultTableProducts.setRowSorter(sorter);
    editButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Get the selected row index
          int selectedRow = resultTableProducts.getSelectedRow();
          if (sorter != null) {
            // Translate the view's selected row index to the model's selected row index
            selectedRow = sorter.convertRowIndexToModel(selectedRow);
          }
          int paymentId = 0;
          // Retrieve data from the selected row
          String mobilePhone = tableModelProducts
            .getValueAt(selectedRow, 0)
            .toString();
          String firstName = tableModelProducts
            .getValueAt(selectedRow, 1)
            .toString();
          String lastName = tableModelProducts
            .getValueAt(selectedRow, 2)
            .toString();
          String licensePlate = tableModelProducts
            .getValueAt(selectedRow, 3)
            .toString();
          double totalCost = (double) tableModelProducts.getValueAt(
            selectedRow,
            4
          );
          double paidAmount = (double) tableModelProducts.getValueAt(
            selectedRow,
            5
          );
          String date = tableModelProducts
            .getValueAt(selectedRow, 6)
            .toString();

          // Create text fields to display the data
          JTextField txtMobilePhone = new JTextField(mobilePhone);
          JTextField txtFirstName = new JTextField(firstName);
          JTextField txtLastName = new JTextField(lastName);
          JTextField txtLicensePlate = new JTextField(licensePlate);
          JTextField txtTotalCost = new JTextField(String.valueOf(totalCost));
          JTextField txtPaidAmount = new JTextField(String.valueOf(paidAmount));
          JTextField txtDate = new JTextField(date);

          // Create a panel to contain the text fields
          JPanel editPanel = new JPanel(new GridLayout(7, 2));
          editPanel.add(new JLabel("Mobile Phone:"));
          editPanel.add(txtMobilePhone);
          editPanel.add(new JLabel("First Name:"));
          editPanel.add(txtFirstName);
          editPanel.add(new JLabel("Last Name:"));
          editPanel.add(txtLastName);
          editPanel.add(new JLabel("License Plate:"));
          editPanel.add(txtLicensePlate);
          editPanel.add(new JLabel("Total Cost:"));
          editPanel.add(txtTotalCost);
          editPanel.add(new JLabel("Paid Amount:"));
          editPanel.add(txtPaidAmount);
          editPanel.add(new JLabel("Date:"));
          editPanel.add(txtDate);

          // Show the edit panel in a dialog
          int option = JOptionPane.showConfirmDialog(
            frame,
            editPanel,
            "Edit Payment",
            JOptionPane.OK_CANCEL_OPTION
          );
          if (option == JOptionPane.OK_OPTION) {
            // Get the edited values from the text fields
            String editedMobilePhone = txtMobilePhone.getText();
            String editedFirstName = txtFirstName.getText();
            String editedLastName = txtLastName.getText();
            String editedLicensePlate = txtLicensePlate.getText();
            double editedTotalCost = Double.parseDouble(txtTotalCost.getText());
            double editedPaidAmount = Double.parseDouble(
              txtPaidAmount.getText()
            );
            String editedDate = txtDate.getText();

            if (editedPaidAmount < 0 || editedTotalCost < 0) {
              JOptionPane.showMessageDialog(frame, "Negative numbers ");
              return;
            }

            if (editedPaidAmount > editedTotalCost ) {
              JOptionPane.showMessageDialog(frame, "Paid amount larger than total cost ");
              return;
            }

            try {
              // Prepare the SQL select statement to retrieve payment_id
              String selectQuery =
                "SELECT payment_id FROM payments WHERE entry_date=? AND total_amount=? AND licence_plate=? AND mobile_phone=? AND amount_paid=?";
              PreparedStatement selectStatement = sindesi.prepareStatement(
                selectQuery
              );

              // Set the parameters for the select statement
              selectStatement.setString(1, date);
              selectStatement.setDouble(2, totalCost);
              selectStatement.setString(3, licensePlate);
              selectStatement.setString(4, mobilePhone);
              selectStatement.setDouble(5, paidAmount);

              // Execute the select statement
              ResultSet resultSet = selectStatement.executeQuery();
              if (resultSet.next()) {
                // Retrieve the payment_id from the result set
                paymentId = resultSet.getInt("payment_id");
                // Do something with the paymentId, such as updating the row data in the table model
              } else {
                JOptionPane.showMessageDialog(
                  frame,
                  "No matching payment found"
                );
              }
            } catch (SQLException ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(
                frame,
                "Error: Unable to update payment"
              );
            }
            try {
              // Check if amount paid is equal to total amount
              if (editedPaidAmount == editedTotalCost) {
                // Prepare the SQL delete statement
                String deleteQuery =
                  "DELETE FROM payments WHERE mobile_phone=? AND licence_plate=? AND payment_id=?";
                PreparedStatement deleteStatement = sindesi.prepareStatement(
                  deleteQuery
                );

                // Set the parameters for the delete statement
                deleteStatement.setString(1, mobilePhone); // Use the original mobile phone as the condition
                deleteStatement.setString(2, editedLicensePlate); // Use the original license plate as the condition
                deleteStatement.setInt(3, paymentId); // Use the original license plate as the condition

                // Execute the delete statement
                int rowsDeleted = deleteStatement.executeUpdate();

                if (rowsDeleted > 0) {
                  // Remove the row from the table model
                  tableModelProducts.removeRow(selectedRow);

                  JOptionPane.showMessageDialog(
                    frame,
                    "Payment deleted successfully"
                  );
                } else {
                  JOptionPane.showMessageDialog(
                    frame,
                    "Failed to delete payment"
                  );
                }
              } else {
                // Prepare the SQL update statement
                String updateQuery =
                  "UPDATE payments SET mobile_phone=?, total_amount=?, amount_paid=?, licence_plate=?, entry_date=? WHERE mobile_phone=? AND licence_plate=? AND payment_id=?";
                PreparedStatement updateStatement = sindesi.prepareStatement(
                  updateQuery
                );

                // Set the parameters for the update statement
                updateStatement.setString(1, editedMobilePhone);
                updateStatement.setDouble(2, editedTotalCost);
                updateStatement.setDouble(3, editedPaidAmount);
                updateStatement.setString(4, editedLicensePlate);
                updateStatement.setString(5, editedDate);
                updateStatement.setString(6, mobilePhone); // Use the original mobile phone as the condition
                updateStatement.setString(7, editedLicensePlate); // Use the original mobile phone as the condition
                updateStatement.setInt(8, paymentId); // Use the original mobile phone as the condition

                // Execute the update statement
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                  // Update the row data in the table model
                  tableModelProducts.setValueAt(
                    editedMobilePhone,
                    selectedRow,
                    0
                  );
                  tableModelProducts.setValueAt(
                    editedFirstName,
                    selectedRow,
                    1
                  );
                  tableModelProducts.setValueAt(editedLastName, selectedRow, 2);
                  tableModelProducts.setValueAt(
                    editedLicensePlate,
                    selectedRow,
                    3
                  );
                  tableModelProducts.setValueAt(
                    editedTotalCost,
                    selectedRow,
                    4
                  );
                  tableModelProducts.setValueAt(
                    editedPaidAmount,
                    selectedRow,
                    5
                  );
                  tableModelProducts.setValueAt(editedDate, selectedRow, 6);

                  JOptionPane.showMessageDialog(
                    frame,
                    "Payment updated successfully"
                  );
                } else {
                  JOptionPane.showMessageDialog(
                    frame,
                    "Failed to update payment"
                  );
                }
              }
            } catch (SQLException ex) {
              ex.printStackTrace();
              JOptionPane.showMessageDialog(
                frame,
                "Error: Unable to update payment"
              );
            }
          }
        }
      }
    );
    // Add action listener for btnSortName (Sort by Name)
    // Array to hold the sorting direction flag
    final boolean[] nameAscending = { true };

    btnSortName.addActionListener(
      new ActionListener() {
        @SuppressWarnings("rawtypes")
        @Override
        public void actionPerformed(ActionEvent e) {
          // Get the index of the "Name" column
          int columnIndex = tableModelProducts.findColumn("Name");

          // Sort the data in the "Name" column using a custom comparator
          Collections.sort(
            tableModelProducts.getDataVector(),
            new Comparator<Vector>() {
              @Override
              public int compare(Vector o1, Vector o2) {
                // Extract the names from the vectors
                String name1 = o1.get(columnIndex).toString();
                String name2 = o2.get(columnIndex).toString();
                // Compare the names based on sorting direction
                if (nameAscending[0]) {
                  return name1.compareTo(name2);
                } else {
                  return name2.compareTo(name1);
                }
              }
            }
          );

          // Reverse the sorting direction flag
          nameAscending[0] = !nameAscending[0];

          // Notify the table that the data has changed
          tableModelProducts.fireTableDataChanged();
        }
      }
    );

    // Add action listener for btnSortCost (Sort by Cost)
    // Array to hold the sorting direction flag
    final boolean[] costAscending = { true };

  btnSortCost.addActionListener(new ActionListener() {
    @SuppressWarnings({"rawtypes"})
    @Override
    public void actionPerformed(ActionEvent e) {
        // Get the index of the "Total Cost" column
        int columnIndex = tableModelProducts.findColumn("Total Cost");
        
        // Sort the data
        Collections.sort(tableModelProducts.getDataVector(), 
            new Comparator<Vector>() {
                @Override
                public int compare(Vector row1, Vector row2) {
                    try {
                        // Parse the values as doubles directly
                        double value1 = parseCostValue(row1.get(columnIndex));
                        double value2 = parseCostValue(row2.get(columnIndex));
                        
                        // Compare based on current sort direction
                        return costAscending[0] 
                            ? Double.compare(value1, value2)
                            : Double.compare(value2, value1);
                    } catch (Exception ex) {
                        return 0; // fallback for invalid values
                    }
                }
                
                private double parseCostValue(Object value) {
                    if (value == null) return 0.0;
                    
                    // Handle different numeric types
                    if (value instanceof Number) {
                        return ((Number)value).doubleValue();
                    }
                    
                    // Handle string representations
                    String strValue = value.toString().trim();
                    
                    // Remove any currency symbols or commas
                    strValue = strValue.replaceAll("[^\\d.]", "");
                    
                    // Parse as double
                    return Double.parseDouble(strValue.isEmpty() ? "0" : strValue);
                }
            });
        
        // Toggle sort direction
        costAscending[0] = !costAscending[0];
        
        // Refresh the table
        tableModelProducts.fireTableDataChanged();
    }
});
    // Array to hold the sorting direction flag
    final boolean[] dateAscending = { true };

    btnSortDate.addActionListener(
      new ActionListener() {
        @SuppressWarnings("rawtypes")
        @Override
        public void actionPerformed(ActionEvent e) {
          // Get the index of the "Date" column
          int columnIndex = tableModelProducts.findColumn("Date");

          // Sort the data in the "Date" column using a custom comparator
          Collections.sort(
            tableModelProducts.getDataVector(),
            new Comparator<Vector>() {
              @Override
              public int compare(Vector o1, Vector o2) {
                // Extract the date strings from the vectors
                String dateStr1 = o1.get(columnIndex).toString();
                String dateStr2 = o2.get(columnIndex).toString();

                // Parse the date strings into Date objects
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = null;
                Date date2 = null;
                try {
                  date1 = dateFormat.parse(dateStr1);
                  date2 = dateFormat.parse(dateStr2);
                } catch (ParseException ex) {
                  ex.printStackTrace();
                }

                // Compare the dates based on sorting direction
                if (dateAscending[0]) {
                  return date1.compareTo(date2);
                } else {
                  return date2.compareTo(date1);
                }
              }
            }
          );

          // Reverse the sorting direction flag
          dateAscending[0] = !dateAscending[0];

          // Notify the table that the data has changed
          tableModelProducts.fireTableDataChanged();
        }
      }
    );

    resultTableProducts
      .getSelectionModel()
      .addListSelectionListener(
        new ListSelectionListener() {
          @Override
          public void valueChanged(ListSelectionEvent e) {
            // Check if a row is selected
            if (
              !e.getValueIsAdjusting() &&
              resultTableProducts.getSelectedRow() != -1
            ) {
              // Enable the "Edit" button
              editButton.setEnabled(true);
            } else {
              // No row is selected, disable the "Edit" button
              editButton.setEnabled(false);
            }
          }
        }
      );

    // for Search Vehicle Button
    btnSearch.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Create components for searching payments by phone number
          JLabel lblPhoneNumber = new JLabel("Search customer:");
          JTextField txtPhoneNumber = new JTextField(20);
          JButton search = new JButton("Search");
          JButton cancel = new JButton("Cancel");

          JPanel searchPanel = new JPanel(
            new MigLayout("wrap 2", "[][]", "[]10")
          );
          mainpanel.remove(ButtonsPanel);
          mainpanel.revalidate();
          mainpanel.repaint();
          searchPanel.add(lblPhoneNumber);
          searchPanel.add(txtPhoneNumber);
          searchPanel.add(search);
          searchPanel.add(cancel);
          searchPanel.add(editButton);

          // Add the search panel to the main panel above the table
          mainpanel.add(searchPanel, BorderLayout.NORTH); // Add search panel to the top

          // Refresh the layout to display the search panel

          // Action listener for the search button
          search.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                // Get the phone number entered in the text field
                String phoneNumber = txtPhoneNumber.getText();

                try {
                  // Query to retrieve data from payments table
                  String paymentsQuery =
                    "SELECT * FROM payments WHERE mobile_phone = ?";
                  PreparedStatement paymentsStatement = sindesi.prepareStatement(
                    paymentsQuery
                  );
                  paymentsStatement.setString(1, phoneNumber);
                  ResultSet paymentsResultSet = paymentsStatement.executeQuery();

                  // Clear the table before adding new search results
                  tableModelProducts.setRowCount(0);
                  double amountPaid = 0;
                  double totalAmount = 0;
                  String licensePlate = null;
                  String mobilePhone = null;
                  String firstName = null;
                  String lastName = null;
                  String paymentDate = null; // Initialize payment date variable

                  // Process the search results from payments table and add them to the table
                  if (paymentsResultSet.next()) {
                    // Retrieve payment details from the ResultSet
                    mobilePhone = paymentsResultSet.getString("mobile_phone");
                    licensePlate = paymentsResultSet.getString("licence_plate");
                    totalAmount = paymentsResultSet.getDouble("total_amount");
                    amountPaid = paymentsResultSet.getDouble("amount_paid");
                    paymentDate = paymentsResultSet.getString("entry_date");
                    // Add the payment details to the table model
                  } else {
                    JOptionPane.showMessageDialog(frame, "No payments found ");
                    return;
                  }
                  String customersQuery =
                    "SELECT name, surname FROM customers WHERE mobile_phone = ?";
                  PreparedStatement customersStatement = sindesi.prepareStatement(
                    customersQuery
                  );
                  customersStatement.setString(1, phoneNumber);
                  ResultSet customersResultSet = customersStatement.executeQuery();

                  // Process the search results from customers table and display customer details
                  if (customersResultSet.next()) {
                    firstName = customersResultSet.getString("name");
                    lastName = customersResultSet.getString("surname");
                  }
                  tableModelProducts.addRow(
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
                } catch (SQLException ex) {
                  ex.printStackTrace();
                  JOptionPane.showMessageDialog(
                    frame,
                    "Error: Unable to search payments"
                  );
                }
              }
            }
          );

          // Action listener for the cancel button
          cancel.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                // Remove the search panel from the main panel
                mainpanel.remove(searchPanel);
                mainpanel.add(ButtonsPanel);

                // Refresh the layout to reflect changes
                mainpanel.revalidate();
                mainpanel.repaint();
              }
            }
          );
        }
      }
    );

    // for Add New Button
    btnAdd.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          JLabel lbldate = new JLabel("Date");
          JLabel lblvehicle = new JLabel("Vehicle License Plates:*");
          JLabel lbltotal_amount = new JLabel("Total Cost:*");
          JLabel lbltotal_paid = new JLabel("Paid Amount");

          JTextField txtdate = new JTextField(20);
          JTextField txtvehicle = new JTextField(20);
          JTextField txttotal_amount = new JTextField(20);
          JTextField txttotal_paid = new JTextField(20);

          JButton insert = new JButton("Insert");
          JButton cancel = new JButton("Cancel");
          JButton btnDatePicker = new JButton("Select Date");

          JPanel add_payment = new JPanel(
            new MigLayout("wrap 3", "[][]", "[]10")
          );

          add_payment.add(lbldate);
          add_payment.add(txtdate);
          add_payment.add(btnDatePicker);
          add_payment.add(lblvehicle);
          add_payment.add(txtvehicle, "span");
          add_payment.add(lbltotal_amount);
          add_payment.add(txttotal_amount, "span");
          add_payment.add(lbltotal_paid);
          add_payment.add(txttotal_paid,"span");

          add_payment.add(insert);
          add_payment.add(cancel);

          // Display the search panel and result table in the main panel
          mainpanel.removeAll(); // Clear previous components from the main panel
          mainpanel.setLayout(new BorderLayout());
          mainpanel.add(add_payment, BorderLayout.CENTER);
          mainpanel.revalidate(); // Refresh the layout to display the new components
          mainpanel.repaint(); // Repaint to reflect changes
          //pick date

          btnDatePicker.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                // Create a new frame for the date picker
                JFrame frame = new JFrame("Select Date");
                JPanel panel = new JPanel();
                frame.setBounds(400, 400, 250, 100);

                // Create a new instance of JXDatePicker
                JXDatePicker picker = new JXDatePicker();
                picker.setDate(Calendar.getInstance().getTime());
                picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));

                // Add an ActionListener to retrieve the selected date
                picker.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // Get the selected date from datePicker
                      Date selectedDate = picker.getDate();

                      // Define a SimpleDateFormat to format the date
                      SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "dd/MM/yyyy"
                      );

                      // Check if a date is selected
                      if (selectedDate != null) {
                        // Format the selected date as a string
                        String formattedDate = dateFormat.format(selectedDate);

                        // Use the formattedDate string as needed (e.g., save it to a variable)
                        System.out.println("Selected Date: " + formattedDate);
                        txtdate.setText(formattedDate);
                        frame.setVisible(false);
                        // You can save the formattedDate string to a variable or use it as needed
                      } else {
                        System.out.println("No date selected.");
                      }
                    }
                  }
                );

                // Add the picker to the panel and the panel to the frame
                panel.add(picker);
                frame.getContentPane().add(panel);

                // Make the frame visible
                frame.setVisible(true);
              }
            }
          );

          // for Search Vehicle Button

          cancel.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                mainpanel.remove(add_payment); // Remove the form panel from the main panel
                mainpanel.revalidate(); // Refresh the layout to reflect changes
                mainpanel.repaint(); // Repaint to reflect changes
              }
            }
          );

          insert.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                // Get the values entered in the text fields
                String date = txtdate.getText();
                String licensePlate = txtvehicle.getText();
                double totalAmount = 0;
                double amountPaid = 0;
                try {
                  amountPaid = Double.parseDouble(txttotal_paid.getText());
                  // Further processing with the amountPaid variable
                } catch (NumberFormatException ee) {
                  amountPaid = 0;
                }

                try {
                  totalAmount = Double.parseDouble(
                    txttotal_amount.getText()
                  );                  // Further processing with the amountPaid variable
                } catch (NumberFormatException ee) {
                  JOptionPane.showMessageDialog(frame, "You must enter total cost");
                  return;
                }

                
                if (date == null || date.isEmpty()) {
                  // Get the current date
                  SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "dd/MM/yyyy"
                  );
                  date = dateFormat.format(new Date());
                }

                if (amountPaid < 0 || totalAmount < 0) {
                  JOptionPane.showMessageDialog(frame, "Negative numbers ");
                  return;
                }

                if (amountPaid > totalAmount ) {
                  JOptionPane.showMessageDialog(frame, "Paid amount larger than total cost ");
                  return;
                }

                try {
                  // Query the vehicles table to get the mobile phone number associated with the license plate
                  String query =
                    "SELECT mobile_phone FROM vehicles WHERE licence_plate = ?";
                  PreparedStatement preparedStatement = sindesi.prepareStatement(
                    query
                  );
                  preparedStatement.setString(1, licensePlate);
                  ResultSet resultSet = preparedStatement.executeQuery();

                  // Check if a matching record is found
                  if (resultSet.next()) {
                    String mobilePhone = resultSet.getString("mobile_phone");

                    // Prepare the SQL INSERT statement
                    query =
                      "INSERT INTO payments (entry_date, total_amount, amount_paid, licence_plate, mobile_phone) VALUES (?, ?, ?, ?, ?)";
                    preparedStatement =
                      sindesi.prepareStatement(
                        query,
                        Statement.RETURN_GENERATED_KEYS
                      );
                    preparedStatement.setString(1, date);
                    preparedStatement.setDouble(2, totalAmount);
                    preparedStatement.setDouble(3, amountPaid);
                    preparedStatement.setString(4, licensePlate);
                    preparedStatement.setString(5, mobilePhone);

                    // Execute the INSERT statement
                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) {
                      JOptionPane.showMessageDialog(
                        frame,
                        "Payment inserted successfully"
                      );
                    } else {
                      JOptionPane.showMessageDialog(
                        frame,
                        "Failed to insert payment"
                      );
                    }
                  } else {
                    JOptionPane.showMessageDialog(
                      frame,
                      "No vehicle found with the provided license plate"
                    );
                  }
                } catch (SQLException ex) {
                  ex.printStackTrace();
                  JOptionPane.showMessageDialog(
                    frame,
                    "Error: Unable to insert payment"
                  );
                }
              }
            }
          );
        }
      }
    );
  }
}
