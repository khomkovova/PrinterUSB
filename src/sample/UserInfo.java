package sample;
import java.sql.*;

public class UserInfo {
    public String name = null;
    public int availablePagePrint = 0;

    public void getUserInfo(){
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/UserPrint?" + "user=root&password=12345");
            System.out.println("Database connected!");

            Statement statement=connection.createStatement();
            ResultSet resultSet =statement.executeQuery("SELECT * FROM USER_PRINT WHERE STUDENT_ID = "+"'"+ "11111111"+"'");
            while(resultSet.next()) {
                System.out.println(resultSet.getInt(1));
                System.out.println(resultSet.getInt(2));
                System.out.println(resultSet.getInt(3));
                System.out.println(resultSet.getInt(4));

            }
            connection.close();

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
