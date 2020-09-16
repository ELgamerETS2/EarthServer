package me.elgamer.earthserver.gui;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.elgamer.earthserver.utils.ClaimRegion;
import me.elgamer.earthserver.utils.Utils;

public class ClaimGui {
	
	public static Inventory inv;
	public static String inventory_name;
	public static int inv_rows = 5 * 9;
	
	public static void initialize() {
		inventory_name = Utils.chat("Claim");
		
		inv = Bukkit.createInventory(null, inv_rows);
		
	}
	
	public static Inventory GUI (Player p) {
		
		Inventory toReturn = Bukkit.createInventory(null, inv_rows, inventory_name);
		
		inv.clear();
		
		Utils.createItemByte(inv, 251, 5, 1, 13, "&cClaim", "&7The 512x512 area you are standing in will be claimed and accessible only for you!");
		Utils.createItemByte(inv, 251, 13, 1, 15, "&cTeam Claim", "&7The 512x512 are you are standing in will be claimed and accessible to all builders!");
		Utils.createItemByte(inv, 251, 3, 1, 11, "&cAdd", "&7Add a player to you claim!");
		Utils.createItemByte(inv, 251, 11, 1, 29, "&cRemove", "&7Remove a player from you claim!");
		Utils.createItemByte(inv, 251, 14, 1, 32, "&cUnclaim", "&7Remove the claim you are standing in!");
		Utils.createItemByte(inv, 251, 2, 1, 17, "&cPublic", "&7Make your claim accessible to all builders!");
		Utils.createItemByte(inv, 251, 10, 1, 35, "&cPrivate", "&7Remove the ability for all builders to access your claim!");
		
		toReturn.setContents(inv.getContents());
		return toReturn;
	}
	
	public static void clicked(Player p, int slot, ItemStack clicked, Inventory inv) {
		
		ClaimRegion claim = new ClaimRegion();
		
		if (clicked == null) {
					
	    } else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cClaim"))) {
			claim.createRegion(p, getRegion(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cTeam Claim"))) {
			if (claim.createRegion(p, getRegion(p))) {
				claim.setPublic(p, getRegion(p));
			}
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cPublic"))) {
			claim.setPublic(p, getRegion(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cPrivate"))) {
			claim.setPrivate(p, getRegion(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cAdd"))) {
			claim.addMember(p, getRegion(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cRemove"))) {
			claim.removeMember(p, getRegion(p));
		} else if (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(Utils.chat("&cUnclaim"))) {
			claim.removeRegion(p, getRegion(p));
		} else {}
	}
	
	public static String getRegion(Player p) {
		Location l = p.getLocation();
		double x = l.getX();
		double z = l.getZ();
		int rX = (int) Math.floor((x/512));
		int rZ = (int) Math.floor((z/512));
		return (rX + "," + rZ);
	}

}
