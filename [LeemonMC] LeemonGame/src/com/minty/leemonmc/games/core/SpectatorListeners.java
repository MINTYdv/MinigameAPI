package com.minty.leemonmc.games.core;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.handlers.MiniGameManager;

public class SpectatorListeners implements Listener {

	private LeemonGame main = LeemonGame.getInstance();
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player)
		{
			Player player = (Player) e.getEntity();
			
			if(main.getSpectatorHandler().isSpectator(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e)
	{
		Player player = (Player) e.getWhoClicked();
		if(player == null) return;
		ItemStack it = e.getCurrentItem();
		if(it == null) return;
		
		if(player.getGameMode() != GameMode.SPECTATOR) return;
		
		e.setCancelled(true);
		
		switch (it.getType()) {
		case BED:
			if(main.getSpectatorHandler().isSpectator(player))
			{
				MiniGameManager.hubItem(player);
			}
			break;

		default:
			break;
		}
		
	}
	
}
