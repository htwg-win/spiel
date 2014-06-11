package models;

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
		//Pseudocode, Code zu hashen erstellen des Salt folgen noch
		String hashpassword = "";
		String salt = "";
		String sqlCreate = "INSERT INTO USER (USERNAME, PASSWORD, SALT)"
				+ "VALUES ('" + username + "','" + hashpassword + "','" + salt
				+ "');";
		try {
			stmt.executeUpdate(sqlCreate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			Db.closeConnection();
		}
		return false;
	}

	public boolean login(String username, String password) {
		Db.getConnection();
		//Pseudocode, Code zum Hashen und wiedererstellen des Passwort folgen noch
		String hashpassword = "";
		try {
			ResultSet rsLogin= stmt
					.executeQuery("SELECT USERNAME, PASSWORD, SALT FROM USER WHERE USERNAME = '" + username + "';");
			rsLogin.first();
			if(!username.equals(rsLogin.getString("username"))){
			return false;}
			
			hashpassword = rsLogin.getString("password") + rsLogin.getString("salt");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			Db.closeConnection();
		}
		if(!password.equals(hashpassword)){
			return false;}
		else{
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

	public String highscores() {
		Db.getConnection();

		String highscoretable = "";

		try {
			ResultSet rsTable = stmt
					.executeQuery("SELECT USERNAME, WINS FROM USER ORDER BY WINS DESC LIMIT 10;");

			while (rsTable.next()) {
				int wins = rsTable.getInt("wins");
				highscoretable = highscoretable + rsTable.getString("username")
						+ wins + "\n";
				;

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