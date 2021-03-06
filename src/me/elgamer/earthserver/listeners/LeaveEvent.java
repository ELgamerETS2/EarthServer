package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.utils.MySQL;

public class LeaveEvent implements Listener {
	
	public LeaveEvent(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		MySQL mysql = new MySQL();

		String region = mysql.getRegions(p.getUniqueId().toString()); 
		
		if (region == null) {
			return;
		}
		
		String[] regions = region.split(";");
	
		for (int i = 0; i<regions.length; i++) {
			mysql.updateTime(regions[i]);
		}
		
	}

}
