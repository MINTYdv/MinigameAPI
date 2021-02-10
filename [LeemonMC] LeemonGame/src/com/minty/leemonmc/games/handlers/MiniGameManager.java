package com.minty.leemonmc.games.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.core.util.Title;
import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.core.GameStart;
import com.minty.leemonmc.games.core.Team;
import com.minty.leemonmc.games.events.GameStartEvent;
import com.minty.leemonmc.games.util.ScoreboardTeam;

import net.md_5.bungee.api.ChatColor;

public class MiniGameManager
{
	
	private GameState currentState;

	private int playingPlayers;
	private List<Player> playingPlayersList = new ArrayList<>();
	private int playersNeededFull;
	private LeemonGame main;
	private Title title;
	public GameStart gs;
	private int restartTimer;
	private String minigameName;
	
	private int gameTimerSeconds;
	private int gameTimerMinutes;
	private boolean gameTimerCounting;
	
	public MiniGameManager(LeemonGame _main)
	{
		main = _main;
		gameTimerSeconds = 0;
		gameTimerMinutes = 0;
	}
	
	public static void hubItem(Player p)
	{
		LeemonGame.getInstance().getApi().sendPlayerToHub(p);
		p.sendMessage("§aConnexion au hub en cours...");
	}
	
	public void setup()
	{
		main.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "Setuping game manager");
		if(gs == null)
		{
			gs = new GameStart(main);
		}

		playersNeededFull = getPlayersPerTeam() * main.getTeamsManager().getTeams().size();
		playingPlayers = 0;
		setCurrentState(GameState.WAITING);
		
