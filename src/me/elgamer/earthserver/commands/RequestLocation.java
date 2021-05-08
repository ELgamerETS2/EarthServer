package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.LocationSQL;

public class RequestLocation implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be sent from the console!");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (!(p.hasPermission("earthserver.location.request"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		
		if (args.length != 1) {
			p.sendMessage(ChatColor.RED + "/locationrequest <name>");
			return true;
		}
		
		if (LocationSQL.requestExists(args[0])) {
			p.sendMessage("This location has already been requested");
			return true;
		}
		
		LocationSQL.addRequest(args[0], p.getLocation());	
		return true;
	}

}
