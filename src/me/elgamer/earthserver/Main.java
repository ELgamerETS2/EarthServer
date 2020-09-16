package me.elgamer.earthserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.earthserver.commands.Claim;
import me.elgamer.earthserver.gui.ClaimGui;
import me.elgamer.earthserver.listeners.InventoryClicked;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {
	
	//MySQL
	private Connection connection;
	public String host, database, username, password, claimData;
	public int port;
	
	//Other
	public static Permission perms = null;
	public static LuckPerms lp = null;
	
	static Main instance;
	static FileConfiguration config;
	
	@Override
	public void onEnable() {
		
		//Config Setup
		Main.instance = this;
		Main.config = this.getConfig();
		
		saveDefaultConfig();
		
		//MySQL		
		mysqlSetup();
		
		//Creates the mysql table if not existing
		createTable();
		
		//Listeners
		new InventoryClicked(this);
		
		//Commands
		getCommand("claim").setExecutor(new Claim());
		
		//GUI
		ClaimGui.initialize();
		
		//Vault
		//setupPermissions();
		
		//LuckPerms
		setupLuckPerms();
	}
	
	public void onDisable() {
		
		//MySQL
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void mysqlSetup() {
		
		host = config.getString("MySQL_host");
		port = config.getInt("MySQL_port");
		database = config.getString("MySQL_database");
		username = config.getString("MySQL_username");
		password = config.getString("MySQL_password");
		claimData = config.getString("MySQL_claimData");
		
		try {
			
			synchronized (this) {
				if (getConnection() != null && !getConnection().isClosed()) {
					return;
				}
				
				Class.forName("com.mysql.jdbc.Driver");
				setConnection(DriverManager.getConnection("jdbc:mysql://" + this.host + ":" 
				+ this.port + "/" + this.database, this.username, this.password));
				
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "MYSQL CONNECTED");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	@SuppressWarnings("unused")
	private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	public static Permission getPermissions() {
        return perms;		
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	private boolean setupLuckPerms() {
		RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
		lp = provider.getProvider();
		return lp != null;
	}
	
	public static LuckPerms getLuckPerms() {
		return lp;
	}
	
	public void createTable() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + claimData
							+ " (REGION_ID TEXT NOT NULL , REGION_OWNER TEXT NOT NULL , MEMBERS TEXT NULL DEFAULT NULL , IS_PUBLIC TEXT NOT NULL DEFAULT 'false' , UNIQUE (REGION_ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}