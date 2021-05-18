package me.elgamer.earthserver.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.Utils;

public class NavigationGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Navigation Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.BRICK, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Explore",
				Utils.chat("&fChoose a location you would like to visit."),
				Utils.chat("&fThen you can create a plot and start building."),
				Utils.chat("&fAll ranks can build here."));	

		Utils.createItem(inv, Material.EYE_OF_ENDER, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server",
				Utils.chat("&fTeleport to a different server."));	

		Utils.createItem(inv, Material.GRASS, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn",
				Utils.chat("&fTeleport to spawn."));

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Explore")) {

			//Will open the build location gui.
			p.closeInventory();
			p.openInventory(LocationGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			p.openInventory(SwitchServerGui.GUI(p));
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Spawn")) {
			p.teleport(Main.spawn);
		}
	}

}
