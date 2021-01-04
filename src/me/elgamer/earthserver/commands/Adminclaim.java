package me.elgamer.earthserver.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.utils.ClaimAdmin;
import me.elgamer.earthserver.utils.Permissions;

public class Adminclaim implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou can't administrate claims from the console!");
			return true;
		}

		//Convert sender to player
		Player p = (Player) sender;

		if (!(p.hasPermission("earthserver.admin"))) {
			p.sendMessage(ChatColor.RED + "You do not have permission for this command!");
			return true;
		}

		ClaimAdmin claim = new ClaimAdmin();

		if (args.length == 1) { 

			if (args[0].equalsIgnoreCase("unclaim")) {
				claim.unclaim(p,"0");
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("public")) {
				claim.setPublic(p,"0");
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("private")) {
				claim.setPrivate(p,"0");
				Permissions.updatePermissions();
				return true;
			}
			
			else {return false;}

		}

		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("transferowner")) {
				claim.transferowner(p,args[1],"0");
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("add")) {
				claim.addMember(p,args[1],"0");
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				claim.removeMember(p,args[1],"0");
				Permissions.updatePermissions();
				return true;
			}
			
			else if (args[0].equalsIgnoreCase("unclaim")) {
				claim.unclaim(p,args[1]);
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("public")) {
				claim.setPublic(p,args[1]);
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("private")) {
				claim.setPrivate(p,args[1]);
				Permissions.updatePermissions();
				return true;
			}
			
			else {return false;}
		}
		
		if (args.length == 3) {
			
			if (args[0].equalsIgnoreCase("transferowner")) {
				claim.transferowner(p,args[1],args[2]);
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("add")) {
				claim.addMember(p,args[1],args[2]);
				Permissions.updatePermissions();
				return true;
			}
			else if (args[0].equalsIgnoreCase("remove")) {
				claim.removeMember(p,args[1],args[2]);
				Permissions.updatePermissions();
				return true;
			}
			
			else {return false;}
			
		}
		
		if (args.length == 0) {
			claim.help(p);
			return true;
		}
		
		if (args.length > 3) {
			return false;
		}
		
		return false;
	}

}
