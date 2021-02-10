package com.minty.leemonmc.games.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.core.CoreMain;

public class GamePlayerJoinEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private Player player;
	 
	 public GamePlayerJoinEvent(Player player)
	 {
		 this.player = player;
	 }
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	 public Player getPlayer()
	 {
		 return player;
	 }
	
}
