package me.elgamer.earthserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.elgamer.earthserver.commands.Add;
import me.elgamer.earthserver.commands.AddLocation;
import me.elgamer.earthserver.commands.AddToDatabase;
import me.elgamer.earthserver.commands.Adminclaim;
import me.elgamer.earthserver.commands.Claim;
import me.elgamer.earthserver.commands.DenyLocation;
import me.elgamer.earthserver.commands.Private;
import me.elgamer.earthserver.commands.Public;
import me.elgamer.earthserver.commands.Remove;
import me.elgamer.earthserver.commands.RemoveLocation;
import me.elgamer.earthserver.commands.RequestLocation;
import me.elgamer.earthserver.commands.Requests;
import me.elgamer.earthserver.commands.TPBlock;
import me.elgamer.earthserver.commands.Teamclaim;
import me.elgamer.earthserver.commands.Unclaim;
import me.elgamer.earthserver.gui.ClaimGui;
import me.elgamer.earthserver.listeners.InventoryClicked;
import me.elgamer.earthserver.listeners.JoinEvent;
import me.elgamer.earthserver.listeners.LeaveEvent;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin {

	//MySQL
	private Connection connection;
	public String host, database, username, password, claimData, permissionData, locationData, locationRequestData;
	public int port;

	//Other
	public static Permission perms = null;
	public static LuckPerms lp = null;

	static Main instance;
	static FileConfiguration config;
	
	public static Location spawn;

	@Override
	public void onEnable() {

		//Config Setup
		Main.instance = this;
		Main.config = this.getConfig();

		saveDefaultConfig();

		//MySQL		
		mysqlSetup();
		
		//Spawn
		spawn = new Location(Bukkit.getWorld(config.getString("World_Name")),config.getDouble("Spawn.x"), config.getDouble("Spawn.y"), config.getDouble("Spawn.z"));

		//Creates the mysql table if not existing
		createClaimTable();
		createUserTable();

		//Listeners
		new InventoryClicked(this);
		new JoinEvent(this);
		new LeaveEvent(this);

		//Commands
		getCommand("claim").setExecutor(new Claim());
		getCommand("unclaim").setExecutor(new Unclaim());
		getCommand("teamclaim").setExecutor(new Teamclaim());
		getCommand("addmember").setExecutor(new Add());
		getCommand("removemember").setExecutor(new Remove());
		getCommand("public").setExecutor(new Public());
		getCommand("private").setExecutor(new Private());
		getCommand("adminclaim").setExecutor(new Adminclaim());
		getCommand("addtodatabase").setExecutor(new AddToDatabase());
		getCommand("tpblock").setExecutor(new TPBlock());
		
		getCommand("locationrequest").setExecutor(new RequestLocation());
		getCommand("addlocation").setExecutor(new AddLocation());
		getCommand("removelocation").setExecutor(new RemoveLocation());
		getCommand("denyrequest").setExecutor(new DenyLocation());
		getCommand("requests").setExecutor(new Requests());

		//GUI
		ClaimGui.initialize();

		//Vault
		setupPermissions();

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
		permissionData = config.getString("MySQL_permissionData");
		locationData = config.getString("MySQL_locationData");
		locationRequestData = config.getString("MySQL_locationRequestData");

		
		try {

			synchronized (this) {
				if (connection != null && !connection.isClosed()) {
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

		try {
			if (connection == null || connection.isClosed()) {
				mysqlSetup();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

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

	public void createClaimTable() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + claimData
							+ " ( REGION_ID VARCHAR(36) NOT NULL , REGION_OWNER TEXT NOT NULL , MEMBERS TEXT NULL DEFAULT NULL , IS_PUBLIC TEXT NOT NULL , LAST_ONLINE TEXT NOT NULL , UNIQUE (REGION_ID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createUserTable() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + permissionData
							+ " ( UUID VARCHAR(36) NOT NULL , ADD_PERM TEXT NULL DEFAULT NULL , REMOVE_PERM TEXT NULL DEFAULT NULL , UNIQUE (UUID))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createLocationTable() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + locationData
							+ " ( LOCATION TEXT NOT NULL , CATEGORY TEXT NOT NULL , SUBCATEGORY TEXT NOT NULL , X DOUBLE NOT NULL , Y DOUBLE NOT NULL , Z DOUBLE NOT NULL , PITCH FLOAT NOT NULL , YAW FLOAT NOT NULL , PITCH  NOT NULL , UNIQUE (LOCATION))");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void createLocationRequestTable() {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("CREATE TABLE IF NOT EXISTS " + locationRequestData
							+ " ( LOCATION TEXT NOT NULL , X DOUBLE NOT NULL , Y DOUBLE NOT NULL , Z DOUBLE NOT NULL , PITCH FLOAT NOT NULL , YAW FLOAT NOT NULL , PITCH  NOT NULL");
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}