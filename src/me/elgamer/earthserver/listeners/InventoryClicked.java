package me.elgamer.earthserver.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.elgamer.earthserver.Main;
import me.elgamer.earthserver.gui.ClaimGui;
//import me.elgamer.earthserver.gui.CustomGui;
import me.elgamer.earthserver.gui.EnglandGui;
import me.elgamer.earthserver.gui.LocationGui;
import me.elgamer.earthserver.gui.LondonGui;
import me.elgamer.earthserver.gui.NavigationGui;
import me.elgamer.earthserver.gui.NorthernIrelandGui;
import me.elgamer.earthserver.gui.OtherGui;
import me.elgamer.earthserver.gui.ScotlandGui;
import me.elgamer.earthserver.gui.SwitchServerGui;
import me.elgamer.earthserver.gui.WalesGui;

public class InventoryClicked implements Listener {
	
	@SuppressWarnings("unused")
	private Main plugin;
	
	public InventoryClicked(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if (e.getCurrentItem() == null) {
			return;
		}
		
		if (e.getCurrentItem().hasItemMeta() == false) {
			return;
		}
		
		String title = e.getView().getTitle();
				
		if (title.equals(ClaimGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ClaimGui.inventory_name)) {
				ClaimGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(NavigationGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(NavigationGui.inventory_name)) {
				NavigationGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(SwitchServerGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(SwitchServerGui.inventory_name)) {
				SwitchServerGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(LocationGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(LocationGui.inventory_name)) {
				LocationGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(EnglandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(EnglandGui.inventory_name)) {
				EnglandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(LondonGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(LondonGui.inventory_name)) {
				LondonGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(NorthernIrelandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(NorthernIrelandGui.inventory_name)) {
				NorthernIrelandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(ScotlandGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ScotlandGui.inventory_name)) {
				ScotlandGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(WalesGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(WalesGui.inventory_name)) {
				WalesGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} else if (title.equals(OtherGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(OtherGui.inventory_name)) {
				OtherGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		} /*else if (title.equals(CustomGui.inventory_name)) {
			e.setCancelled(true);
			if (e.getCurrentItem() == null){
				return;
			}
			if (title.equals(ClaimGui.inventory_name)) {
				ClaimGui.clicked((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getInventory());
			}
		}*/
		
	}

}