		title = new Title();
	}
	
	public void gameTimer()
	{
		if(gameTimerCounting == true)
		{
			gameTimerSeconds++;
			
			if(gameTimerSeconds == 60) {
				gameTimerMinutes++;
				gameTimerSeconds = 0;
			}
		}
	}
	
	public void echo(String message) {
		System.out.println("[" + main.getName() + "]-> " + message);
	}

	public void checkForGameStart()
	{
		if(main.getApi().getServerManager().getServerType() != ServerType.MINIGAME) return;
		
		if(playingPlayers >= getPlayersNeededFull())
		{
			if(gs.countingDown == false) {
				gs.setCountDown(11);
				gs.startCounting();
			}

			return;
		}
		if(playingPlayers >= getPlayersNeededMin())
		{
			if(gs.countingDown == false)
			{
				gs.setCountDown(getMinPlayersCooldown());
				gs.startCounting();
				return;
			}
		}
		if(playingPlayers < getPlayersNeededMin())
		{
			gs.stopCounting();
			return;
		}
	}
	
	public void startGame()
	{
		for(Player player : main.getGameManager().getPlayingPlayersList())
		{
			player.getInventory().clear();
		}
		
		main.getGameManager().setCurrentState(GameState.PLAYING);
		restartTimer = getConfigRestartTimer();
		for(Player player : main.getGameManager().getPlayingPlayersList())
		{
			
			Team team = main.getTeamsManager().getTeam(player);
			if(team == null)
			{
				team = main.getTeamsManager().getAvailableTeam();
				main.getTeamsManager().addPlayerToTeam(player, team);
			}
			
			@SuppressWarnings("unused")
			ScoreboardTeam sbTeam = main.getTeamsManager().teamToSbTeam(team);
			handleAddingTeams(player);
		}
		Bukkit.getPluginManager().callEvent(new GameStartEvent());
	}
	
	private void handleAddingTeams(Player player)
	{
		for(ScoreboardTeam team : main.getTeamsManager().getScoreboardTeams())
		{
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(team.createTeam());
		}
		
		for(Player players1 : Bukkit.getOnlinePlayers())
		{
			for(Player players2 : Bukkit.getOnlinePlayers())
			{
				Team gameTeam = main.getTeamsManager().getTeam(players2);
				
				ScoreboardTeam team = main.getTeamsManager().teamToSbTeam(gameTeam);

				if(team != null)
				{
					((CraftPlayer) players1).getHandle().playerConnection.sendPacket(team.addOrRemovePlayer(3, players2.getName()));
				}
			}
		}
	}
	
	public void startCountingGame()
	{
		gs.startCounting();
	}
	
	public void endGame(Team winner) {
		
		if(main.getApi().getServerManager().getServerType() != ServerType.MINIGAME) return;
		if(getCurrentState() == GameState.FINISH) return;
		
		for(Player player : main.getGameManager().getPlayingPlayersList())
		{
			main.getSpectatorHandler().setSpectator(player);
			if(main.getTeamsManager().getTeam(player) != winner)
			{
				title.sendTitle(player, 0, 120, 20, "§c☠ Défaite ! ☠", "§7L'équipe " + winner.getTag() + winner.getName() + " §7 remporte la partie !");
			}
		}
		for(Player player : winner.getPlayers())
		{
			title.sendTitle(player, 0, 120, 20, "§e✦ VICTOIRE ✦", "§7L'équipe " + winner.getTag() + winner.getName() + " §7 remporte la partie !");
		}
		
		setCurrentState(GameState.FINISH);
		Bukkit.broadcastMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §7L'équipe " + winner.getTag() + winner.getName() + "§7 remporte la partie !");
		
		// RESTART SERVER
		
		restartTimer++;
        @SuppressWarnings("unused")
		BukkitTask _task = new BukkitRunnable() {

        	@Override
        	public void run()
        	{
        		for(Player player : winner.getPlayers())
        		{
                    Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                    FireworkMeta fwm = fw.getFireworkMeta();
        			
        			Random r = new Random(); 
        			
                    Color c1 = winner.getArmorColor();
                    
                    fwm.addEffect(FireworkEffect.builder().withColor(c1).flicker(true).build());
                    
                    int rp = r.nextInt(2) + 1;
                    fwm.setPower(rp);
                    fw.setFireworkMeta(fwm);
                    
                    BukkitTask __task = new BukkitRunnable() {
                    	public void run() {
                    		fw.detonate();
                    	}
                    }.runTaskTimer(main, 5, 5);
                    
        		}
        		
        		if(restartTimer == 2)
        		{
        			for(Player player : Bukkit.getOnlinePlayers())
        			{
        				main.getApi().sendPlayerToHub(player);
        			}
        		}
        		if(restartTimer == 0)
        		{
        			Bukkit.shutdown();
        			this.cancel();
        		}
        		restartTimer--;
        	}
        	
        }.runTaskTimer(main, 0, 20);
	}
	
	
	
	/* 
	 * GETTERS AND SETTERS
	 * */
	
	public void addPlayingPlayer(Player player)
	{
		if(main.getApi().getServerManager().getServerType() != ServerType.MINIGAME) return;
		
		if(playingPlayers < getPlayersNeededFull())
		{
			playingPlayersList.add(player);
			playingPlayers++;
		}
		
	}

	public void checkForTeamEmpty() {
		for(Team team : main.getTeamsManager().getTeams()) {
			if(team.getPlayers().size() <= 0)
			{
				team.eliminate();
			}
		}
		
		List<Team> notEmptyTeams = new ArrayList<>();
		for(Team team : main.getTeamsManager().getTeams()) {
			if(team.isEmpty() == false)
			{
				notEmptyTeams.add(team);
			}
		}
		if(notEmptyTeams.size() == 1) {
			endGame(notEmptyTeams.get(0));
		}
	}
	
	public void removePlayingPlayer(Player player) {
		if(main.getApi().getServerManager().getServerType() != ServerType.MINIGAME) return;
		playingPlayers--;
		if(getPlayingPlayersList().contains(player))
		{
			getPlayingPlayersList().remove(player);
		}
		if(playingPlayers < 0)
		{
			playingPlayers = 0;
		}

	}

	public int getPlayersNeededFull() {
		return playersNeededFull;
	}
	
	public GameState getCurrentState()
	{
		return currentState;
	}
	
	public int getFullPlayersCooldown()
	{
		return main.getConfig().getInt("game-cooldown-full");
	}
	
	public int getMinPlayersCooldown()
	{
		return main.getConfig().getInt("game-cooldown-mini");
	}
	
	public boolean gameIsFull() {
		if(playingPlayers == playersNeededFull)
		{
			return true;
		} else {
			return false;
		}
	}
	
	public void setCurrentState(GameState currentState)
	{
		this.currentState = currentState;
	}
	
	private int getConfigRestartTimer()
	{
		return main.getConfig().getInt("restart-cooldown");  
	}
	
	public void setGameTimerCounting(boolean gameTimerCounting) {
		this.gameTimerCounting = gameTimerCounting;
	}
	
	public int getGameTimerSeconds() {
		return gameTimerSeconds;
	}
	
	public int getGameTimerMinutes() {
		return gameTimerMinutes;
	}
	
	public void setMinigameName(String minigameName) {
		this.minigameName = minigameName;
	}
	
	public String getMinigameName() {
		return minigameName;
	}
	
	public int getPlayersPerTeam()
	{
		return main.getConfig().getInt("players-per-team");
	}
	
	public int getPlayersNeededMin()
	{
		return main.getConfig().getInt("players-min"); 
	}
	
	public boolean isGameTimerCounting() {
		return gameTimerCounting;
	}
	
	@Deprecated
	public int getPlayingPlayers() {
		return playingPlayers;
	}
	
	public List<Player> getPlayingPlayersList() {
		return playingPlayersList;
	}
	
	public int getRestartTimer()
	{
		return restartTimer;
	}
	
}
