import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

public class Service {

  String date;
  Vehicle vehicle;
  Part[] parts;
  Double total_cost;
  String description;

  static void Servicee() {
    JPanel mainpanel = GUI.getMainPanel();
    Connection sindesi = CONNECTION.getConnection();
    // Create and configure the form components
    JLabel lblDate = new JLabel("Date:*");
    JTextField txtDate = new JTextField(20);

    JLabel lblDescription = new JLabel("Description:*");
    JTextArea txtDescription = new JTextArea(5,20);

    JLabel lblParts = new JLabel("Parts: \n (Code, amount)");
    JTextArea txtParts = new JTextArea(5, 20); // 5 rows, 20 columns

    JLabel lblVehicle = new JLabel("Vehicles number:*");
    JTextField txtVehicle = new JTextField(10); // 5 rows, 20 columns

    DefaultTableModel partsTableModel = new DefaultTableModel();
    DefaultTableModel ServiceTableModel = new DefaultTableModel();

    JTable partsTable = new JTable(partsTableModel);
    JTable serviceTable = new JTable(ServiceTableModel);

    JScrollPane partsScrollPane = new JScrollPane(partsTable);
    JScrollPane serviceScrollPane = new JScrollPane(serviceTable);
    // Create the panel for the form
    JPanel formPanel = new JPanel(new MigLayout("wrap 3, gapx 5, gapy 5")); // Add gapx and gapy constraints
    JButton btnDatePicker = new JButton("Select Date");

    // Wrap after 2 components
    formPanel.add(lblVehicle);
    formPanel.add(txtVehicle, "span");
    formPanel.add(lblDate);
    formPanel.add(txtDate);
    formPanel.add(btnDatePicker);
    formPanel.add(lblDescription);
    formPanel.add(txtDescription, "span");
    formPanel.add(lblParts);
    formPanel.add(txtParts, "span");
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new MigLayout("wrap 5, gapx 5, gapy 5")); // Set layout manager to BoxLayout with horizontal alignment

    // Create and configure the close button
    JButton closeButton = new JButton("Close");
    JButton insertButton = new JButton("Insert"); // Button to insert values into database
    JButton saveButton = new JButton("Save"); // Button to insert values into database
    JButton searchVehicle = new JButton("Find vehicle");

