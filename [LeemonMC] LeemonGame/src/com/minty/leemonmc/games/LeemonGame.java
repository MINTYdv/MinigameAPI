package com.minty.leemonmc.games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.games.boosters.RankBoosterBarHandler;
import com.minty.leemonmc.games.boosters.RankBoosterManager;
import com.minty.leemonmc.games.cmd.CommandReplay;
import com.minty.leemonmc.games.core.GameStart;
import com.minty.leemonmc.games.core.GamesListeners;
import com.minty.leemonmc.games.core.GamesUtils;
import com.minty.leemonmc.games.core.SpectatorListeners;
import com.minty.leemonmc.games.handlers.MiniGameManager;
import com.minty.leemonmc.games.handlers.SpectatorHandler;
import com.minty.leemonmc.games.handlers.TeamsChatHandler;
import com.minty.leemonmc.games.handlers.TeamsManager;
import com.minty.leemonmc.games.rewards.GameRewardsManager;
import com.minty.leemonmc.games.runnables.SpectatorRunnable;

public class LeemonGame extends JavaPlugin {

	private CoreMain api = (CoreMain) Bukkit.getPluginManager().getPlugin("LeemonCore");
	
	private static LeemonGame instance;
	
	private MiniGameManager gameManager;
	private RankBoosterManager rankBoosterManager;
	private SpectatorHandler spectatorHandler;
	private GamesUtils gamesUtils;
	private TeamsManager teamsManager;
	private GameRewardsManager gameRewardsManager;
	private RankBoosterBarHandler rankBoosterBarHandler;
	private TeamsChatHandler teamsChatHandler;
	
	@Override
	public void onEnable()
	{
		System.out.println("[LeemonGame] Plugin actif !");
		saveDefaultConfig();
		
		registerReferences();
		registerListeners();
		registerRunnables();
		registerCommands();
	}
	
	private void registerCommands()
	{
		getCommand("re").setExecutor(new CommandReplay());
		getCommand("replay").setExecutor(new CommandReplay());
	}

	private void registerListeners()
	{
		getServer().getPluginManager().registerEvents(getGameRewardsManager(), this);
		getServer().getPluginManager().registerEvents(new GamesListeners(this), this);
		getServer().getPluginManager().registerEvents(getRankBoosterBarHandler(), this);
		getServer().getPluginManager().registerEvents(getTeamsChatHandler(), this);
		getServer().getPluginManager().registerEvents(new SpectatorListeners(), this);
	}
	
	private void registerReferences()
	{
		api = (CoreMain) Bukkit.getPluginManager().getPlugin("LeemonCore");
		instance = this;
		
		getApi().log("Setuping teams & game managers!...");
		gameManager = new MiniGameManager(this);
		teamsManager = new TeamsManager(this);
		getTeamsManager().setup();
		
		getGameManager().setup();
		spectatorHandler = new SpectatorHandler(this);
		gamesUtils = new GamesUtils(this);
		gameRewardsManager = new GameRewardsManager(this);
		rankBoosterManager = new RankBoosterManager(this);
		rankBoosterBarHandler = new RankBoosterBarHandler(this);
		teamsChatHandler = new TeamsChatHandler();

	}
	
	private void registerRunnables()
	{
		getApi().log("Registering runnables tasks...");
		
		new SpectatorRunnable().runTaskTimer(this, 10, 10);
		
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		getGameManager().gameTimer();
        		if(getGameManager().gs == null) {
        			Bukkit.broadcastMessage("§cInit gamestart");
        			getGameManager().gs = new GameStart(LeemonGame.getInstance());
        		}
        		if(getGameManager().getCurrentState() == GameState.WAITING)
        		{
        			getGameManager().checkForGameStart();
    				for(Player player : Bukkit.getOnlinePlayers()) {
            			if(getGameManager().gs.countingDown == true)
            			{
            				player.setLevel(getGameManager().gs.getCountdown());
            			} else {
            				player.setLevel(0);
            			}
    				}
        		}
        		
        		getApi().getServerManager().getServer().setGameState(getGameManager().getCurrentState());
        		getApi().getServerManager().getServer().setPlayingPlayers(getGameManager().getPlayingPlayers());
        		getApi().getServerManager().getServer().setMaxPlayers(getGameManager().getPlayersNeededFull());
        	}
        }.runTaskTimer(this, 0, 20);
	}
	
	@Override
	public void onDisable()
	{
		System.out.println("[LeemonGame] Plugin inactif !");
		getRankBoosterBarHandler().hideBossBarToAllPlayers();
	}
	
	/* 
	 * Getters and setters
	 * */
	
	public CoreMain getApi() {
		return api;
	}
	
	public GamesUtils getGamesUtils() {
		return gamesUtils;
	}
	
	public SpectatorHandler getSpectatorHandler() {
		return spectatorHandler;
	}
	
	public MiniGameManager getGameManager() {
		return gameManager;
	}
	
	public RankBoosterManager getRankBoosterManager() {
		return rankBoosterManager;
	}
	
	public TeamsManager getTeamsManager() {
		return teamsManager;
	}
	
	public GameRewardsManager getGameRewardsManager() {
		return gameRewardsManager;
	}
	
	public RankBoosterBarHandler getRankBoosterBarHandler() {
		return rankBoosterBarHandler;
	}

	public TeamsChatHandler getTeamsChatHandler() {
		return teamsChatHandler;
	}
	
	public static LeemonGame getInstance() {
		return instance;
	}
	
}
