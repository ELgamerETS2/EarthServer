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
					("INSERT INTO " + instance.claimData + " (REGION_ID,REGION_OWNER,IS_PUBLIC,LAST_ONLINE) VALUE (?,?,?,?)");
			statement.setString(1, region);
			statement.setString(2, uuid);
			statement.setString(3, "false");
			statement.setString(4, Time.getTime());
			
			statement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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

			if (names == null) {
				names = uuid;
			} else {
				names = names + "," + uuid;
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

			if (results.getBoolean("IS_PUBLIC")) {
				if (results.getString("MEMBERS") == null) {
					return ("builder");
				} else {
					return (results.getString("MEMBERS") + ",builder");
				}
			} else {
				return (results.getString("MEMBERS"));
			}

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

	public boolean addPermission(String uuid, String region) {
		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.permissionData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				String regions = results.getString("ADD_PERM");
				if (region != null) {
					regions = regions + ";" + region;
				} else {
					regions = region;
				}
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.permissionData + " SET ADD_PERM=? WHERE UUID=?");
				statement.setString(2,uuid);
				statement.setString(1,regions);
				statement.executeUpdate();
			} else {
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.permissionData + " (UUID,ADD_PERM,REMOVE_PERM) VALUE (?,?,?)");
				insert.setString(1, uuid);
				insert.setString(2, region);
				insert.setString(3, null);
				insert.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removePermission(String uuid, String region) {
		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.permissionData + " WHERE UUID=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				String regions = results.getString("REMOVE_PERM");
				if (region != null) {
					regions = regions + ";" + region;
				} else {
					regions = region;
				}
				statement = instance.getConnection().prepareStatement
						("UPDATE " + instance.permissionData + " SET REMOVE_PERM=? WHERE UUID=?");
				statement.setString(2,uuid);
				statement.setString(1,regions);
				statement.executeUpdate();
			} else {
				PreparedStatement insert = instance.getConnection().prepareStatement
						("INSERT INTO " + instance.permissionData + " (UUID,ADD_PERM,REMOVE_PERM) VALUE (?,?,?)");
				insert.setString(1, uuid);
				insert.setString(2, null);
				insert.setString(3, region);
				insert.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}


	public String[] getPermission() {
		try {

			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.permissionData);
			ResultSet results = statement.executeQuery();

			if (results.next()) {

				String uuid;
				String add;
				String remove;

				uuid = results.getString("UUID");
				add = results.getString("ADD_PERM");
				remove = results.getString("REMOVE_PERM");

				statement = instance.getConnection().prepareStatement
						("DELETE FROM " + instance.permissionData + " WHERE UUID=?");
				statement.setString(1, uuid);
				statement.executeUpdate();

				return (new String[]{uuid, add, remove});
			} else { 
				return null;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getMembers(String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1, region);
			ResultSet results = statement.executeQuery();
			results.next();

			if (results.getString("MEMBERS") == null) {
				return null;
			} else {
				return (results.getString("MEMBERS").split(","));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isPublic(String region) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_ID=?");
			statement.setString(1, region);
			ResultSet results = statement.executeQuery();
			results.next();

			return (results.getBoolean("IS_PUBLIC"));

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean hasRegion(String uuid) {

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();

			return (results.next());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public void updateTime(String region) {

		String time = Time.getTime();

		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("UPDATE " + instance.claimData + " SET LAST_ONLINE=? WHERE REGION_ID=?");
			statement.setString(2, region);
			statement.setString(1, time);
			statement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getRegions(String uuid) {
		
		try {
			PreparedStatement statement = instance.getConnection().prepareStatement
					("SELECT * FROM " + instance.claimData + " WHERE REGION_OWNER=?");
			statement.setString(1, uuid);
			ResultSet results = statement.executeQuery();
			
			String regions = null;
			
			while(results.next()) {
				
				if (regions == null) {
					regions = results.getString("REGION_ID");
				} else {
					regions = regions + ";" + results.getString("REGION_ID");
				}
			}
			return (regions);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
