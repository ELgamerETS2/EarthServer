package me.elgamer.earthserver.utils;

import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
			if (mysql.getOwner(region).equals(p.getUniqueId().toString())) {
				p.sendMessage(ChatColor.RED + "You already own the region " + region + "!");
				return false;
			}
			if (Bukkit.getPlayer(mysql.getOwner(region)) != null) {
				String regionOwner = Bukkit.getPlayer(UUID.fromString(mysql.getOwner(region))).getName();
				p.sendMessage(ChatColor.RED + ("The region " + region + " is already claimed by " + regionOwner + "!"));
				p.sendMessage(ChatColor.RED + ("If you believe this is wrongly claimed please contact staff!"));
			} else {
				String regionOwner = Bukkit.getOfflinePlayer(UUID.fromString(mysql.getOwner(region))).getName();
				p.sendMessage(ChatColor.RED + ("The region " + region + " is already claimed by " + regionOwner + "!"));
				p.sendMessage(ChatColor.RED + ("If you believe this is wrongly claimed please contact staff!"));
			}
			return false;
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

		mysql.addPermission(p.getUniqueId().toString(), region);

		p.sendMessage(ChatColor.GREEN + "Region " + region + " created!");

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

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {
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

					mysql.addPermission("builder", region);

					p.sendMessage(ChatColor.GREEN + "Region " + region + " is now open for all builders!");

					try {
						regions.save();
					} catch (StorageException e1) {
						e1.printStackTrace();
					}
				} else {
					p.sendMessage(ChatColor.RED + "The region " + region + " is already public!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
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

					mysql.removePermission("builder", region);

					p.sendMessage(ChatColor.RED + "Region " + region + " is now private, only the region owner and members have access!");

					try {
						regions.save();
					} catch (StorageException e1) {
						e1.printStackTrace();
					}
				} else {
					p.sendMessage(ChatColor.RED + "The region " + region + " is already public!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
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

					@SuppressWarnings({ "deprecation" })
					@Override
					public void onAnvilClick(AnvilClickEvent e) {
						if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {

							e.setWillClose(true);
							e.setWillDestroy(true);
							String name = e.getName();
							wordMatcher = wordPattern.matcher(name);

							//Check if name contains 1 word and only alphabetical characters
							if (wordMatcher.matches()) {
								
								String uuid;
								
								if (Bukkit.getPlayer(name) != null) {
									uuid = Bukkit.getPlayer(name).getUniqueId().toString();
								} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
									uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
								} else {
									uuid = null;
								}

								if (uuid != null) {

									if (uuid.equals(p.getUniqueId().toString())) {
										p.sendMessage(ChatColor.RED + "You already own this region!");
									} else {

										World world = Bukkit.getWorld(config.getString("World_Name"));

										RegionContainer container = wg.getRegionContainer();
										RegionManager regions = container.get(world);

										ProtectedRegion claim = regions.getRegion(region);
										DefaultDomain members = claim.getMembers();

										Set<UUID> set = members.getUniqueIds();

										if (!(set.contains(UUID.fromString(uuid)))) {

											members.addPlayer(UUID.fromString(uuid));

											claim.setMembers(members);
											mysql.addMember(region, uuid);

											mysql.addPermission(uuid, region);

											p.sendMessage(ChatColor.GREEN + name + " added to region " + region + "!");

											try {
												regions.save();
											} catch (StorageException e1) {
												e1.printStackTrace();
											}

										} else {
											p.sendMessage(ChatColor.RED + name + " is already a member of this region!");
										}
									}

								} else {
									p.sendMessage(ChatColor.RED + name + " has never connected to this server!");
								}

							} else {
								p.sendMessage(ChatColor.RED + name + " is not a valid username!");
							}

						} else {
							e.setWillClose(false);
							e.setWillDestroy(false);
						}
					}

				},"Please input username!");

				ItemStack i = new ItemStack(Material.NAME_TAG);
				ItemMeta im = i.getItemMeta();
				im.setDisplayName("Input user");
				i.setItemMeta(im);

				gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);

				gui.open();

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
			return false;
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

					@SuppressWarnings("deprecation")
					@Override
					public void onAnvilClick(AnvilClickEvent e) {
						if (e.getSlot() == AnvilGui.AnvilSlot.OUTPUT) {

							e.setWillClose(true);
							e.setWillDestroy(true);
							String name = e.getName();
							wordMatcher = wordPattern.matcher(name);

							//Check if name contains 1 word and only alphabetical characters
							if (wordMatcher.matches()) {

								String uuid;

								if (Bukkit.getPlayer(name) != null) {
									uuid = Bukkit.getPlayer(name).getUniqueId().toString();
								} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
									uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
								} else {
									uuid = null;
								}

								if (uuid != null) {

									World world = Bukkit.getWorld(config.getString("World_Name"));

									RegionContainer container = wg.getRegionContainer();
									RegionManager regions = container.get(world);

									ProtectedRegion claim = regions.getRegion(region);
									DefaultDomain members = claim.getMembers();

									Set<UUID> set = members.getUniqueIds();

									if (set.contains(UUID.fromString(uuid))) {

										members.removePlayer(UUID.fromString(uuid));

										claim.setMembers(members);
										mysql.removeMember(region, uuid);

										mysql.removePermission(uuid, region);

										p.sendMessage(ChatColor.RED + name + " removed from region " + region + "!");

										try {
											regions.save();
										} catch (StorageException e1) {
											e1.printStackTrace();
										}
									} else {
										p.sendMessage(ChatColor.RED + name + " is not a member of this region!");
									}

								} else {
									p.sendMessage(ChatColor.RED + name + " has never connected to this server!");
								}

							} else {
								p.sendMessage(ChatColor.RED + name + " is not a valid username!");
							}

						} else {
							e.setWillClose(false);
							e.setWillDestroy(false);
						}
					}

				},"Please input username!");

				ItemStack i = new ItemStack(Material.NAME_TAG);
				ItemMeta im = i.getItemMeta();
				im.setDisplayName("Input user");
				i.setItemMeta(im);

				gui.setSlot(AnvilGui.AnvilSlot.INPUT_LEFT, i);

				gui.open();
			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
			return false;
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

				mysql.removePermission(p.getUniqueId().toString(), region);

				String members = mysql.removeRegion(region);

				p.sendMessage(ChatColor.RED + "Region " + region + " removed!");

				if (members != null) {
					String[] regionMembers = members.split(",");

					for (int i = 0 ; i < regionMembers.length ; i++) {

						mysql.removePermission(regionMembers[i], region);

					}
				}

				try {
					regions.save();
				} catch (StorageException e1) {
					e1.printStackTrace();
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
			return false;
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

	@SuppressWarnings("deprecation")
	public boolean addMember(Player p, String region, String name) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				String uuid;
				
				if (Bukkit.getPlayer(name) != null) {
					uuid = Bukkit.getPlayer(name).getUniqueId().toString();
				} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
					uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
				} else {
					uuid = null;
				}

				if (uuid != null) {

					if (uuid.equals(p.getUniqueId().toString())) {
						p.sendMessage(ChatColor.RED + "You already own this region!");
					} else {

						World world = Bukkit.getWorld(config.getString("World_Name"));

						RegionContainer container = wg.getRegionContainer();
						RegionManager regions = container.get(world);

						ProtectedRegion claim = regions.getRegion(region);
						DefaultDomain members = claim.getMembers();

						Set<UUID> set = members.getUniqueIds();

						if (!(set.contains(UUID.fromString(uuid)))) {

							members.addPlayer(UUID.fromString(uuid));

							claim.setMembers(members);
							mysql.addMember(region, uuid);

							mysql.addPermission(uuid, region);

							p.sendMessage(ChatColor.GREEN + name + " added to region " + region + "!");

							try {
								regions.save();
							} catch (StorageException e1) {
								e1.printStackTrace();
							}

						} else {
							p.sendMessage(ChatColor.RED + name + " is already a member of this region!");
						}
					}

				} else {
					p.sendMessage(ChatColor.RED + name + " has never connected to this server!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
			return false;
		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean removeMember(Player p, String region, String name) {

		Main instance = Main.getInstance();
		FileConfiguration config = instance.getConfig();

		MySQL mysql = new MySQL();
		WorldGuardPlugin wg = getWorldGuard();

		if (mysql.regionExists(region)) {

			if (p.getUniqueId().toString().equals(mysql.getOwner(region))) {

				String uuid;
				
				if (Bukkit.getPlayer(name) != null) {
					uuid = Bukkit.getPlayer(name).getUniqueId().toString();
				} else if (Bukkit.getOfflinePlayer(name) != null && Bukkit.getOfflinePlayer(name).hasPlayedBefore()){
					uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();
				} else {
					uuid = null;
				}

				if (uuid != null) {

					World world = Bukkit.getWorld(config.getString("World_Name"));

					RegionContainer container = wg.getRegionContainer();
					RegionManager regions = container.get(world);

					ProtectedRegion claim = regions.getRegion(region);
					DefaultDomain members = claim.getMembers();

					Set<UUID> set = members.getUniqueIds();

					if (set.contains(UUID.fromString(uuid))) {

						members.removePlayer(UUID.fromString(uuid));

						claim.setMembers(members);
						mysql.removeMember(region, uuid);

						mysql.removePermission(uuid, region);

						p.sendMessage(ChatColor.RED + name + " removed from region " + region + "!");

						try {
							regions.save();
						} catch (StorageException e1) {
							e1.printStackTrace();
						}
					} else {
						p.sendMessage(ChatColor.RED + name + " is not a member of this region!");
					}

				} else {
					p.sendMessage(ChatColor.RED + name + " has never connected to this server!");
				}

			} else {
				p.sendMessage(ChatColor.RED + "You do not own the region " + region +"!");
			}

		} else {
			p.sendMessage(ChatColor.RED + "The region " + region + " does not exists!");
			return false;
		}

		return false;
	}

	public void help(Player p) {

		p.closeInventory();
		p.sendMessage(Utils.chat("&7To open the claim gui do &a/claim &7or use the commands below!"));
		p.sendMessage(Utils.chat("&a/claim info &7returns the region name, owner and members!"));
		p.sendMessage(Utils.chat("&a/claim [radius] &7claims all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/unclaim [radius] &7unclaims all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/teamclaim [radius] &7claims all 512x512 regions in a square radius of regions and makes them public to all builders!"));
		p.sendMessage(Utils.chat("&a/addmember <user> [radius] &7adds the specified user to all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/removemember <user> [radius] &7removes the specified user to all 512x512 regions in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/public [radius] &7makes all 512x512 regions public to builders in a square radius of regions!"));
		p.sendMessage(Utils.chat("&a/private [radius] &7makes all 512x512 regions private in a square radius of regions!"));

	}

	public void info(Player p, String region) {

		MySQL mysql = new MySQL();
		if (mysql.regionExists(region)) {

			String regionOwner;
			
			if (Bukkit.getPlayer(mysql.getOwner(region)) != null) {
				regionOwner = Bukkit.getPlayer(UUID.fromString(mysql.getOwner(region))).getName();
			} else {
				regionOwner = Bukkit.getOfflinePlayer(UUID.fromString(mysql.getOwner(region))).getName();
			}
			
			String[] members = mysql.getMembers(region);
			Boolean isPublic = mysql.isPublic(region);
			String names = null;
			String regionMember;
			
			if (members != null) {
				for (String m : members) {
					if (Bukkit.getPlayer(UUID.fromString(m)) != null) {
						regionMember = Bukkit.getPlayer(UUID.fromString(m)).getName();
					} else {
						regionMember = Bukkit.getOfflinePlayer(UUID.fromString(m)).getName();
					}
					if (names == null) {
						names = regionMember;
					} else {
						names = names + ", " + regionMember;
					}
				}
			}
			
			if (isPublic == true) {
				p.sendMessage(ChatColor.GREEN + "The region " + region + " is claimed by " + regionOwner + " and is public");
			} else if (names == null) {
				p.sendMessage(ChatColor.GREEN + "The region " + region + " is claimed by " + regionOwner + " and is private");
			} else {
				p.sendMessage(ChatColor.GREEN + "The region " + region + " is claimed by " + regionOwner + " and has members " + names);
			}

		} else {

			p.sendMessage(ChatColor.GREEN + "The region " + region + " is unclaimed!");

		}

	}

}
