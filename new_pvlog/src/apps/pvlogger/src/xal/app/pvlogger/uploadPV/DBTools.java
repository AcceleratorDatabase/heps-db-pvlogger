package xal.app.pvlogger.uploadPV;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.Connection;

/**
 * 
 * @author lv
 * @author chu
 */
public class DBTools {

	public static Connection conn;

	/*
	 * db_type:mysql, oracle
	 * url:jdbc:mysql://localhost:3306/dbname
	 * */
	public static Connection getConnection(String db_type, String url,
			String user, String password) throws SQLException {
		if (conn == null) {

			try {
				if (db_type.toLowerCase().equals("mysql")) {
					Class.forName("com.mysql.jdbc.Driver");
				} else if (db_type.toLowerCase().equals("oracle")) {
					Class.forName("oracle.jdbc.driver.OracleDriver");
				}
				conn = (Connection) DriverManager.getConnection(url, user,
						password);
				boolean rbsign = conn.getRewriteBatchedStatements();
				if (!rbsign) {
					url = url + "?rewriteBatchedStatements=true";
					closeConnection();
					conn = (Connection) DriverManager.getConnection(url, user,
							password);
				}
			} catch (ClassNotFoundException ce) {
				ce.printStackTrace();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		} else {
			String userName = conn.getMetaData().getUserName();
			String u = userName.substring(0, userName.indexOf("@"));
			if (!user.equals(u)) {
				closeConnection();
				DriverManager.getConnection(url, user, password);
				boolean rbsign = conn.getRewriteBatchedStatements();
				if (!rbsign) {
					url = url + "?rewriteBatchedStatements=true";
					closeConnection();
					conn = (Connection) DriverManager.getConnection(url, user,
							password);
				}
			}
		}
		return conn;
	}

	
	
	public static void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void closeStatement(Statement state) {
		if (state != null) {
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closePreparedStatement(PreparedStatement state) {
		if (state != null) {
			try {
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
