package com.minty.leemonmc.games.rewards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.minty.leemonmc.core.util.Title;

public class PlayerRewards
{

	private List<GameReward> gameRewards = new ArrayList<>();
	private List<GameRewardExploit> gameExploits = new ArrayList<>();
	private Player owner;
	private Title title;
	
	public PlayerRewards(Player _owner) {
		owner = _owner;
		title = new Title();
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public List<GameReward> getGameRewards() {
		return gameRewards;
	}
	
	public void addReward(GameReward _reward) {
		getGameRewards().add(_reward);
	}
	
	public void addExploit(GameRewardExploit _expl)
	{
		getGameExploits().add(_expl);
	}
	
	public List<GameRewardExploit> getGameExploits() {
		return gameExploits;
	}
	
}
