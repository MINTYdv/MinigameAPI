package com.minty.leemonmc.games.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minty.leemonmc.games.core.Team;

public class GameFinishEvent extends Event {

	 private static final HandlerList handlers = new HandlerList();
	 
	 private Team winner;
	 
	 public GameFinishEvent(Team winner)
	 {
		 this.winner = winner;
	 }
	 
	 public HandlerList getHandlers()
	 {
	    return handlers; 
	 } 
	 
	 public static HandlerList getHandlerList()
	 { 
		return handlers;
	 }
	
	 public Team getWinnerTeam()
	 {
		 return winner;
	 }
	
}
