package me.elgamer.earthserver.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.elgamer.earthserver.Main;

public class MySQL {

	Main instance = Main.getInstance(); 

	public boolean checkDuplicate(String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1, region);
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


	public String getOwner(String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1, region);
			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getString("REGION_OWNER"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean regionExists(String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1,region);
			ResultSet results = statement.executeQuery();
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createRegion(String uuid, String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("INSERT INTO " + instance.claimData + " (REGION_ID,REGION_OWNER) VALUE (?,?)");
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean setPublic(String region) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET IS_PUBLIC=? WHERE REGION_ID=?");
			statement.setString(1,"true");
			statement.setString(2,region);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean setPrivate(String region) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET IS_PUBLIC=? WHERE REGION_ID=?");
			statement.setString(1,"false");
			statement.setString(2,region);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean addMember(String region, String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1,region);
			ResultSet results = statement.executeQuery();
			results.next();

			String names = results.getString("MEMBERS");
			names = names + "," + uuid;

			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET MEMBERS=? WHERE REGION_ID=?");
			statement.setString(1,names);
			statement.setString(2,region);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeMember(String region, String uuid) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1,region);
			ResultSet results = statement.executeQuery();
			results.next();

			String names = results.getString("MEMBERS");
			String[] nameString = names.split(",");
			names = null;

			for (int i = 0 ; i < nameString.length ; i++) {
				if (nameString[i].equals(uuid)) {}
				else {
					if (names == null) {
						names = nameString[i];
					} else {
						names = names + "," + nameString[i];
					}
				}
			}

			statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET MEMBERS=? WHERE REGION_ID=?");
			statement.setString(1,names);
			statement.setString(2,region);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String removeRegion(String region) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1,region);
			ResultSet results = statement.executeQuery();
			results.next();

			statement = instance.getConnection().prepareStatement
					("DELETE FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1, region);
			statement.executeUpdate();

			return results.getString("MEMBERS");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public boolean transferowner(String uuid, String region) {
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET REGION_OWNER=? WHERE REGION_ID=?");
			statement.setString(1,uuid);
			statement.setString(2,region);
			statement.executeUpdate();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
