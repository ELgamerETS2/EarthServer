package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.ClaimGui;

public class InventoryClicked implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public InventoryClicked(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		String title = e.getInventory().getTitle();
		if (title.equals(ClaimGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ClaimGui.inventory_name)) {
				ClaimGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}
	}

}
