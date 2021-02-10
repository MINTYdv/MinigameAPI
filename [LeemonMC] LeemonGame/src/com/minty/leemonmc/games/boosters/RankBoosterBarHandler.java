package com.minty.leemonmc.games.boosters;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.core.events.CoreInitEvent;
import com.minty.leemonmc.games.LeemonGame;

public class RankBoosterBarHandler implements Listener {

	public BossBar bar;
	private LeemonGame main;
	
	public RankBoosterBarHandler(LeemonGame _main) {
		this.main = _main;
	}
	
	public void delete()
	{
		bar.setVisible(false);
	}
	
	public void showBossBarToAllPlayers()
	{
        for(Player players : Bukkit.getOnlinePlayers())
        {
        	if(bar.getPlayers().contains(players)) continue;
        	bar.addPlayer(players);
            bar.setVisible(true);
        }
	}
	
	public void hideBossBarToAllPlayers()
	{
        for(Player players : Bukkit.getOnlinePlayers())
        {
        	if(!bar.getPlayers().contains(players)) continue;
        	bar.removePlayer(players);
            bar.setVisible(false);
        }
	}
	
	@EventHandler
	public void onInit(CoreInitEvent e)
	{
		setupBossBar();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if(main.getApi().getServerManager().getServerType() == ServerType.MINIGAME) {
			if(main.getGameManager().getCurrentState() == GameState.WAITING)
			{
				showBossBarToAllPlayers();
			} else
			{
				hideBossBarToAllPlayers();
			}
		} else {
			hideBossBarToAllPlayers();
		}
	}
	
	public void setupBossBar()
	{
		System.out.println("boss bar setup");

		bar = Bukkit.createBossBar("§fBienvenue sur §e§lLeemonMC §f!", BarColor.YELLOW, BarStyle.SEGMENTED_6, new BarFlag[0]);
		
		hideBossBarToAllPlayers();
		
        new BukkitRunnable() {

			@Override
			public void run()
			{
				if(main.getApi().getServerManager().getServerType() == ServerType.MINIGAME) {
					if(main.getGameManager().getCurrentState() == GameState.WAITING)
					{
						showBossBarToAllPlayers();
						
				        for(@SuppressWarnings("unused") Player players : Bukkit.getOnlinePlayers())
				        {
				        	if(!main.getRankBoosterManager().isBoosted())
				        	{
				        		hideBossBarToAllPlayers();
				        	} else
				        	{
				        		showBossBarToAllPlayers();
				        		bar.setVisible(true);
				        	}
				        }
			        	if(!main.getRankBoosterManager().isBoosted())
			        	{
			        		hideBossBarToAllPlayers();
			        	} else
			        	{
			        		showBossBarToAllPlayers();
			        		bar.setVisible(true);
			        		bar.setTitle(main.getRankBoosterManager().getBoosterBarMessage());
			        	}
					} else
					{
						hideBossBarToAllPlayers();
					}
				} else {
					hideBossBarToAllPlayers();
				}
				
			}
        	
        }.runTaskTimer(main, 20, 20);
	}
	
}
