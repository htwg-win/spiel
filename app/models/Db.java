package models;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class Db {

	static Connection conn = null;
	static Statement stmt = null;

	public static Connection getConnection() {

		try {
			Class.forName("org.sqlite.JDBC");

			conn = DriverManager.getConnection("jdbc:sqlite:SQLite\\game.sqlite");

			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
	}

	public static void closeConnection() {

		try {
			stmt.close();
		} catch (Exception e) { /* ignored */
		}
		try {
			conn.close();
		} catch (Exception e) { /* ignored */
		}
	}

	public static boolean create(String username, String password) {
		Db.getConnection();
		try {

			PreparedStatement prepStmt = conn.prepareStatement("SELECT USERNAME, PASSWORD, SALT FROM USER WHERE USERNAME = ?;");
			prepStmt.setString(1, username);
			ResultSet rsLogin = prepStmt.executeQuery();

			if (rsLogin.next()) {
				return false;
			}

			String hashpassword = "";
			try {
				hashpassword = PasswordHash.createHash(password);
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidKeySpecException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String salt = PasswordHash.getSaltString();

			PreparedStatement update = conn.prepareStatement("INSERT INTO USER (USERNAME, PASSWORD, SALT)" + "VALUES (?,?,?);");
			update.setString(1, username);
			update.setString(2, hashpassword);
			update.setString(3, salt);
			update.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}
		return true;
	}

	public static boolean login(String username, String password) {
		Db.getConnection();

		String hashpassword = "";

		try {

			PreparedStatement prepStmt = conn.prepareStatement("SELECT USERNAME, PASSWORD, SALT FROM USER WHERE USERNAME = ?;");
			prepStmt.setString(1, username);
			ResultSet rsLogin = prepStmt.executeQuery();

			if (!rsLogin.next()) {
				return false;
			}

			hashpassword = rsLogin.getString("password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}

		try {
			if (!PasswordHash.validatePassword(password, hashpassword)) {
				return false;
			} else {
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	public static void win(String username) {
		Db.getConnection();

		try {
			PreparedStatement update = conn.prepareStatement("UPDATE USER SET WINS = WINS+1 WHERE USERNAME = ?;");
			update.setString(1, username);

			update.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}
	}

	public static String[] highscores() {
		Db.getConnection();

		String[] highscoretable = new String[10];

		try {

			ResultSet rsTable = stmt.executeQuery("SELECT USERNAME, WINS FROM USER ORDER BY WINS DESC LIMIT 10;");
			int i = 0;
			while (rsTable.next()) {
				int wins = rsTable.getInt("wins");
				highscoretable[i++] = rsTable.getString("username") + ":" + wins;

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}
		return highscoretable;
	}

}