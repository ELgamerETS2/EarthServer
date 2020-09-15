package me.elgamer.earthserver.utils;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.AnvilGui;
import me.elgamer.earthserver.gui.AnvilGui.AnvilClickEvent;
import net.md_5.bungee.api.ChatColor;

public class ClaimRegion {

	Pattern wordPattern = Pattern.compile("\\w+");
	Matcher wordMatcher;

	public boolean createRegion(Player p, String region) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		WorldGuardPlugin wg = getWorldGuard();
		MySQL mysql = new MySQL();

		//Check if the region already exists
		if (mysql.checkDuplicate(region)) {
			if (Bukkit.getPlayer(mysql.getOwner(region)) != null) {
				String regionOwner = Bukkit.getPlayer(UUID.fromString(mysql.getOwner(region))).getName();
				p.sendMessage(ChatColor.RED + ("The region " + region + " is already claimed by " + regionOwner + "!"));
				p.sendMessage(ChatColor.RED + ("If you believe this is wrongly claimed please contact staff!"));
			} else {
				String regionOwner = Bukkit.getOfflinePlayer(UUID.fromString(mysql.getOwner(region))).getName();
				p.sendMessage(ChatColor.RED + ("The region " + region + " is already claimed by " + regionOwner + "!"));
				p.sendMessage(ChatColor.RED + ("If you believe this is wrongly claimed please contact staff!"));
			}
			return true;
		}

		//Use the region coordinates to get the min and max location
		String[] coords = region.split(",");

		int Xmin = Integer.parseInt(coords[0]) * 512;
		int Zmin = Integer.parseInt(coords[1]) * 512;

		int Xmax = Integer.parseInt(coords[0]) * 512 + 511;
		int Zmax = Integer.parseInt(coords[1]) * 512 + 511;

		//Create the region
		World world = Bukkit.getWorld(config.getString("World_Name"));
		
		RegionContainer container = wg.getRegionContainer();
		RegionManager regions = container.get(world);

		ProtectedRegion claim = new ProtectedCuboidRegion(region,
				new BlockVector(Xmin, -512, Zmin), new BlockVector(Xmax, 1536, Zmax));

		DefaultDomain owners = new DefaultDomain();
		owners.addPlayer(wg.wrapPlayer(p));

		claim.setOwners(owners);

		regions.addRegion(claim);
		
		mysql.createRegion(p.getUniqueId().toString(), region);
		
		Permissions perms = new Permissions();
		
		perms.addPermission(p.getUniqueId(), region);

		//Save the new region
		try {
			regions.save();
		} catch (StorageException e1) {
			e1.printStackTrace();
		}

