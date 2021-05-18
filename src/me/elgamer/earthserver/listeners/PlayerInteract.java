package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.NavigationGui;

public class PlayerInteract implements Listener {
	
	public PlayerInteract(Main plugin) {

		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);

	}

	@EventHandler
	public void interactEvent(PlayerInteractEvent e) {
		
		if (e.getPlayer().getOpenInventory().getType() != InventoryType.CRAFTING && e.getPlayer().getOpenInventory().getType() != InventoryType.CREATIVE) {
		    return;
		}
		
		if (e.getPlayer().getInventory().getItemInMainHand().equals(Main.gui)) {
			e.setCancelled(true);
			e.getPlayer().openInventory(NavigationGui.GUI(e.getPlayer()));
		}
	}

}