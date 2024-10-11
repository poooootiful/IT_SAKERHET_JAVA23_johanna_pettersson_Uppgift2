import org.mindrot.jbcrypt.BCrypt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

public class SqlFunctions {
    public static void Register (String email, String password) {

        Connection connection = Database.getConnection();

        String sql="INSERT INTO Users (email, password) VALUES ('"+email+"','"+password+"');";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int Login (String email, String password) {
        Boolean check = CheckPass(email,password);
        System.out.println(check);

        if (check) {
            Connection connection = Database.getConnection();
            String sql ="SELECT id FROM Users WHERE email = '"+email+"';";

            try {

                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql);
                resultSet.next();
                return resultSet.getInt("id");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    private static Boolean CheckPass (String email, String RawPass) {
        Connection connection = Database.getConnection();

        String sql = "SELECT password FROM Users WHERE email = '"+email+"';";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            resultSet.next();

            String hashedPassword = resultSet.getString("password");
            System.out.println(hashedPassword);
            System.out.println(RawPass);
            return  BCrypt.checkpw(RawPass, hashedPassword);

        } catch (SQLException e) {
            Main.StartMenu();
            throw new RuntimeException(e);
        }
    }

    public static int Id;

    public static void setId (int id) {
        Id = id;
    }

    public static int getId () {
        return Id;
    }

    public static void SaveNewCapsule(int userid, String encodedKey, String message) {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO timecapsules (UserId, Message,Encryptkey) VALUES ("+userid+",'"+message+"','"+encodedKey+"');";

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ShowAllCapsules(int userid) {
        Connection connection = Database.getConnection();
        String sql = "SELECT Encryptkey, message FROM timecapsules where UserId = '"+userid+"'";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String encodedkey = resultSet.getString("Encryptkey");
                String encryptedmessage = resultSet.getString("message");

                byte [] decodedkey = Base64.getDecoder().decode(encodedkey);
                SecretKey secretKey = new SecretKeySpec(decodedkey,0,decodedkey.length,"AES");
                try {
                    String decryptedmessage = Security.decrypt(encryptedmessage,secretKey);
                    System.out.println("Message: " + decryptedmessage + "\n");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
