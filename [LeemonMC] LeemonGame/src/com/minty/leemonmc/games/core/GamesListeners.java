package com.minty.leemonmc.games.core;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.events.GuisLoadingEvent;
import com.minty.leemonmc.core.events.dataLoadedEvent;
import com.minty.leemonmc.core.util.Title;
import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.events.GameFinishEvent;
import com.minty.leemonmc.games.events.GamePlayerJoinEvent;
import com.minty.leemonmc.games.events.GamePlayerQuitEvent;
import com.minty.leemonmc.games.events.GameStartEvent;
import com.minty.leemonmc.games.events.LobbyPlayerJoinEvent;
import com.minty.leemonmc.games.gui.TeamSelectorMenu;
import com.minty.leemonmc.games.handlers.BackHubHandler;

public class GamesListeners implements Listener {

	private LeemonGame main;
	private Title title;
	
	public GamesListeners(LeemonGame _main) {
		this.main = _main;
		title = new Title();
	}
	
	@EventHandler
	public void onGameStart(GameStartEvent e)
	{
		main.getGameManager().setGameTimerCounting(true);
	}
	
	@EventHandler
	public void onGameStop(GameFinishEvent e) {
		main.getGameManager().setGameTimerCounting(false);
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e)
	{
		if(!(e.getTarget() instanceof Player)) {
			return;
		}
		
		Player player = (Player) e.getTarget();
		String UUID = player.getUniqueId().toString();
		Account account = CoreMain.getInstance().getAccountManager().getAccount(UUID);
		if(account.isModEnabled()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e)
	{
		Account account = main.getApi().getAccountManager().getAccount(e.getUniqueId().toString());
		if(account.isModEnabled() == true) return;
		
		if(main.getGameManager().getCurrentState() == GameState.WAITING) {
			if(main.getGameManager().getPlayingPlayers() == main.getGameManager().getPlayersNeededFull()) {
				e.disallow(Result.KICK_FULL, "§cLe jeu est complet !");
			}
		}
	}
	
	@EventHandler
	public void onArmorStand(PlayerArmorStandManipulateEvent e)
	{
		Player player = e.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract2(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		if(player.getGameMode() == GameMode.CREATIVE) return;
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDataLoaded(dataLoadedEvent e)
	{
		Player player = e.getPlayer();
		title = new Title();
		if(player == null) return;
		
		// add player to waiting

		Account account = main.getApi().getAccountManager().getAccount(player.getUniqueId().toString());

		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		main.getApi().getLeemonUtils().removeAllEffects(player);
		
		if(main.getGameManager().getCurrentState() != GameState.WAITING)
		{
			main.getSpectatorHandler().leaveSpectator(player);
			main.getSpectatorHandler().setSpectator(player);
			return;
		}
		
		if(main.getGameManager().getCurrentState() == GameState.WAITING && !account.isModEnabled())
		{
			main.getGameManager().addPlayingPlayer(player);
			Bukkit.broadcastMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» " + main.getApi().getPlayerDisplayNameChat(player) + " §7a rejoint la partie ! §a(" + main.getGameManager().getPlayingPlayers() + "/" + main.getGameManager().getPlayersNeededFull() + ")");
		
			for(Player pls : Bukkit.getOnlinePlayers())
			{
    			title.sendActionBar(pls, main.getApi().getPlayerDisplayNameChat(player) + " §7a rejoint la partie ! §a(" + main.getGameManager().getPlayingPlayers() + "/" + main.getGameManager().getPlayersNeededFull() + ")");
			}
		}
		
		if(!account.isModEnabled())
		{
			Bukkit.getPluginManager().callEvent(new GamePlayerJoinEvent(player));
		}
		
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{		
			main.getSpectatorHandler().leaveSpectator(player);
			// Teleport player to the lobby's location
			double x = main.getConfig().getDouble("locations.lobby.x");
			double y = main.getConfig().getDouble("locations.lobby.y");
			double z = main.getConfig().getDouble("locations.lobby.z");
			Location lobbyLoc = new Location(player.getWorld(), x, y, z);
			player.teleport(lobbyLoc);
			
			if(!account.isModEnabled())
			{
				//Set player's gamemode
				player.setGameMode(GameMode.ADVENTURE);
				
				//Set player's health
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(20);
				
				// set player's exp
				player.setExp(0);
				player.setLevel(0);
				
				// set player's hotbar
				player.getInventory().clear();
				
				player.getInventory().setItem(0, main.getApi().getGuiUtils().createItem(Material.WOOL, "§6Choix de l'équipe §7§o(Clic droit)", (byte) 0));
				player.getInventory().setItem(8, main.getApi().getLeemonUtils().getLobbyItem());
				Bukkit.getPluginManager().callEvent(new LobbyPlayerJoinEvent(player));
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		e.setJoinMessage("");
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			e.setDeathMessage("");
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e)
	{
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e)
	{
		if(e.getDamager() instanceof Player) {
			if(e.getEntity() instanceof Player)
			{
				Player damager = (Player) e.getDamager();
				Player receiver = (Player) e.getEntity();
				
				if(damager == null) return;
				if(receiver == null) return;
				
		
				if(main.getTeamsManager().getTeam(damager) == main.getTeamsManager().getTeam(receiver) && main.getGameManager().getCurrentState() == GameState.PLAYING)
				{
					e.setCancelled(true);
					title.sendActionBar(damager, "§c✖ Tu ne peux pas attaquer tes coéquipiers ✖");
				}
			}
		}
		
	}
	
	@EventHandler
	public void OnGuisLoading(GuisLoadingEvent e)
	{
		e.getMain().getGuiManager().addMenu(new TeamSelectorMenu(main.getApi().getGuiUtils(), main));
	}
	
	// Block weather changing in the game
	@EventHandler
	public void OnWeatherChange(WeatherChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Action action = e.getAction();
		if(action == null) return;
		if(player == null) return;
		ItemStack it = e.getItem();
		if(it == null) return;

		
		if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK && player.getGameMode() == GameMode.ADVENTURE)
		{
			switch(it.getType())
			{
				case WOOL:
					main.getApi().getGuiManager().open(player, TeamSelectorMenu.class);
					break;
				case BED:
					BackHubHandler.hubClicked(player);
					break;
				default:
					break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	// Block item drop
	public void onDrop(PlayerDropItemEvent e)
	{
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			e.setCancelled(true);
		}
	}
	
	// Block hunger in the hub
	@EventHandler
	public void OnHungerLoss(FoodLevelChangeEvent e)
	{
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onGameEnd(GameFinishEvent e) {
		main.getGameManager().endGame(e.getWinnerTeam());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.getTo().getY() <= 10 && main.getGameManager().getCurrentState() == GameState.WAITING) {
			Player player = e.getPlayer();
			double x = main.getConfig().getDouble("locations.lobby.x");
			double y = main.getConfig().getDouble("locations.lobby.y");
			double z = main.getConfig().getDouble("locations.lobby.z");
			Location lobbyLoc = new Location(player.getWorld(), x, y, z);
			player.teleport(lobbyLoc);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		Account account = main.getApi().getAccountManager().getAccount(player.getUniqueId().toString());
		
		if(account.isModEnabled()) return;
		
		Bukkit.getPluginManager().callEvent(new GamePlayerQuitEvent(player));
		player.setWalkSpeed(0.2f);
		player.setFlySpeed(0.1f);
		
		if(main.getSpectatorHandler().isSpectator(player))
		{
			e.setQuitMessage("");
			return;
		}
		
		if(main.getGameManager().getCurrentState() == GameState.WAITING)
		{
			main.getGameManager().removePlayingPlayer(player);
			e.setQuitMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» " + main.getApi().getPlayerDisplayNameChat(player) + " §ca quitté la partie ! §c(" + main.getGameManager().getPlayingPlayers() + "/" + main .getGameManager().getPlayersNeededFull() + ")");
		
			for(Player pls : Bukkit.getOnlinePlayers())
			{
    			title.sendActionBar(pls, main.getApi().getPlayerDisplayNameChat(player) + " §ca quitté la partie ! §c(" + main.getGameManager().getPlayingPlayers() + "/" + main .getGameManager().getPlayersNeededFull() + ")");
			}
			
		} else if(main.getGameManager().getCurrentState() == GameState.PLAYING)
		{
			e.setQuitMessage(player.getDisplayName() + " §7a quitté la partie.");
			main.getGameManager().checkForTeamEmpty(); 
		}

	}
	
}
