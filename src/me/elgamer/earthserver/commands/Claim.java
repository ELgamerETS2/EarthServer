package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.gui.ClaimGui;
import me.elgamer.earthserver.utils.ClaimRegion;
import me.elgamer.earthserver.utils.Permissions;

public class Claim implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot claim a region!");
			return true;
		}
		
		ClaimRegion claim = new ClaimRegion();

		//Convert sender to player
		Player p = (Player) sender;
		
		//Check if player has permission
		if (!(p.hasPermission("earthserver.claim"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		//If command is run without args then open the claim gui
		if (args.length == 0) {
			p.openInventory(ClaimGui.GUI(p));
			return true;
		}

		if (args.length > 1) {
			return false;
		}
		
		if (args[0].equalsIgnoreCase("help")) {
			claim.help(p);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("info")) {
			claim.info(p, getRegion(p));
			return true;
		}

		try {
			int radius = Integer.parseInt(args[0]);			

			if (radius > 2 || radius < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, radius);
			
			for (int i = 0; i < points.length; i++) {
				claim.createRegion(p, points[i]);
			}
			
			Permissions.updatePermissions();

		} catch (NumberFormatException e) {
			p.sendMessage(ChatColor.RED + "You must use an integer value between 0 and 2");
			return false;
		}

		return true;
	}

	public static String getRegion(Player p) {
		Location l = p.getLocation();
		double x = l.getX();
		double z = l.getZ();
		int rX = (int) Math.floor((x/512));
		int rZ = (int) Math.floor((z/512));
		return (rX + "," + rZ);
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

}
