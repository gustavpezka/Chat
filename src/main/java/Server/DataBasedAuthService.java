package Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBasedAuthService implements AuthService {
    private static Connection connection;
    private static Statement stmt;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:main.db");
            stmt=connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean addNewUser(String login, String password, String nickname){
        connect();
        try {
            String srt = String.format("INSERT into userlist (login,password,nickname) VALUES ('%s','%s','%s')", login, password, nickname);
            stmt.executeUpdate(srt);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            disconnect();
        }
        return true;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        connect();
        String str = String.format("SELECT nickname FROM userlist WHERE login = '%s' AND password = '%s';",login,password);
        try {
            ResultSet rs = stmt.executeQuery(str);
            while (rs.next()){
                String nickname = rs.getString("nickname");
                return nickname;
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            disconnect();
        }
        return null;
    }

    @Override
    public void changeNickname(String nickname, String login){
        connect();
        String str = String.format("UPDATE userlist SET nickname = '%s' WHERE login is '%s';", nickname,login);
        try {
            stmt.executeUpdate(str);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
