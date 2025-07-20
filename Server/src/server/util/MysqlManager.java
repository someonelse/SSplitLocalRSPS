package server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import server.model.players.Client;

public class MysqlManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/game?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "tsm123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Updated for MySQL 8+
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws Exception {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static boolean saveHighScore(Client c) {
        String deleteSkills = "DELETE FROM skills WHERE playerName = ?";
        String insertSkills = "INSERT INTO skills (playerName, Attacklvl, Attackxp, ...) VALUES (?, ?, ?, ...)";
        // ... Build the full statement
        try (Connection conn = getConnection();
             PreparedStatement psDelete = conn.prepareStatement(deleteSkills);
             PreparedStatement psInsert = conn.prepareStatement(insertSkills)) {
            psDelete.setString(1, c.playerName);
            psDelete.executeUpdate();

            psInsert.setString(1, c.playerName);
            psInsert.setInt(2, c.playerLevel[0]);
            // ... Set other parameters

            psInsert.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Always log!
            return false;
        }
    }

    public static int loadVotingPoints(Client c) {
        String sql = "SELECT voting_points FROM user WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.playerName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("voting_points");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