    // Add the close button to the form panel
    buttonPanel.add(searchVehicle);
    buttonPanel.add(insertButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(closeButton);
    formPanel.add(buttonPanel, "gaptop 10");
    JPanel formfullpanel = new JPanel(new BorderLayout());
    formfullpanel.add(formPanel, BorderLayout.NORTH);

    // Create a panel to display the available parts in a table-like format
    JPanel tablePanel = new JPanel(new GridLayout(2, 1)); // 2 rows, 1 column
    tablePanel.add(serviceScrollPane);
    tablePanel.add(partsScrollPane);
    // Fetch parts data from the database and create a table model

    formfullpanel.add(tablePanel, BorderLayout.CENTER);

    txtDate.setEnabled(false);
    txtDescription.setEnabled(false);
    txtParts.setEnabled(false);

    partsTableModel.addColumn("Code");
    partsTableModel.addColumn("Part Name");
    partsTableModel.addColumn("Availability");

    ServiceTableModel.addColumn("License plate");
    ServiceTableModel.addColumn("Date");
    ServiceTableModel.addColumn("Parts Used");
    ServiceTableModel.addColumn("Description");
    try {
      String query = "SELECT code,name, availability FROM parts"; // Adjust the column names if needed
      Statement statement = sindesi.createStatement();
      ResultSet resultSet = statement.executeQuery(query);

      // Add data to the table model
      while (resultSet.next()) {
        String code = resultSet.getString("code");
        String name = resultSet.getString("name");
        int availability = resultSet.getInt("availability");
        partsTableModel.addRow(new Object[] { code, name, availability });
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
        null,
        "Error: Unable to fetch parts data",
        "Database Error",
        JOptionPane.ERROR_MESSAGE
      );
    }

    try {
      String query = "SELECT * FROM maintenance_log"; // Adjust the column names if needed
      Statement statement = sindesi.createStatement();
      ResultSet resultSet = statement.executeQuery(query);

      // Add data to the table model
      while (resultSet.next()) {
        String licence_plate = resultSet.getString("licence_plate");
        String date = resultSet.getString("date");
        String parts_used = resultSet.getString("parts_used");
        String description = resultSet.getString("description");

        ServiceTableModel.addRow(
          new Object[] { licence_plate, date, parts_used, description }
        );
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(
        null,
        "Error: Unable to fetch service data",
        "Database Error",
        JOptionPane.ERROR_MESSAGE
      );
    }

    closeButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          mainpanel.remove(formfullpanel); // Remove the form panel from the main panel
          mainpanel.revalidate(); // Refresh the layout to reflect changes
          mainpanel.repaint(); // Repaint to reflect changes
        }
      }
    );

    searchVehicle.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          try {
            Statement stmt = sindesi.createStatement();
            String licensePlate = txtVehicle.getText();
            ResultSet rs = stmt.executeQuery(
              "SELECT * FROM vehicles WHERE licence_plate = '" +
              licensePlate +
              "'"
            );

            if (rs.next()) {
              // If vehicle found, enable text fields and display information

              txtDate.setEnabled(true);
              txtDescription.setEnabled(true);
              txtParts.setEnabled(true);
            } else {
              // If vehicle not found, show message
              JOptionPane.showMessageDialog(
                partsScrollPane,
                "Vehicle not found",
                "Error",
                JOptionPane.ERROR_MESSAGE
              );
            }

            // Close resources
            rs.close();
            stmt.close();
          } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
              partsScrollPane,
              "Database error",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      }
    );

    insertButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Add code to insert data into the database
          String date = txtDate.getText();
          String description = txtDescription.getText();
          String parts = txtParts.getText();
          String licence_plate = txtVehicle.getText();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date inputDate = dateFormat.parse(date);
            Date currentDate = new Date(); // Current date

            if (inputDate.after(currentDate)) {
              JOptionPane.showMessageDialog(
                null,
                "Invalid date",
                "Error",
                JOptionPane.ERROR_MESSAGE
              );
              return;             }
        } catch (ParseException ee) {
            System.out.println("Invalid date format. Please enter date in dd/MM/yyyy format.");
        }
         
        
          if (
            date.isEmpty() || licence_plate.isEmpty() || description.isEmpty()
          ) {
            JOptionPane.showMessageDialog(
              null,
              "Invalid Date",
              "Error",
              JOptionPane.ERROR_MESSAGE
            );
            return; // Exit the method if mobile phone or license plate is not provided
          }

          String partsText = txtParts.getText();
          String[] lines = partsText.split("\\n");

          // Split the line into part code and amount
          for (String line : lines) {
            if (partsText.isBlank()) break;
            String[] partsInfo = line.split(",");
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            // Process part code and amount
            int partCode = Integer.parseInt(partsInfo[0].trim()); // Parse part code as integer
            int amount = Integer.parseInt(partsInfo[1].trim());
            String query =
              "SELECT name, availability FROM parts WHERE code = ?";
            try {
              // Prepare the SQL statement
              preparedStatement = sindesi.prepareStatement(query);
              preparedStatement.setInt(1, partCode);
              resultSet = preparedStatement.executeQuery();

              // Execute the query
              if (resultSet.next()) {
                // Retrieve part details from the ResultSet
                String name = resultSet.getString("name");
                int availability = resultSet.getInt("availability");
                if (availability < amount) {
                  JOptionPane.showMessageDialog(
                    null,
                    "Insufficient availability for " +
                    name +
                    ". Requested: " +
                    amount +
                    ", Available: " +
                    availability,
                    "Insufficient Availability",
                    JOptionPane.ERROR_MESSAGE
                  );
                  return;
                } else {
                  String updateQuery =
                    "UPDATE parts SET availability = ? WHERE code = ?";
                  try {
                    // Prepare the SQL statement for update
                    preparedStatement = sindesi.prepareStatement(updateQuery);

                    // Calculate new availability
                    int newAvailability = availability - amount;

                    // Set parameters for the update statement
                    preparedStatement.setInt(1, newAvailability);
                    preparedStatement.setInt(2, partCode);

                    // Execute the update query
                    int rowsUpdated = preparedStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                      System.out.println("Availability updated successfully!");
                    } else {
                      System.out.println("Failed to update availability.");
                    }
                  } catch (SQLException ee) {
                    // Handle any SQL exceptions
                    ee.printStackTrace();
                  }
                }
              }
            } catch (SQLException ex) {
              ex.printStackTrace();
              // Handle SQL exception
            }
          }
          try {
            String query1 =
              "INSERT INTO maintenance_log (date, description, parts_used,licence_plate) VALUES (?, ?, ?,?)";
            PreparedStatement preparedStatementt = sindesi.prepareStatement(
              query1
            );
            preparedStatementt.setString(1, date);
            preparedStatementt.setString(2, description);
            preparedStatementt.setString(3, parts);
            preparedStatementt.setString(4, licence_plate);

            int rowsInserted = preparedStatementt.executeUpdate();
            if (rowsInserted > 0) {
              JOptionPane.showMessageDialog(
                null,
                "New record added successfully"
              );

              // Clear the text fields after successful insertion
              txtDate.setText("");
              txtDescription.setText("");
              txtParts.setText("");
            } else {
              JOptionPane.showMessageDialog(
                null,
                "Failed to add new record",
                "Error",
                JOptionPane.ERROR_MESSAGE
              );
            }
          } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
              null,
              "Error: Unable to add record",
              "Database Error",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      }
    );

    //* pick date
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
                  txtDate.setText(formattedDate);
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

    saveButton.addActionListener(
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Add code to save data as a text file
          String date = txtDate.getText();
          String description = txtDescription.getText();
          String partsText = txtParts.getText();
          String formattedDate = date.replace("/", "-");
          String licence_plate = txtVehicle.getText();

          List<String> partNames = new ArrayList<>();
          if ( description.isEmpty()) {
            // Display a message dialog indicating missing information
            JOptionPane.showMessageDialog(
              null,
              "Date and description are mandatory fields.",
              "Missing Information",
              JOptionPane.ERROR_MESSAGE
            );
            return; // Or you can use System.exit(0) to exit the application
          }
          if (date == null || date.isEmpty()) {
            // Get the current date
            SimpleDateFormat dateFormat = new SimpleDateFormat(
              "dd/MM/yyyy"
            );
            date = dateFormat.format(new Date());
          }
          // Create a base filename based on the date
          String baseFilename =
            "maintenance_logs/maintenance_log_" +
            licence_plate +
            "/" +
            formattedDate +
            ".txt";
          String filename = baseFilename;

          File file = new File(filename);
          File parentDirectory = file.getParentFile(); // Get the parent directory of the file

          // Ensure the parent directory exists, create it if it doesn't
          if (!parentDirectory.exists()) {
            parentDirectory.mkdirs(); // Create the parent directory and any missing intermediate directories
          }

          int counter = 1;

          // Check if the file already exists
          while (file.exists()) {
            // Append a counter to the filename
            filename = baseFilename.replace(".txt", "_" + counter + ".txt");
            file = new File(filename);
            counter++;
          }
          try {
            // Create the file
            if (file.createNewFile()) {
              // Write data to the text file
              try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(
                  "  __  __    _    ___ _   _ _____ _____ _   _    _    _   _  ____ _____ \r\n" + //
                  " |  \\/  |  / \\  |_ _| \\ | |_   _| ____| \\ | |  / \\  | \\ | |/ ___| ____|\r\n" + //
                  " | |\\/| | / _ \\  | ||  \\| | | | |  _| |  \\| | / _ \\ |  \\| | |   |  _|  \r\n" + //
                  " | |  | |/ ___ \\ | || |\\  | | | | |___| |\\  |/ ___ \\| |\\  | |___| |___ \r\n" + //
                  " |_|  |_/_/   \\_\\___|_| \\_| |_| |_____|_| \\_/_/   \\_\\_| \\_|\\____|_____|\r\n" + //
                  "                                                                       \r\n" + //
                  "  _     ___   ____                                                     \r\n" + //
                  " | |   / _ \\ / ___|                                                    \r\n" + //
                  " | |  | | | | |  _                                                     \r\n" + //
                  " | |__| |_| | |_| |                                                    \r\n" + //
                  " |_____\\___/ \\____|                                                    \r\n" + //
                  "                                                                       "
                );
                // Write data to the text file
                writer.println("Date: " + date);
                writer.println("licence plate:" + licence_plate);
                writer.println("Description: " + description);
                writer.println("Parts Used: ");
                // Split the parts text into individual parts
                String[] lines = partsText.split("\\n");

                // Process each line
                for (String line : lines) {
                  // Split the line into part code and amount
                  String[] partsInfo = line.split(",");
                  PreparedStatement preparedStatement = null;
                  ResultSet resultSet = null;
                  // Process part code and amount
                  int partCode = Integer.parseInt(partsInfo[0].trim()); // Parse part code as integer
                  int amount = Integer.parseInt(partsInfo[1].trim());
                  String query =
                    "SELECT name, availability FROM parts WHERE code = ?";
                  try {
                    // Prepare the SQL statement
                    preparedStatement = sindesi.prepareStatement(query);
                    preparedStatement.setInt(1, partCode);
                    resultSet = preparedStatement.executeQuery();

                    // Execute the query
                    if (resultSet.next()) {
                      // Retrieve part details from the ResultSet
                      String name = resultSet.getString("name");
                      int availability = resultSet.getInt("availability");
                      if (availability < amount) {
                        JOptionPane.showMessageDialog(
                          null,
                          "Insufficient availability for " +
                          name +
                          ". Requested: " +
                          amount +
                          ", Available: " +
                          availability,
                          "Insufficient Availability",
                          JOptionPane.ERROR_MESSAGE
                        );
                        return;
                      } else {
                        String updateQuery =
                          "UPDATE parts SET availability = ? WHERE code = ?";
                        try {
                          // Prepare the SQL statement for update
                          preparedStatement =
                            sindesi.prepareStatement(updateQuery);

                          // Calculate new availability
                          int newAvailability = availability - amount;

                          // Set parameters for the update statement
                          preparedStatement.setInt(1, newAvailability);
                          preparedStatement.setInt(2, partCode);

                          // Execute the update query
                          int rowsUpdated = preparedStatement.executeUpdate();

                          if (rowsUpdated > 0) {
                            System.out.println(
                              "Availability updated successfully!"
                            );
                          } else {
                            System.out.println(
                              "Failed to update availability."
                            );
                          }
                        } catch (SQLException ee) {
                          // Handle any SQL exceptions
                          ee.printStackTrace();
                        }
                        partNames.add(name);
                        writer.println(name + ":" + amount);
                      }
                    }
                  } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Handle SQL exception
                  }
                }

                JOptionPane.showMessageDialog(
                  null,
                  "Data saved as text file: " + filename
                );
              }
            } else {
              JOptionPane.showMessageDialog(
                null,
                "Failed to create file: " + filename,
                "File Error",
                JOptionPane.ERROR_MESSAGE
              );
            }
          } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
              null,
              "Error: Unable to save data as text file",
              "File Error",
              JOptionPane.ERROR_MESSAGE
            );
          }
        }
      }
    );

    // Add the table panel to the search result panel
    // Display the form in the main panel
    mainpanel.removeAll(); // Clear previous components from the main panel
    mainpanel.add(formfullpanel);
    mainpanel.revalidate(); // Refresh the layout to display the new components
    mainpanel.repaint(); // Repaint to reflect changes
  }

  static void Search_Servicee() {
    {
      JPanel mainpanel = GUI.getMainPanel();
      Connection sindesi = CONNECTION.getConnection();

      // Create and configure the components for searching vehicles
      JLabel lblSearchBy = new JLabel("Search by license number:");
      JTextField txtSearchInput = new JTextField(20);
      JButton btnSearch = new JButton("Search");
      JButton closeButton = new JButton("Close");

      // Create the panel for searching vehicles
      JPanel searchPanel = new JPanel(new MigLayout("wrap 2", "[][]", "[]10")); // Wrap after 2 components
      searchPanel.add(lblSearchBy);
      searchPanel.add(txtSearchInput, "span 2, growx"); // Spanning 2 columns and expanding horizontally
      searchPanel.add(btnSearch); // Spanning 2 columns and centered horizontally
      searchPanel.add(closeButton); // Aligned to the left, spanning 1 column and 2 rows

      DefaultTableModel ServiceTableModel = new DefaultTableModel();
      JTable serviceTable = new JTable(ServiceTableModel);
      ServiceTableModel.addColumn("License plate");
      ServiceTableModel.addColumn("Date");
      ServiceTableModel.addColumn("Parts Used");
      ServiceTableModel.addColumn("Description");
      JScrollPane serviceScrollPane = new JScrollPane(serviceTable);
      JPanel tablePanel = new JPanel(new GridLayout(1, 1)); // 2 rows, 1 column
      tablePanel.add(serviceScrollPane);
      JPanel contentPanel = new JPanel(new BorderLayout());

      // Create a panel to hold the search form
      JPanel searchPanelWrapper = new JPanel(new BorderLayout());
      searchPanelWrapper.add(searchPanel, BorderLayout.NORTH); // Add the search panel to the top

      // Create a panel to hold the two tables side by side

      // Add the table panel to the content panel
      contentPanel.add(tablePanel, BorderLayout.CENTER);
      closeButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Clear the search results and close the search functionality
            ServiceTableModel.setRowCount(0);
            // Switch back to the main panel
            mainpanel.remove(contentPanel); // Remove the form panel from the main panel
            mainpanel.remove(searchPanelWrapper);
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

              query = "SELECT * FROM maintenance_log WHERE licence_plate = ?";

              PreparedStatement preparedStatement = sindesi.prepareStatement(
                query
              );
              preparedStatement.setString(1, searchInput);

              ResultSet resultSet = preparedStatement.executeQuery();
              ServiceTableModel.setRowCount(0); // Clear previous search results
              boolean found = false;

              while (resultSet.next()) {
                String licence_plate = resultSet.getString("licence_plate");
                String date = resultSet.getString("date");
                String parts_used = resultSet.getString("parts_used");
                String description = resultSet.getString("description");

                ServiceTableModel.addRow(
                  new Object[] { licence_plate, date, parts_used, description }
                );
                found = true;
              }

              if (!found) {
                JOptionPane.showMessageDialog(
                  null,
                  "No matching records found.",
                  "Search Results",
                  JOptionPane.INFORMATION_MESSAGE
                );
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

      // Display the search panel and result table in the main panel
      mainpanel.removeAll(); // Clear previous components from the main panel
      // Create a panel to hold the search form and the two tables side by side

      // Add the search panel wrapper and the content panel to the main panel
      mainpanel.removeAll(); // Clear previous components from the main panel
      mainpanel.setLayout(new BorderLayout());
      mainpanel.add(searchPanelWrapper, BorderLayout.NORTH); // Add the search panel to the top
      mainpanel.add(contentPanel, BorderLayout.CENTER); // Add the content panel below the search panel
      mainpanel.revalidate(); // Refresh the layout to display the new components
      mainpanel.repaint(); // Repaint to reflect changes
    }
  }
}
