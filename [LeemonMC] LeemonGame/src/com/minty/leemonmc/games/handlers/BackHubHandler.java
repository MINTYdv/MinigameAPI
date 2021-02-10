package com.minty.leemonmc.games.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.games.LeemonGame;

public class BackHubHandler {

	private static Map<Player, Boolean> hubConfirmations = new HashMap<>();
	
	public static void hubClicked(Player player)
	{
		if(!getHubConfirmations().containsKey(player)) {
			getHubConfirmations().put(player, true);
		}
		
		if(getHubConfirmations().get(player) == true) {
			getHubConfirmations().remove(player);
			getHubConfirmations().put(player, false);
			
			player.sendMessage("§6§lLeemonMC §f» §7Êtes vous sur de vouloir §cretourner au hub §7? Cliquez §6à nouveau §7pour confirmer !");
			return;
		}
		
		if(getHubConfirmations().get(player) == false)
		{
			if(LeemonGame.getInstance().getGameManager().isGameTimerCounting()) return;
			
			MiniGameManager.hubItem(player);
		}
	}
	
	public static Map<Player, Boolean> getHubConfirmations() {
		return hubConfirmations;
	}
	
}
