import java.sql.Connection;
import java.sql.DriverManager;

public class CONNECTION {

    private static Connection con; // Static variable to hold the connection

    public static Connection connectDB(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/MECHANIC",
                    username,
                    password
            );
            System.out.println("Connection Successful...");
            return con;
        } catch (Exception e) {
            System.out.println("NO CONNECTION " + e);
            return null;
        }
    }

    public static Connection getConnection() {
        return con;
    }
}

