package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.LocationSQL;

public class AddLocation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be sent from the console!");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (!(p.hasPermission("earthserver.location.add"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		
		if (args.length < 3) {
			p.sendMessage(ChatColor.RED + "/addlocation <name> <category> <subcategory> [requestName]");
			return true;
		}
		
		if (!(args[1].equalsIgnoreCase("london") || 
				args[1].equalsIgnoreCase("england") || 
				args[1].equalsIgnoreCase("wales") ||
				args[1].equalsIgnoreCase("scotland") ||
				args[1].equalsIgnoreCase("northern-ireland") ||
				args[1].equalsIgnoreCase("other"))) {
			
			p.sendMessage(ChatColor.RED + "Category must be one of the following:");
			p.sendMessage(ChatColor.RED + "England, Scotland, Wales, Northern-Ireland, London or Other");
			return true;
			
		}	
		
		if (args.length == 4) {
			if (!(LocationSQL.requestExists(args[0]))) {
				p.sendMessage("This location has not been requested");
				return true;
			}
		} else {
			if (LocationSQL.locationExists(args[0])) {
				p.sendMessage("This location has already been added");
				return true;
			}
			
			Location l = LocationSQL.getRequestLocation(args[3]);
			
			LocationSQL.addLocation(args[0], args[1], args[2], l);	
			return true;
			
		}
		
		if (args.length != 3) {
			p.sendMessage(ChatColor.RED + "/addlocation <name> <category> <subcategory> [requestName]");
			return true;
		}
		
		if (LocationSQL.locationExists(args[0])) {
			p.sendMessage("This location has already been added");
			return true;
		}
		
		LocationSQL.addLocation(args[0], args[1], args[2], p.getLocation());	
		return true;
	}

}
