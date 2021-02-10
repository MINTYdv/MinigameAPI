package com.minty.leemonmc.games.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LobbyPlayerJoinEvent extends Event {

	private Player player;
	
	 private static final HandlerList handlers = new HandlerList();

	 public LobbyPlayerJoinEvent(Player _play) {
		 player = _play;
	 }
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	 public Player getPlayer() {
		return player;
	}
}
