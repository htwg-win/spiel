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

			conn = DriverManager
					.getConnection("jdbc:sqlite:\\Workspace\\spiel\\SQLite\\game.sqlite");

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

	public boolean create(String username, String password) {
		Db.getConnection();
		try {
		ResultSet rsLogin = stmt
				.executeQuery("SELECT USERNAME, PASSWORD, SALT FROM USER WHERE USERNAME = '"
						+ username + "';");
		String userDatabase = rsLogin.getString("username");
		if (!username.equals(userDatabase)) {
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
		String sqlCreate = "INSERT INTO USER (USERNAME, PASSWORD, SALT)"
				+ "VALUES ('" + username + "','" + hashpassword + "','" + salt
				+ "');";
		
			stmt.executeUpdate(sqlCreate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}
		return true;
	}

	public boolean login(String username, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		Db.getConnection();

		String hashpassword = "";
		String userDatabase_2 = "";

		try {
			ResultSet rsLogin = stmt
					.executeQuery("SELECT USERNAME, PASSWORD, SALT FROM USER WHERE USERNAME = '"
							+ username + "';");
			userDatabase_2 = rsLogin.getString("username");

			if (!username.equals(userDatabase_2)) {
				return false;
			}

			hashpassword = rsLogin.getString("password");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}

		if (!PasswordHash.validatePassword(password, hashpassword)) {
			return false;
		} else {
			return true;
		}

	}

	public void win(String username) {
		Db.getConnection();
		String sqlWin = "UPDATE USER SET WINS = WINS+1 WHERE USERNAME = '"
				+ username + "';";
		try {
			stmt.executeUpdate(sqlWin);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Db.closeConnection();
		}
	}

	public String[] highscores() {
		Db.getConnection();

		String[] highscoretable = new String[10];

		try {
			ResultSet rsTable = stmt
					.executeQuery("SELECT USERNAME, WINS FROM USER ORDER BY WINS DESC LIMIT 10;");
			int i = 0;
			while (rsTable.next()) {
				int wins = rsTable.getInt("wins");
				highscoretable[i] = rsTable.getString("username") + ":" + wins;

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