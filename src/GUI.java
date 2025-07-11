import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

public class GUI implements ActionListener {

  private static Connection sindesi; // Connection instance

  //Initialize Buttons / Labels ect.
  protected JButton b1 = new JButton("Add New Customer");
  protected JButton b2 = new JButton("Add New Vehicle");
  protected JButton b3 = new JButton("Add New Item");
  protected JButton b4 = new JButton("Pending Payments");
  protected JButton b5 = new JButton("Search Customer");
  protected JButton b6 = new JButton("Search Vehicle");
  protected JButton b7 = new JButton("View / Search Items");
  protected JButton b8 = new JButton("Add New Service");
  protected JButton b9 = new JButton("Search Service");

  protected JLabel statusLabel = new JLabel("Not connected"); // Initialize with default text

  public static JFrame frame = new JFrame();
  public static JPanel mainpanel = new JPanel();
  JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

  public static JPanel getMainPanel() {
    return mainpanel;
  }

  public static JFrame getFrame() {
    return frame;
  }

  public GUI(Connection connection) {
    GUI.sindesi = connection; // Store the connection instance
    if (sindesi != null) {
      statusLabel.setText("Connected to database");
    }
    frame.setLayout(new BorderLayout());

    JPanel Toppanel = new JPanel(); // The Gray panel to the top with the buttons
    Toppanel.setLayout(
      new MigLayout("wrap 5, fill", "20[]20[]20[]20[]20", "[]10[]10")
    );
    Toppanel.setBackground(Color.DARK_GRAY);

    mainpanel.setLayout(new BorderLayout()); //mainpanel is the panel under the TopPanel and its for the main information
    mainpanel.setBackground(Color.LIGHT_GRAY);
    connectionPanel.add(statusLabel);

    //Set Colors to all buttons
    b1.setBackground(Color.LIGHT_GRAY);
    b2.setBackground(Color.LIGHT_GRAY);
    b3.setBackground(Color.LIGHT_GRAY);
    b4.setBackground(Color.LIGHT_GRAY);
    b5.setBackground(Color.LIGHT_GRAY);
    b6.setBackground(Color.LIGHT_GRAY);
    b7.setBackground(Color.LIGHT_GRAY);
    b8.setBackground(Color.LIGHT_GRAY);
    b9.setBackground(Color.LIGHT_GRAY);

    //Make buttons clickable
    b1.addActionListener(this);
    b2.addActionListener(this);
    b3.addActionListener(this);
    b4.addActionListener(this);
    b5.addActionListener(this);
    b6.addActionListener(this);
    b7.addActionListener(this);
    b8.addActionListener(this);
    b9.addActionListener(this);

    //Add buttons to the Toppanel
    Toppanel.add(b1, "growx");
    Toppanel.add(b2, "growx");
    Toppanel.add(b3, "growx");
    Toppanel.add(b4, "growx");
    Toppanel.add(b9, "growx");
    Toppanel.add(b5, "growx");
    Toppanel.add(b6, "growx");
    Toppanel.add(b7, "growx");
    Toppanel.add(b8, "growx");

    //Adding Toppanel to the frame (NORTH) and mainpanel to CENTER
    frame.add(Toppanel, BorderLayout.NORTH);
    frame.add(mainpanel, BorderLayout.CENTER);
    frame.add(connectionPanel, BorderLayout.SOUTH);

    frame.setTitle("Mechanic");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 700);
    frame.setVisible(true);

    //ImageIcon image = new ImageIcon("logo.png");
    //frame.setIconImage(image.getImage());
    frame.getContentPane().setBackground(Color.GRAY);

    frame.addWindowListener(
      new WindowAdapter() { //When the frame is closed by the user, close the connection too!
        @Override
        public void windowClosing(WindowEvent e) {
          System.out.println("GUI is closing!");
          // Database.closeConnection();
          System.out.println("Connection Closed");
          super.windowClosing(e);
        }
      }
    );
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    //* ADD CUSTOMER
    if (e.getSource() == b1) {
      Customer.addCustomer();
    }

    //* add vehicle
    if (e.getSource() == b2) {
      Vehicle.registerVehicle();
    }

    //* add item
    if (e.getSource() == b3) {
      Part.addPart();
    }

    //* Search Customer
    if (e.getSource() == b5) {
      Customer.searchcustomer();
    }

    //*search vechicle
    if (e.getSource() == b6) {
      Vehicle.searchVehicle();
    }

    //*search part
    if (e.getSource() == b7) {
      Part.searchPart();
    }

    //* */ For pending payments
    if (e.getSource() == b4) {
      PendingPayment.Payment();
    }
    //* eksipiretisi */
    if (e.getSource() == b8) {
      Service.Servicee();
    }

    if (e.getSource() == b9) {
      Service.Search_Servicee();
    }
  }
}
//todo send strings uppercase
