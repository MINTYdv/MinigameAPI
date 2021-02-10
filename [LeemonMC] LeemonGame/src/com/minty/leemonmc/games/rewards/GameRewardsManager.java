package com.minty.leemonmc.games.rewards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.Title;
import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.core.Team;
import com.minty.leemonmc.games.events.GameFinishEvent;

public class GameRewardsManager implements Listener {

	private LeemonGame main;
	
	private Title title;
	private Map<Player, PlayerRewards> playerRewards = new HashMap<>();
	
	public GameRewardsManager(LeemonGame _main)
	{
		title = new Title();
		main = _main;
	}
	
	public Map<Player, PlayerRewards> getPlayerRewards()
	{
		return playerRewards;
	}
	
	public PlayerRewards getPlayerRewards(Player player)
	{
		return getPlayerRewards().get(player);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		if(!getPlayerRewards().containsKey(player))
		{
			getPlayerRewards().put(player, new PlayerRewards(player));
		}
	}
	
	@EventHandler
	public void onGameEnd(GameFinishEvent e)
	{
		Team winner = e.getWinnerTeam();
		List<Player> playingPlayers = main.getGameManager().getPlayingPlayersList();
		
		
		for(Player player : playingPlayers)
		{
			String UUID = player.getUniqueId().toString();
			if(main.getTeamsManager().getTeam(player) == winner)
			{
				getPlayerRewards(player).getGameRewards().add(new GameReward(GameRewardType.VICTORY, 20, 0));
			} else
			{
				getPlayerRewards(player).getGameRewards().add(new GameReward(GameRewardType.DEFEAT, 3, 0));
			}
			
			player.sendMessage("§8§m---------------------------------------");
			player.sendMessage("");
			player.sendMessage("§r               " + main.getGameManager().getMinigameName().toUpperCase() + " §8• §e§lRécapitulatif");
			player.sendMessage("");
			player.sendMessage("§7Victoire de l'équipe " + winner.getTag() + winner.getName() + " §e!");
			player.sendMessage("");

			Map<GameRewardType, List<GameReward>> rewardsMap = new HashMap<>();
			
			for(GameRewardType type : GameRewardType.values())
			{

				List<GameReward> rewardsForType = new ArrayList<>();
				
				for(GameReward reward : getPlayerRewards(player).getGameRewards())
				{
					if(reward.getType() == type)
					{
						rewardsForType.add(reward);
					}
				}
				
				if(rewardsMap.containsKey(type)) {
					rewardsMap.remove(type);
				}
				
				rewardsMap.put(type, rewardsForType);

			}
			
			int bigTotal = 0;
			
			int pulpeTotal = 0;
			int lemonsTotal = 0;
			
			for(Entry<GameRewardType, List<GameReward>> entry : rewardsMap.entrySet())
			{
				for(GameReward reward : entry.getValue())
				{
					pulpeTotal += main.getRankBoosterManager().getBoostedPulpeResult(reward.getPulpe());
					lemonsTotal += main.getRankBoosterManager().getBoostedLemonsResult(reward.getLemons());
				}
				
				if(pulpeTotal + lemonsTotal != 0)
				{
					if(!main.getRankBoosterManager().isBoosted())
					{
						player.sendMessage("§6§l➜ " + entry.getKey().getName() + "§7: §e+" + pulpeTotal + " pulpe §7- §6+" + lemonsTotal + " citrons");
					} else {
						player.sendMessage("§6§l➜ " + entry.getKey().getName() + "§7: §e+" + pulpeTotal + " pulpe " + "§f(+" + main.getRankBoosterManager().getTotalPulpeBooster() + "%) §7- §6+" + lemonsTotal + " citrons §f(+" + main.getRankBoosterManager().getTotalLemonsBooster() + "%)");
					}
					
					CoreMain.getInstance().getAccountManager().getAccount(UUID).addLemons(lemonsTotal);
					CoreMain.getInstance().getAccountManager().getAccount(UUID).addPulpe(pulpeTotal);
					bigTotal += pulpeTotal;
				}
			}
			
			player.sendMessage("");
			player.sendMessage("§8§m---------------------------------------");
			
			final int tt = bigTotal;
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			    @Override
			    public void run()
			    {
			        new BukkitRunnable() {
						int a = 0;
			        	@Override
			        	public void run()
			        	{
			        		if(a < tt)
			        		{
			        			a++;
			        			player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
			        			title.sendTitle(player, 0, 20, 0, "", "§e+" + a + " pulpe");
			        		} else {
			        			title.sendTitle(player, 0, 20, 0, "", "§6§l+" + tt + " pulpe");
			        			title.sendActionBar(player, "§6Pour rejouer faites §e/re");
			        		}
			        		
			        		if(a == tt) {
			        			
			        		}
			        	}
			        }.runTaskTimer(main, 0, 1);
			        
			    }
			}, 65L);
			

			
		}
		
	}
	
}
