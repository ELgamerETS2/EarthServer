package me.elgamer.earthserver.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.Utils;

public class SwitchServerGui {

	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 3 * 9;

	public static void initialize() {
		inventory_name = ChatColor.AQUA + "" + ChatColor.BOLD + "Servers";

		inv = Bukkit.createInventory(null, inv_rows);

	}

	public static Inventory GUI (Player p) {

		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);

		inv.clear();

		Utils.createItem(inv, Material.BEACON, 1, 12, ChatColor.AQUA + "" + ChatColor.BOLD + "Lobby Server",
				Utils.chat("&fTeleports you back to the lobby server."));

		Utils.createItem(inv, Material.BRICK, 1, 14, ChatColor.AQUA + "" + ChatColor.BOLD + "Building Server",
				ChatColor.WHITE + "Teleport to the building server.",
				ChatColor.WHITE + "No requirements to start building.",
				ChatColor.WHITE + "Recommended version is 1.16.5.",
				ChatColor.WHITE + "Supports 1.12.2 - 1.16.5.",
				ChatColor.WHITE + "Currently in the Testing Phase!");	

		Utils.createItem(inv, Material.PUMPKIN, 1, 16, ChatColor.AQUA + "" + ChatColor.BOLD + "Minigames Server",
				ChatColor.WHITE + "Teleport to the minigames server.",
				ChatColor.WHITE + "Current minigames: Hide'n'Seek.",
				ChatColor.WHITE + "Supports 1.12.2 - 1.16.5.");	

		Utils.createItem(inv, Material.SPRUCE_DOOR_ITEM, 1, 27, ChatColor.AQUA + "" + ChatColor.BOLD + "Return", 
				Utils.chat("&fGo back to the navigation menu."));	

		toReturn.setContents(inv.getContents());
		return toReturn;
	}

	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {

		if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Lobby Server")) {

			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("lobby");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Return")) {
			p.closeInventory();
			p.openInventory(NavigationGui.GUI(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Building Server")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("building");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "" + ChatColor.BOLD + "Minigames Server")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF("minigames");

			p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
		}
	}

}