		return true;
	}

	public boolean setPublic(Player p, String region) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString() == mysql.getOwner(region)) {
				World world = Bukkit.getWorld(config.getString("World_Name"));
				
				RegionContainer container = wg.getRegionContainer();
				RegionManager regions = container.get(world);

				ProtectedRegion claim = regions.getRegion(region);
				DefaultDomain members = claim.getMembers();

				Set<String> set = members.getGroups();

				if (!(set.contains("builder"))) {
					members.addGroup("builder");

					claim.setMembers(members);
					mysql.setPublic(region);

					try {
						regions.save();
					} catch (StorageException e1) {
						e1.printStackTrace();
					}
				} else {
					p.sendMessage(ChatColor.RED + "This region is already public!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own this region!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "This region does not exists!");
			return false;
		}

		return true;
	}

	public boolean setPrivate(Player p, String region) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				World world = Bukkit.getWorld(config.getString("World_Name"));
				
				RegionContainer container = wg.getRegionContainer();
				RegionManager regions = container.get(world);

				ProtectedRegion claim = regions.getRegion(region);
				DefaultDomain members = claim.getMembers();

				Set<String> set = members.getGroups();

				if (set.contains("builder")) {
					members.removeGroup("builder");

					claim.setMembers(members);
					mysql.setPrivate(region);

					try {
						regions.save();
					} catch (StorageException e1) {
						e1.printStackTrace();
					}
				} else {
					p.sendMessage(ChatColor.RED + "This region is already private!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own this region!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "This region does not exists!");
			return false;
		}

		return true;
	}

	public boolean addMember(Player p, String region) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				//Close PlotGui to prompt user with AnvilGui to input name
				p.closeInventory();

				AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){

					@Override
					public void onAnvilClick(AnvilClickEvent e) {
						if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {

							e.setWillClose(true);
							e.setWillDestroy(true);
							String name = e.getName();
							wordMatcher = wordPattern.matcher(name);

							//Check if name contains 1 word and only alphabetical characters
							if (wordMatcher.matches()) {

								Player user = Bukkit.getPlayer(name);

								if (user == null) {
									p.sendMessage(ChatColor.RED + "This player is not online!");

								} else {

									World world = Bukkit.getWorld(config.getString("World_Name"));
									
									RegionContainer container = wg.getRegionContainer();
									RegionManager regions = container.get(world);

									ProtectedRegion claim = regions.getRegion(region);
									DefaultDomain members = claim.getMembers();

									Set<String> set = members.getPlayers();

									if (!(set.contains(name))) {

										members.addPlayer(name);

										claim.setMembers(members);
										mysql.addMember(region, user.getUniqueId().toString());
										
										Permissions perms = new Permissions();
										
										perms.addPermission(p.getUniqueId(), region);

										try {
											regions.save();
										} catch (StorageException e1) {
											e1.printStackTrace();
										}
									} else {
										p.sendMessage(ChatColor.RED + "This user is already a member of this region!");
									}
								}

							} else {
								p.sendMessage(ChatColor.RED + "This is not a valid username!");
							}
						} else {
							e.setWillClose(false);
							e.setWillDestroy(false);
						}
					}

				},"Please input Plot Name!");

				ItemStack i = new ItemStack(Material.NAME_TAG);
				ItemMeta im = i.getItemMeta();
				im.setDisplayName("");
				i.setItemMeta(im);

				gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);

				gui.open();

			} else {
				p.sendMessage(ChatColor.RED + "You do not own this region!");
			}
		} else {
			p.sendMessage(ChatColor.RED + "This region does not exists!");
		}

		return false;
	}

	public boolean removeMember(Player p, String region) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				//Close PlotGui to prompt user with AnvilGui to input name
				p.closeInventory();

				AnvilGui gui = new AnvilGui(p, new AnvilGui.AnvilClickEventHandler(){

					@Override
					public void onAnvilClick(AnvilClickEvent e) {
						if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {

							e.setWillClose(true);
							e.setWillDestroy(true);
							String name = e.getName();
							wordMatcher = wordPattern.matcher(name);

							//Check if name contains 1 word and only alphabetical characters
							if (wordMatcher.matches()) {

								Player user = Bukkit.getPlayer(name);

								if (user == null) {
									p.sendMessage(ChatColor.RED + "This player is not online!");

								} else {

									World world = Bukkit.getWorld(config.getString("World_Name"));
									
									RegionContainer container = wg.getRegionContainer();
									RegionManager regions = container.get(world);

									ProtectedRegion claim = regions.getRegion(region);
									DefaultDomain members = claim.getMembers();

									Set<String> set = members.getPlayers();

									if (!(set.contains(name))) {

										members.removePlayer(name);

										claim.setMembers(members);
										mysql.removeMember(region, user.getUniqueId().toString());

										Permissions perms = new Permissions();
										
										perms.removePermission(p.getUniqueId(), region);
										
										try {
											regions.save();
										} catch (StorageException e1) {
											e1.printStackTrace();
										}
									} else {
										p.sendMessage(ChatColor.RED + "This user not a member of this region!");
									}
								}

							} else {
								p.sendMessage(ChatColor.RED + "This is not a valid username!");
							}
						} else {
							e.setWillClose(false);
							e.setWillDestroy(false);
						}
					}

				},"Please input Plot Name!");

				ItemStack i = new ItemStack(Material.NAME_TAG);
				ItemMeta im = i.getItemMeta();
				im.setDisplayName("");
				i.setItemMeta(im);

				gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);

				gui.open();
			} else {
				p.sendMessage(ChatColor.RED + "You do not own this region!");
			}
		} else {
			p.sendMessage(ChatColor.RED + "This region does not exists!");
		}

		return false;
	}
	
	public boolean removeRegion(Player p, String region) {
		
		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				World world = Bukkit.getWorld(config.getString("World_Name"));
				
				RegionContainer container = wg.getRegionContainer();
				RegionManager regions = container.get(world);

				regions.removeRegion(region);
				String members = mysql.removeRegion(region);
				
				if (members == null) {
					return true;
				}
				
				String[] regionMembers = members.split(",");
				
				Permissions perms = new Permissions();
				perms.removePermission(p.getUniqueId(), region);
			
				for (int i = 0 ; i < regionMembers.length ; i++) {
					
					perms.removePermission(UUID.fromString(regionMembers[i]), region);
					
				}
				
				
				try {
					regions.save();
				} catch (StorageException e1) {
					e1.printStackTrace();
				}
				
			} else {
				p.sendMessage(ChatColor.RED + "You do not own this region!");
			}
		} else {
			p.sendMessage(ChatColor.RED + "This region does not exists!");
		}
				
		return true;
	}

	private WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}

		return (WorldGuardPlugin) plugin;
	}

}
