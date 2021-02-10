package com.minty.leemonmc.games.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.minty.leemonmc.core.util.GuiUtils;
import com.minty.leemonmc.games.LeemonGame;

public class Team {

	private String name;
	private String tag;
	private byte woolData;
	private Location spawn;
	private List<Player> players = new ArrayList<>();
	private int maxPlayers;
	private double points = 0;
	private List<Player> alivePlayers = new ArrayList<>();
	private boolean eliminated;
	
	private Color armorColor;
	private LeemonGame main;
	
	public Team(LeemonGame _main, String _name, String _tag, byte _woolData, World _world, double _x, double _y, double _z, float _yaw, float _pitch, int _maxPlayers, Color _armorColor)
	{
		main = _main;
		this.armorColor = _armorColor;
		eliminated = false;
		this.name = _name;
		this.tag = _tag;
		this.woolData = _woolData;
		this.spawn = new Location(_world, _x, _y, _z, _yaw, _pitch);
		this.maxPlayers = _maxPlayers;
	}
	
	public void eliminate() {
		eliminated = true;
	}
	
	public void revive() {
		eliminated = false;
		alivePlayers.clear();
		for(Player p : players)
		{
			alivePlayers.add(p);
		}
	}
	
	public boolean isEmpty() {
		if(getPlayers().size() <= 0) return true;
		return false;
	}
	
	public boolean isAlive()
	{
		if(eliminated == true) return false;
		return true;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public void removePlayer(Player player)
	{
		if(players.contains(player))
		{
			players.remove(player);
		} else {
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §cVous n'êtes pas dans cette équipe !");
		}
	}
	
	public void addPlayer(Player player)
	{
		if(isFull())
		{
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §cCette équipe est complète !");
			return;
		} else
		{
			players.add(player);
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f§l» §7Vous avez correctement rejoint l'équipe " + tag + name + "§7!");
			resetAlivePlayers();
		}
	}
	
	public Color getArmorColor()
	{
		return armorColor;
	}
	
	public ItemStack getColoredArmor(Material mat)
	{
		if(mat != Material.LEATHER_BOOTS && mat != Material.LEATHER_HELMET && mat != Material.LEATHER_CHESTPLATE && mat != Material.LEATHER_LEGGINGS) return new ItemStack(Material.BARRIER, 1);
		ItemStack it = new ItemStack(mat, 1, (byte) 0);
		
		LeatherArmorMeta meta = (LeatherArmorMeta) it.getItemMeta();
		
		meta.setColor(armorColor);
		meta.spigot().setUnbreakable(true);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		
		it.setItemMeta(meta);
		
		return it;
	}
	
	public void addPoint(){
		points++;
	}
	
	public double getPoints() {
		return points;
	}
	
	public void addPoints(int i){
		points = points + i;
	}
	
	public void removePoint() {
		points--;
	}
	public void setPoints(int i){
		points = i;
	}
	
	public ItemStack getIcon(GuiUtils utils)
	{
		return utils.createItem(Material.WOOL, "§6Équipe " + tag + name, woolData);
	}

	public void resetAlivePlayers()
	{
		if(getPlayers().size() <= 0) return;
		
		alivePlayers.clear();
		for(Player players : getPlayers())
		{
			alivePlayers.add(players);
		}
	}
	
	public List<Player> getAlivePlayers()
	{
		return alivePlayers;
	}
	
	public void eliminatePlayer(Player player)
	{
		if(!getAlivePlayers().contains(player)) return;
		getAlivePlayers().remove(player);
	}
	
	public boolean isFull() {
		if(players.size() >= maxPlayers) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getTag() {
		return tag;
	}
	
	public byte getWoolData() {
		return woolData;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
}
