package com.minty.leemonmc.games.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.cache.Account;
import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.core.Team;

import net.minecraft.server.v1_9_R2.BiomeBase.a;

public class TeamsChatHandler implements Listener {

	private LeemonGame main = LeemonGame.getInstance();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e)
	{
		Player player = e.getPlayer();
		
		if(main.getGameManager().getCurrentState() != GameState.PLAYING) return;
		if(main.getTeamsManager().getTeam(player) == null) return;
		
		String message = e.getMessage();
		Team team = main.getTeamsManager().getTeam(player);
		String UUID = player.getUniqueId().toString();
		Account account = main.getApi().getAccountManager().getAccount(UUID);
		
		List<Player> recipients = new ArrayList<>();
		
		if(message.length() <= 1) {
			return;
		}
		
		if(!message.startsWith("!"))
		{
			e.setCancelled(true);
			recipients.addAll(team.getPlayers());
			for(Player target : Bukkit.getOnlinePlayers()) {
				Account targetAccount = main.getApi().getAccountManager().getAccount(target.getUniqueId().toString());
				if(targetAccount.isModEnabled()) {
					recipients.add(target);
				}
			}
			
			for(Player recipient : recipients)
			{
				recipient.sendMessage("§7(Chat d'équipe) " + team.getTag() + team.getName() + " " + account.getNickedName() + "§f: " + message);
			}
			return;
		}
		
		e.setCancelled(true);
		Bukkit.broadcastMessage("§7(Global) " + team.getTag() + team.getName() + " " + account.getNickedName() + "§f: §6!§f" + message.substring(1)); 
		
	}
	
}
