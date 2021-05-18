package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.LocationSQL;

public class GotoRequest implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command cannot be sent from the console!");
			return false;
		}
		
		Player p = (Player) sender;
		
		if (!(p.hasPermission("earthserver.location.torequest"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}
		
		if (args.length != 1) {
			p.sendMessage(ChatColor.RED + "/torequest <name>");
			return true;
		}
		
		if (!(LocationSQL.requestExists(args[0]))) {
			p.sendMessage(ChatColor.RED + "This location has not been requested.");
			return true;
		}
		
		p.teleport(LocationSQL.toRequestLocation(args[0]));
		return true;
	}

}
