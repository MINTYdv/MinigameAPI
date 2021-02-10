package com.minty.leemonmc.games.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.core.Team;
import com.minty.leemonmc.games.util.ScoreboardTeam;

public class TeamsManager {

	private LeemonGame main;
	
	private Map<Player, Team> playersTeams = new HashMap<>();
	private List<Team> teams = new ArrayList<>();
	private Map<Team, ScoreboardTeam> scoreboardTeams = new HashMap<>();
	private List<ScoreboardTeam> scoreboardTeamsList = new ArrayList<>();
	
	public TeamsManager(LeemonGame main) {
		this.main = main;
		scoreboardTeamsList = new ArrayList<>();
		scoreboardTeams = new HashMap<>();
		teams = new ArrayList<>();
	}
	
	public void setup()
	{
		ConfigurationSection section = main.getConfig().getConfigurationSection("teams");
		for(String team : section.getKeys(false))
		{
			String name = main.getConfig().getString("teams." + team + ".name");
			String tag = main.getConfig().getString("teams." + team + ".tag");
			byte woolData = (byte) main.getConfig().getInt("teams." + team + ".woolData");
			
			int x = main.getConfig().getInt("teams." + team + ".spawn.x");
			int y = main.getConfig().getInt("teams." + team + ".spawn.y");
			int z = main.getConfig().getInt("teams." + team + ".spawn.z");
			float yaw = (float) main.getConfig().getDouble("teams." + team + ".yaw");
			float pitch = (float) main.getConfig().getDouble("teams." + team + ".pitch");
			
			Color armorColor = main.getApi().getLeemonUtils().stringToColor(main.getConfig().getString("teams." + team + ".armorColor"));
			
			System.out.println("§cYaw: " + yaw + " Pitch: " + pitch);
			Team newTeam = new Team(main, name, tag, woolData, Bukkit.getWorld("world"), x, y, z, yaw, pitch, main.getGameManager().getPlayersPerTeam(), armorColor);
			teams.add(newTeam);
		}
		System.out.println(teams.size() + " équipes ont été chargées !");
		createScoreboardTeams();
	}
	
	public Team getTeamByColor(Color color) {
		for(Team team : getTeams())
		{
			if(team.getArmorColor() == color) {
				return team;
			}
		}
		return null;
	}
	
	private void createScoreboardTeams()
	{
		
		for(int i = 0; i < teams.size(); i++)
		{
			Team t = teams.get(i);
			
			String teamName = String.valueOf(i);
			
			ScoreboardTeam sbTeam = new ScoreboardTeam(teamName, t.getTag() + "[" + t.getName() + "] ");

			scoreboardTeams.put(t, sbTeam);
			
			scoreboardTeamsList.add(sbTeam);
		}
	}
	
	public List<ScoreboardTeam> getScoreboardTeams()
	{
		return scoreboardTeamsList;
	}
	
	public ScoreboardTeam teamToSbTeam(Team team)
	{
		return scoreboardTeams.get(team);
	}
	
	@SuppressWarnings("unused")
	public Team getRandomTeam(List<Team> givenList) {
	    Random rand = new Random();
	 
	    int numberOfElements = 2;
	 
	    for (int i = 0; i < numberOfElements; i++) {
	        int randomIndex = rand.nextInt(givenList.size());
	        Team randomElement = givenList.get(randomIndex);
	        return randomElement;
	    }
	    return null;
	}
	
	public List<Team> getAliveTeams() {
		List<Team> result = new ArrayList<>();
		for(Team team : teams) {
			if(team.isAlive() == true) {
				result.add(team);
			}
		}
		return result;
	}
	
	public Team getAvailableTeam()
	{
		List<Team> availableTeams = new ArrayList<>();
		for(Team team : teams)
		{
			if(!team.isFull())
			{
				availableTeams.add(team);
			}
		}
		return getRandomTeam(availableTeams);
	}
	
	public Team getTeam(Player player)
	{
		return playersTeams.get(player);
	}
	
	public void removePlayerFromTeam(Player player, Team team)
	{
		if(team.getPlayers().contains(player))
		{
			team.removePlayer(player);
			playersTeams.remove(player);
		}
	}
	
	public void addPlayerToTeam(Player player, Team team)
	{
		if(team.getPlayers().contains(player))
		{
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §7Vous êtes déjà dans l'équipe " + team.getTag() + team.getName() + " §7!");
			return;
		}
		
		if(team.isFull()) {
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §cCette équipe est complète !");
			return;
		}
		
		if(getTeam(player) != null) {
			removePlayerFromTeam(player, getTeam(player));
		}
		
		team.addPlayer(player);
		playersTeams.put(player, team);
		player.getInventory().setItem(0,main.getApi().getGuiUtils().createItem(Material.WOOL, "§6Choix de l'équipe §7§o(Clic droit)", team.getWoolData()));
		
	}
	
	public void randomTeam(Player player) 
	{
		if(getTeam(player) != null) {
			removePlayerFromTeam(player, getTeam(player));
		}
		if(playersTeams.containsKey(player)) {
			playersTeams.remove(player);
		}
		player.getInventory().setItem(0,main.getApi().getGuiUtils().createItem(Material.WOOL, "§6Choix de l'équipe §7§o(Clic droit)", (byte) 0));
	}
	
	public Map<Player, Team> getPlayersTeams() {
		return playersTeams;
	}
	
	public List<Team> getTeams() {
		return teams;
	}
}
