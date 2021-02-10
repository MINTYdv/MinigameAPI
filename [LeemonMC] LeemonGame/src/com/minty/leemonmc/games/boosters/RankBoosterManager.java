package com.minty.leemonmc.games.boosters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.Rank;
import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.games.LeemonGame;

public class RankBoosterManager
{

	private Map<Rank, RankBooster> ranksBoosters = new HashMap<>();
	private List<Player> boostersPlayers = new ArrayList<>();
	
	private LeemonGame main;
	private int totalPulpeBooster = 0;
	private int totalLemonsBooster = 0;
	private boolean isBoosted = false;;
	private int id;
	
	public RankBoosterManager(LeemonGame _main)
	{
		main = _main;
		isBoosted = false;
		setup();
	}
	
	private void setup()
	{
		register(new RankBooster(Rank.VIP, 20, 0));
		register(new RankBooster(Rank.VIP_PLUS, 50, 0));
		register(new RankBooster(Rank.LEMON, 100, 0));
		register(new RankBooster(Rank.CUSTOM, 110, 0));
		register(new RankBooster(Rank.HELPER, 110, 0));
		register(new RankBooster(Rank.MOD, 110, 0));
		register(new RankBooster(Rank.SUPER_MOD, 110, 0));
		register(new RankBooster(Rank.DEVELOPER, 110, 0));
		register(new RankBooster(Rank.RESPONSABLE, 110, 0));
		register(new RankBooster(Rank.ADMIN, 150, 0));
		
		id = 0;
        new BukkitRunnable() {
        	@Override
        	public void run()
        	{
        		for(@SuppressWarnings("unused") Player online : Bukkit.getOnlinePlayers())
        		{
            		updateTotalBooster();
        		}
        	}
        }.runTaskTimer(main, 0, 20);
	}
	
	public String getBoosterBarMessage()
	{
		List<String> results = new ArrayList<>();
		String result1 = "";
		String result2 = "";
		if(getBoostersPlayers().size() >= 1)
		{
			result1 += "§f✦ §eJeu boosté par ";
			for(int i = 0; i < getBoostersPlayers().size(); i++)
			{
				
				Player player = getBoostersPlayers().get(i);
				
				if(i == getBoostersPlayers().size() - 1)
				{
					result1 += main.getApi().getPlayerDisplayNameChat(player) + " §f✦"; 
					result2 += "§f✦ §eVous obtiendrez en fin de partie §e+" + getTotalPulpeBooster() + "% pulpe §f& §6+" + getTotalLemonsBooster() + "% citrons §f✦";
				} else
				{
					result1 += main.getApi().getPlayerDisplayNameChat(player) + "§7, ";
				}
			}
		}
		
		id++;

		results.add(result1);
		results.add(result2);
		
		if(id >= results.size()) {
			id = 0;
		}
		
		return results.get(id);
	}
	
	public String getBoosterMessage()
	{
		String result = "";
		if(getBoostersPlayers().size() >= 1)
		{
			result += "§eJeu boosté par ";
			for(int i = 0; i < getBoostersPlayers().size(); i++)
			{

				Player player = getBoostersPlayers().get(i);
				
				if(i == getBoostersPlayers().size() - 1)
				{
					result += main.getApi().getPlayerDisplayNameChat(player) + "§8 - §e+" + getTotalPulpeBooster() + "% pulpe §6+" + getTotalLemonsBooster() + "% citrons"; 
				} else
				{
					result += main.getApi().getPlayerDisplayNameChat(player) + "§f, ";
				}
			}
		}
		return result;
	}
	
	public void updateTotalBooster()
	{

		if(main.getGameManager().getCurrentState() != GameState.WAITING) return;
		
		getBoostersPlayers().clear();
		totalPulpeBooster = 0;
		totalLemonsBooster = 0;
		
		for(Player player : main.getGameManager().getPlayingPlayersList())
		{
			String UUID = player.getUniqueId().toString();
			Rank rank = CoreMain.getInstance().getAccountManager().getAccount(UUID).getRank();
			
			for(Entry<Rank, RankBooster> entry : getRanksBoosters().entrySet())
			{
				if(entry.getKey() == rank)
				{
					getBoostersPlayers().add(player);
					isBoosted = true;
					totalPulpeBooster += entry.getValue().getPulpeBooster();
					totalLemonsBooster += entry.getValue().getLemonsBooster();
				}
			}
		}
		
		if(totalPulpeBooster > 200) {
			totalPulpeBooster = 200;
		}
		if(totalLemonsBooster > 100) {
			totalLemonsBooster = 100;
		}
		
		if(getBoostersPlayers().size() <= 0) {
			isBoosted = false;
		}
	}
	
	public boolean isBoosted() {
		return isBoosted;
	}
	
	public int getBoostedLemonsResult(int lemons)
	{
		int result = lemons;
		result *= getTotalLemonsBooster();
		result /= 100;
		result += lemons;
		return result;
	}
	
	public int getBoostedPulpeResult(int pulpe)
	{
		int result = pulpe;
		result *= getTotalPulpeBooster();
		result /= 100;
		result += pulpe;
		return result;
	}
	
	private void register(RankBooster rankBooster) 
	{
		System.out.println("Registering rank booster for rank " + rankBooster.getRequiredRank().toString() + "...");
		
		if(getRanksBoosters().containsKey(rankBooster.getRequiredRank()))
		{
			getRanksBoosters().remove(rankBooster.getRequiredRank());
		}
		
		getRanksBoosters().put(rankBooster.getRequiredRank(), rankBooster);
	}
	
	public List<Player> getBoostersPlayers() {
		return boostersPlayers;
	}
	
	public int getTotalPulpeBooster() {
		return totalPulpeBooster;
	}
	
	public int getTotalLemonsBooster() {
		return totalLemonsBooster;
	}
	
	public Map<Rank, RankBooster> getRanksBoosters() {
		return ranksBoosters;
	}
	
}
