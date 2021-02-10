package com.minty.leemonmc.games.boosters;

import com.minty.leemonmc.basics.core.Rank;

public class RankBooster {

	private Rank requiredRank;
	private int pulpeBooster;
	private int lemonsBooster;
	
	public RankBooster(Rank _requiredRank, int _pulpeBooster, int _lemonsBooster)
	{
		requiredRank = _requiredRank;
		pulpeBooster = _pulpeBooster;
		lemonsBooster = _lemonsBooster;
	}
	
	public Rank getRequiredRank() {
		return requiredRank;
	}
	
	public int getPulpeBooster() {
		return pulpeBooster;
	}
	
	public int getLemonsBooster() {
		return lemonsBooster;
	}
	
}
