package com.minty.leemonmc.games.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.games.LeemonGame;

public class SpectatorHandler
{

	private LeemonGame main;
	private Map<Player, Boolean> spectators = new HashMap<>();
	
	public SpectatorHandler(LeemonGame _main)
	{
		main = _main;
	}
	
	public void setSpectator(Player player)
	{
		if(spectators.containsKey(player))
		{
			spectators.remove(player);
		}
		
		spectators.put(player, true);
		player.setAllowFlight(true);
		
		if(main.getGameManager().getPlayingPlayersList().size() > 1)
		{
			Player target = main.getGameManager().getPlayingPlayersList().get(0);
			player.teleport(target);
			player.setFlying(true);
		}
		
		player.setGameMode(GameMode.SPECTATOR);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		
		Inventory inv = player.getInventory();
		player.getInventory().clear();
		
		inv.setItem(8, main.getApi().getLeemonUtils().getLobbyItem());
		inv.setItem(4, main.getApi().getGuiUtils().createItem(Material.NETHER_STAR, "§6Rejouer §7§o(Clic-gauche)", (byte) 0));
	}
	
	public List<Player> getSpectators()
	{
		List<Player> result = new ArrayList<>();
		for(Entry<Player, Boolean> entry : spectators.entrySet())
		{
			if(entry.getValue()) {
				result.add(entry.getKey());
			}
		}
		return result;
	}
	
	public boolean isSpectator(Player player)
	{
		if(!spectators.containsKey(player))
		{
			spectators.put(player, false);
		}
		return spectators.get(player);
	}
	
	public void leaveSpectator(Player player)
	{
		if(spectators.containsKey(player))
		{
			spectators.remove(player);
		}
		
		player.setAllowFlight(false);
		spectators.put(player, false);
	}
	
}
