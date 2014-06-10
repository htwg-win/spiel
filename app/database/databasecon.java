package database;

import java.sql.*;

public class databasecon {
	public void create() {
		String name = "";
		String password = "";
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:\\SQLite\\game.db3");
			Statement stmt = conn.createStatement();
			ResultSet test = stmt.executeQuery("SELECT userid FROM user");
			
			
			while(test.next())
				System.out.println(test.getString("userid"));
			
//			stmt.execute(sql);
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}