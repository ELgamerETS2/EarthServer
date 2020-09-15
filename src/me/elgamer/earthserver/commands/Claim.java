package me.elgamer.earthserver.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.elgamer.earthserver.gui.ClaimGui;
import net.md_5.bungee.api.ChatColor;

public class Claim implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		//Check is command sender is a player
		if (!(sender instanceof Player)) {
			sender.sendMessage("&cYou cannot create a plot!");
			return true;
		}

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

		return false;
	}



}
