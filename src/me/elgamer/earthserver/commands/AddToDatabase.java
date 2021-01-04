package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.MySQL;

public class AddToDatabase implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (sender instanceof Player) {
			//Convert sender to player
			Player p = (Player) sender;

			if (!(p.hasPermission("earthserver.addtodatabase"))) {
				p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
				return true;
			}
		}

		if (args.length != 3 && args.length != 4) {
			return false;
		}

		MySQL mysql = new MySQL();

		if (!mysql.createRegion(args[1], args[0])) {
			return false;
		}

		if (args[2].equals("true")) {
			mysql.setPublic(args[0]);
		}

		if (args.length == 4) {
			String[] members = args[3].split(",");

			for (int i = 0; i < members.length; i++) {
				mysql.addMember(args[0], members[i]);
			}
		}

		sender.sendMessage("Added the region " + args[0] + " to the database!");
		
		return true;
	}

}
