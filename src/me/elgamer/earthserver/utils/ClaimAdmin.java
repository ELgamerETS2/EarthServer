package me.elgamer.earthserver.utils;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.earthserver.Main;
import net.md_5.bungee.api.ChatColor;

public class ClaimAdmin {

	public boolean unclaim(Player p, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					regions.removeRegion(points[i]);

					mysql.removePermission(mysql.getOwner(points[i]), points[i]);

					String members = mysql.removeRegion(points[i]);

					p.sendMessage(ChatColor.RED + "Region " + points[i] + " removed!");

					if (members != null) {
						String[] regionMembers = members.split(",");

						for (int j = 0 ; j < regionMembers.length ; j++) {

							mysql.removePermission(regionMembers[j], points[i]);

						}
					}

					try {
						regions.save();
					} catch (StorageException e1) {
						e1.printStackTrace();
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}

			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}

	}

	public boolean setPublic(Player p, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(points[i]);
					DefaultDomain members = claim.getMembers();

					Set<String> set = members.getGroups();

					if (!(set.contains("builder"))) {
						members.addGroup("builder");

						claim.setMembers(members);
						mysql.setPublic(points[i]);

						mysql.addPermission("builder", points[i]);

						p.sendMessage(ChatColor.GREEN + "Region " + points[i] + " is now open for all builders!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + "The region " + points[i] + " is already public!");
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}

			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}
	}

	public boolean setPrivate(Player p, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(points[i]);
					DefaultDomain members = claim.getMembers();

					Set<String> set = members.getGroups();

					if (set.contains("builder")) {
						members.removeGroup("builder");

						claim.setMembers(members);
						mysql.setPrivate(points[i]);

						mysql.removePermission("builder", points[i]);

						p.sendMessage(ChatColor.RED + "Region " + points[i] + " is now private, only the region owner and members have access!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + "The region " + points[i] + " is already private!");
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}

			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean transferowner(Player p, String name, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		String uuid;

		if (Bukkit.getPlayer(name) != null) {
			uuid = Bukkit.getPlayer(name).getUniqueId().toString();
		} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
			uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
		} else {
			p.sendMessage("The user " + name + " has never connected to this server!");
			return true;
		}

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(points[i]);
					DefaultDomain owner = claim.getOwners();

					String pOld = mysql.getOwner(points[i]);

					if (uuid.equals(pOld)) {
						p.sendMessage(ChatColor.RED + name + " is already the owner of region " + points[i]);
					} else {

						owner.removeAll();
						owner.addPlayer(UUID.fromString(uuid));

						claim.setOwners(owner);
						mysql.transferowner(uuid, points[i]);

						mysql.removePermission(pOld, points[i]);
						mysql.addPermission(uuid, points[i]);

						p.sendMessage(ChatColor.GREEN + "Transferred region owner of " + points[i] + " from " + Bukkit.getOfflinePlayer(UUID.fromString(pOld)).getName() + " to " + name + "!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}

			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean addMember(Player p, String name, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		String uuid;
		
		if (Bukkit.getPlayer(name) != null) {
			uuid = Bukkit.getPlayer(name).getUniqueId().toString();
		} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
			uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
		} else {
			p.sendMessage("The user " + name + " has never connected to this server!");
			return true;
		}

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(points[i]);
					DefaultDomain members = claim.getMembers();

					Set<UUID> set = members.getUniqueIds();

					if (!(set.contains(UUID.fromString(uuid)))) {

						members.addPlayer(UUID.fromString(uuid));

						claim.setMembers(members);
						mysql.addMember(points[i], uuid);

						mysql.addPermission(uuid, points[i]);

						p.sendMessage(ChatColor.GREEN + name + "added to region " + points[i] + "!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + name + " is already a member of the region " + points[i] + "!");
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}
			
			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	public boolean removeMember(Player p, String name, String radius) {

		MySQL mysql = new MySQL();

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		String uuid;

		if (Bukkit.getPlayer(name) != null) {
			uuid = Bukkit.getPlayer(name).getUniqueId().toString();
		} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
			uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
		} else {
			p.sendMessage("The user " + name + " has never connected to this server!");
			return true;
		}

		try {
			int r = Integer.parseInt(radius);			

			if (r > 2 || r < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, r);

			WorldGuardPlugin wg = getWorldGuard();

			for (int i = 0; i < points.length; i++) {

				if (mysql.regionExists(points[i])) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(points[i]);
					DefaultDomain members = claim.getMembers();

					Set<UUID> set = members.getUniqueIds();

					if (set.contains(UUID.fromString(uuid))) {

						members.removePlayer(UUID.fromString(uuid));

						claim.setMembers(members);
						mysql.removeMember(points[i], uuid);

						mysql.removePermission(uuid, points[i]);

						p.sendMessage(ChatColor.GREEN + name + "remove from region " + points[i] + "!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + name + " is not a member of the region " + points[i] + "!");
					}

				} else {
					p.sendMessage(ChatColor.RED + "The region " + points[i] + " does not exists!");
				}
			}
			
			return true;

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}
	}

	public boolean help(Player p) {
		
		p.sendMessage(Utils.chat("&a/adminclaim unclaim [radius] &7unclaims all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/adminclaim transferowner <user> [radius] &7transfers ownership in all 512x512 regions in a square radius of regions to the specified user!"));
		p.sendMessage(Utils.chat("&a/adminclaim add <user> [radius] &7adds the specified user to all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/adminclaim remove <user> [radius] &7removes the specified user to all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/adminclaim public [radius] &7makes all 512x512 regions public to builders in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/admincalim private [radius] &7makes all 512x512 regions private in a square radius of regions!"));
		return true;
	}

	public static String getRegion(double x, double z) {
		int rX = (int) Math.floor((x/512));
		int rZ = (int) Math.floor((z/512));
		return (rX + "," + rZ);
	}

	public static String[] getRegions(Player p, int radius) {

		String[] points = new String[(int) Math.pow(radius*2+1,2)];
		Location l = p.getLocation();
		double x = l.getX();
		double z = l.getZ();

		x -= 512*radius;
		z -= 512*radius;

		for (int i = 1; i <= radius*2+1; i++) {

			for (int j = 1; j <= radius*2+1; j++) {

				points[((i-1)*(radius*2+1)+j)-1] = getRegion(x, z);
				if (j!=radius*2+1) {x += 512;}
				else {x = l.getX() - 512*radius;}
			}

			z += 512;

		}

		return points;

	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

}
