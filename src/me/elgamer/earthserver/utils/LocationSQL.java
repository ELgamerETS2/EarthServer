package me.elgamer.earthserver.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.elgamer.earthserver.Main;

public class LocationSQL {


	//Returns the number of locations for a given subcategory
	public static int subCount(String sub) {

		return 0;

	}

	//Returns whether the location is already requested
	public static boolean requestExists(String loc) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.locationRequestData + " WHERE LOCATION=?");
			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Returns true if there is a new location request
	public static boolean requestExists() {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.locationRequestData);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Creates a location request
	public static void addRequest(String loc, Location l) {

		Main instance = Main.getInstance();


		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.locationRequestData + " (LOCATION,X,Y,Z,PITCH,YAW) VALUE (?,?,?,?,?,?)");
			statement.setString(1, loc);
			statement.setDouble(2, l.getX());
			statement.setDouble(3, l.getY());
			statement.setDouble(4, l.getZ());
			statement.setFloat(5, l.getPitch());
			statement.setFloat(6, l.getYaw());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}


	}

	//Creates a location from request
	public static boolean addLocation(String loc, String cat, String subcat, Location l) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.locationRequestData + " (LOCATION,CATEGORY,SUBCATEGORY,X,Y,Z,PITCH,YAW) VALUE (?,?,?,?,?,?,?,?)");
			statement.setString(1, loc);
			statement.setString(2, cat);
			statement.setString(3, subcat);
			statement.setDouble(4, l.getX());
			statement.setDouble(5, l.getY());
			statement.setDouble(6, l.getZ());
			statement.setFloat(7, l.getPitch());
			statement.setFloat(8, l.getYaw());

			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;


	}

	//Returns whether the location is already requested
	public static boolean locationExists(String loc) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.locationData + " WHERE LOCATION=?");
			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Return the location of a request and deletes it subsequently
	public static Location getRequestLocation(String loc) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.locationRequestData + " WHERE LOCATION=?");
			statement.setString(1, loc);
			ResultSet results = statement.executeQuery();
			results.next();

			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.locationRequestData + " WHERE LOCATION=?");
			statement.executeUpdate();

			return (new Location(Bukkit.getWorld("world"), results.getDouble("X"), results.getDouble("Y"), results.getDouble("Z"), results.getFloat("PITCH"), results.getFloat("YAW")));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	//Remove location request
	public static boolean removeRequest(String loc) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.locationRequestData + " WHERE LOCATION=?");
			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	//Remove location
	public static boolean removeLocation(String loc) {

		Main instance = Main.getInstance();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.locationData + " WHERE LOCATION=?");
			statement.executeUpdate();

			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}
	
	public static HashMap<String, Location> getRequests(){
		
		Main instance = Main.getInstance();
		HashMap<String, Location> requests = new HashMap<String, Location>();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.locationRequestData);
			ResultSet results = statement.executeQuery();
			
			while (results.next()) {
				
				requests.put(results.getString("LOCATION"), new Location(Bukkit.getWorld("world"), results.getDouble("X"), results.getDouble("Y"), results.getDouble("Z"), results.getFloat("PITCH"), results.getFloat("YAW")));
					
			}

			return requests;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
