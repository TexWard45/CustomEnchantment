package com.bafmc.customenchantment.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.bukkit.entity.Player;

public class Database {
	private File file;
	private Connection connection;

	public Database(File file) {
		this.file = file;
	}

	public void connect() {
		if (!isConnected()) {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void disconnect() {
		if (isConnected()) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isConnected() {
		return connection != null;
	}

	public void init() {
		try {
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE IF NOT EXISTS item_action_logs (" 
					+ "id INTEGER PRIMARY KEY,"
					+ "date DATETIME," 
					+ "player varchar(16),"
					+ "item1_type VARCHAR(64)," 
					+ "item2_type VARCHAR(64),"
					+ "result VARCHAR(64)," + "data TEXT)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void insertLogs(Player player, String itemType1, String itemType2, String result, Map<String, Object> map) {
		try {
			String playerName = player.getName();

			PreparedStatement prepareStatement = connection.prepareStatement(
					"INSERT INTO item_action_logs(date, player,item1_type,item2_type,result,data) VALUES(?,?,?,?,?,?)");
			prepareStatement.setString(1, format.format(new Date()));
			prepareStatement.setString(2, playerName);
			prepareStatement.setString(3, itemType1);
			prepareStatement.setString(4, itemType2);
			prepareStatement.setString(5, result);

			String data = "";
			int i = 0;
			for (String key : map.keySet()) {
				if (i == map.size() - 1) {
					data += key + "=" + map.get(key);
					
				} else {
					data += key + "=" + map.get(key) + ",";
				}
				i++;
			}
			
			prepareStatement.setString(6, data);
			prepareStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
