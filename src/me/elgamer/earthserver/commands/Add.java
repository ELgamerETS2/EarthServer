package me.elgamer.earthserver.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.ClaimRegion;
import me.elgamer.earthserver.utils.Permissions;

public class Add implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot add a player to a region!");
			return true;
		}

		//Convert sender to player
		Player p = (Player) sender;

		if (!(p.hasPermission("earthserver.add"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		ClaimRegion claim = new ClaimRegion();

		if (args.length == 1) { 

			claim.addMember(p, getRegion(p), args[0]);
			Permissions.updatePermissions();
			return true;
			
		}

		if (args.length != 2) {
			return false;
		}
		
		Player user = Bukkit.getPlayer(args[0]);

		if (user == null) {
			p.sendMessage(ChatColor.RED + args[0] + " is not online!");
			return true;
		}

		try {
			int radius = Integer.parseInt(args[1]);			

			if (radius > 2 || radius < 0) {
				p.sendMessage(ChatColor.RED + "You may only use a maximum radius of 2");
				return true;
			}

			String[] points = getRegions(p, radius);
			for (int i = 0; i < points.length; i++) {
				claim.addMember(p, points[i], args[0]);

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
