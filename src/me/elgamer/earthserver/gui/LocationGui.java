package me.elgamer.earthserver.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.utils.LocationSQL;
import me.elgamer.earthserver.utils.Utils;

public class LocationGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Location Menu";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		if (LocationSQL.CategoryCount("london") + LocationSQL.CategoryCount("england") > 21) {

			Utils.createItem(inv, Material.GREEN_GLAZED_TERRACOTTA, 1, 11, ChatColor.AQUA + "" + ChatColor.BOLD + "England", "Locations in England excluding London");
			Utils.createItem(inv, Material.GRAY_GLAZED_TERRACOTTA, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "London", "Locations in London");	

		} else {

			Utils.createItem(inv, Material.GREEN_GLAZED_TERRACOTTA, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "England", "Locations in England");

		}

		if (LocationSQL.CategoryCount("wales") + LocationSQL.CategoryCount("scotland") + LocationSQL.CategoryCount("northern-ireland") + LocationSQL.CategoryCount("other") > 21) {

			Utils.createItem(inv, Material.ORANGE_GLAZED_TERRACOTTA, 1, 13, ChatColor.AQUA + "" + ChatColor.BOLD + "Other", "Locations not in the 4 main countries of the UK");

			Utils.createItem(inv, Material.MAGENTA_GLAZED_TERRACOTTA, 1, 15, ChatColor.AQUA + "" + ChatColor.BOLD + "Scotland", "Locations in Scotland");

			Utils.createItem(inv, Material.RED_GLAZED_TERRACOTTA, 1, 17, ChatColor.AQUA + "" + ChatColor.BOLD + "Wales", "Locations in Wales");

			Utils.createItem(inv, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Northern Ireland", "Locations in Northern Ireland");

		} else {

			Utils.createItem(inv, Material.ORANGE_GLAZED_TERRACOTTA, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Other", "Locations not in England");

		}

		Utils.createItem(inv, Material.SPRUCE_DOOR, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the navigation menu."));	

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "England")) {

			//Will open the build location gui.
			p.closeInventory();
			p.openInventory(EnglandGui.GUI(p));
			
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "London")) {
			p.closeInventory();
			p.openInventory(LondonGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Scotland")) {
			p.closeInventory();
			p.openInventory(ScotlandGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Northern Ireland")) {
			p.closeInventory();
			p.openInventory(NorthernIrelandGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Wales")) {
			p.closeInventory();
			p.openInventory(WalesGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Other")) {
			p.closeInventory();
			p.openInventory(OtherGui.GUI(p));


		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Switch Server")) {
			p.closeInventory();
			p.openInventory(SwitchServerGui.GUI(p));

		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(NavigationGui.GUI(p));}
	}

}
