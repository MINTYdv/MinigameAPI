package com.minty.leemonmc.games.runnables;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.games.LeemonGame;

public class SpectatorRunnable extends BukkitRunnable
{

	private LeemonGame main = LeemonGame.getInstance();
	
	@Override
	public void run()
	{
		for(Player player : main.getSpectatorHandler().getSpectators())
		{
			player.setAllowFlight(true);
		}
	}

	
}
