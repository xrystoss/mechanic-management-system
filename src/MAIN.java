import java.sql.Connection;

public class MAIN {

  public static Connection sindesi;

  public static void main(String[] args) {
    System.out.println("Hello world!");
    sindesi = CONNECTION.connectDB("root", "1234");
    if (sindesi != null) {
      // currentTime = LocalTime.now();
      new GUI(sindesi); // Open the GUI
    } else {
      System.out.println("provlima database!");
    }
  }
}
