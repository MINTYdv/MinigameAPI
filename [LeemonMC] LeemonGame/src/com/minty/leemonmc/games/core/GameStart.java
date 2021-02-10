package com.minty.leemonmc.games.core;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.core.util.Title;
import com.minty.leemonmc.games.LeemonGame;

public class GameStart {

	private LeemonGame main;
	private boolean gameStarted = false;
	private Title title;
	
	public boolean countingDown = false;
	private int countdownTimer = 0;
	
	public GameStart(LeemonGame _main)
	{
		title = new Title();
		gameStarted = false;
		countingDown = false;
		main = _main;
		
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		countDown();
        	}
        }.runTaskTimer(main, 0, 20);
	}
	
	public void startCounting()
	{
		countingDown = true;
	}
	
	public void stopCounting() {
		countingDown = false;
		setCountDown(0);
	}
	
	public int getCountdown() {
		return countdownTimer;
	}
	
	public void setCountDown(int _target)
	{
		countdownTimer = _target;
	}
	
	private void countDown()
	{
		if(gameStarted == true || countingDown == false) return;
		
		countingDown = true;
		countdownTimer--;
		
		if(countdownTimer == 30)
		{
			Bukkit.broadcastMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §7La partie démarrera dans §e" + countdownTimer + " §7secondes !");
			for(Player player : Bukkit.getOnlinePlayers()) 
			{
				title.sendActionBar(player, "§6Démarrage dans " + countdownTimer + "s !");
			}

		}
		
		if(countdownTimer == 10)
		{
			Bukkit.broadcastMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §7La partie démarrera dans §e" + countdownTimer + " §7secondes !");
			for(Player player : Bukkit.getOnlinePlayers()) 
			{
				title.sendActionBar(player, "§6Démarrage dans " + countdownTimer + "s !");
			}
		}
		
		if(countdownTimer <= 10)
		{
			for(Player player : Bukkit.getOnlinePlayers()) 
			{
				title.sendTitle(player, 0, 20, 20, "§7", "§6Démarrage dans " + countdownTimer + "s !");
				title.sendActionBar(player, "§6Démarrage dans " + countdownTimer + "s !");
				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HAT, 1f, 1f); // ne pas toucher
			}
		}

		if(countdownTimer == 0)
		{
			gameStarted = true;
			main.getGameManager().setCurrentState(GameState.PLAYING);
			main.getGameManager().startGame();
			for(Player player : Bukkit.getOnlinePlayers()) 
			{
				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
			}
		}
		
	}
	
}
