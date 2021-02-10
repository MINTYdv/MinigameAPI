package com.minty.leemonmc.games.rewards;

public class GameReward
{

	private int pulpe;
	private int lemons;
	private GameRewardType type;
	
	public GameReward(GameRewardType _type, int _pulpe, int _lemons)
	{
		type = _type;
		pulpe = _pulpe;
		lemons = _lemons;
	}
	
	public GameRewardType getType() {
		return type;
	}
	
	public int getPulpe() {
		return pulpe;
	}
	
	public int getLemons() {
		return lemons;
	}
	
}
