package library;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Connector {
    
    //Method yang berfungsi untuk membuat koneksi ke database MySQL
    public static Connection koneksiDb(){
        try {
            String url = "jdbc:mysql://localhost:3308/librarymanagement";
            String user = "root";
            String pass = "";
            //Membuat objek "connection" untuk mendapat koneksi ke MySql
            Connection connection = DriverManager.getConnection(url, user, pass);
            return connection;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

            return null;
        }
    }
    
}
