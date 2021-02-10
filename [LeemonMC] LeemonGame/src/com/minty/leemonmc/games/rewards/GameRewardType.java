package com.minty.leemonmc.games.rewards;

public enum GameRewardType {

	KILLS("Kills"),
	VICTORY("Victoire"),
	DEFEAT("D�faite");
	
	private String name;
	
	GameRewardType(String _name)
	{
		name = _name;
	}
	
	public String getName() {
		return name;
	}
	
}
