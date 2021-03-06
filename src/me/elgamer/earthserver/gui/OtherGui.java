package me.elgamer.earthserver.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.utils.LocationSQL;
import me.elgamer.earthserver.utils.Utils;

public class OtherGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Other";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (LocationSQL.CategoryCount("wales") + LocationSQL.CategoryCount("scotland") + LocationSQL.CategoryCount("northern-ireland") + LocationSQL.CategoryCount("other") > 21) {

			ArrayList<String[]> locations = LocationSQL.getLocations("other");

			int i = 11;

			for (String[] s : locations) {

				Utils.createItemByte(inv, Material.CONCRETE, 5, 1, i, ChatColor.AQUA + "" + ChatColor.BOLD + s[0] + ", " + s[1], Utils.chat("&fClick to teleport to this location"));

				i++;

				if (i % 9 == 0) {
					i = i + 2;
				}

			}


		} else {

			ArrayList<String[]> locations = LocationSQL.getLocations("scotland");

			int i = 11;

			if (locations == null) {

			} else {

				for (String[] s : locations) {

					Utils.createItemByte(inv, Material.CONCRETE, 5, 1, i, ChatColor.AQUA + "" + ChatColor.BOLD + s[0] + ", " + s[1] + ", Scotland", Utils.chat("&fClick to teleport to this location"));

					i++;

					if (i % 9 == 0) {
						i = i + 2;
					}

				}

			}

			locations = LocationSQL.getLocations("wales");

			if (locations == null) {

			} else {

				for (String[] s : locations) {

					Utils.createItemByte(inv, Material.CONCRETE, 5, 1, i, ChatColor.AQUA + "" + ChatColor.BOLD + s[0] + ", " + s[1] + ", Wales", Utils.chat("&fClick to teleport to this location"));

					i++;

					if (i % 9 == 0) {
						i = i + 2;
					}

				}
			}
			
			locations = LocationSQL.getLocations("northern-ireland");

			if (locations == null) {

			} else {

				for (String[] s : locations) {

					Utils.createItemByte(inv, Material.CONCRETE, 5, 1, i, ChatColor.AQUA + "" + ChatColor.BOLD + s[0] + ", " + s[1] + ", Northern Ireland", Utils.chat("&fClick to teleport to this location"));

					i++;

					if (i % 9 == 0) {
						i = i + 2;
					}

				}
			}
			
			locations = LocationSQL.getLocations("other");

			if (locations == null) {

			} else {

				for (String[] s : locations) {

					Utils.createItemByte(inv, Material.CONCRETE, 5, 1, i, ChatColor.AQUA + "" + ChatColor.BOLD + s[0] + ", " + s[1], Utils.chat("&fClick to teleport to this location"));

					i++;

					if (i % 9 == 0) {
						i = i + 2;
					}

				}
			}

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 45, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the location menu."));	

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {

			p.closeInventory();
			p.openInventory(LocationGui.GUI(p));

		} else {

			String[] location = clicked.getItemMeta().getDisplayName().replace(" ", "").split(",");
			p.teleport(LocationSQL.getLocation(ChatColor.stripColor(location[0])));
			
		}
	}

}
