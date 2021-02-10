package com.minty.leemonmc.games.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.minty.leemonmc.core.CoreMain;
import com.minty.leemonmc.core.util.GuiBuilder;
import com.minty.leemonmc.core.util.GuiUtils;
import com.minty.leemonmc.games.LeemonGame;
import com.minty.leemonmc.games.core.Team;

public class TeamSelectorMenu implements GuiBuilder {

	private GuiUtils utils;
	private LeemonGame main;
	
	public TeamSelectorMenu(GuiUtils _utils, LeemonGame main) {
		this.utils = _utils;
		this.main = main;
	}
	
	@Override
	public void contents(Player player, Inventory inv) {

		List<Team> teams = main.getTeamsManager().getTeams();
		for(int i = 0; i < teams.size(); i++)
		{
			
			List<String> lore = new ArrayList<>();
			Team team = teams.get(i);
			
			if(team.getPlayers().size() <= 0)
			{
				lore.add("§7Aucun joueur dans cette équipe.");
			} else
			{
				for(Player p : team.getPlayers()) {
					lore.add("§8- §f" + p.getName());
				}
			}
			lore.add("");
			if(main.getTeamsManager().getTeam(player) == team)
			{
				lore.add("§4» §cC'est votre équipe !");
			} else {
				if(team.getPlayers().size() == main.getGameManager().getPlayersPerTeam())
				{
					lore.add("§4» §cCette équipe est complète !");
				} else {
					lore.add("§6» §eCliquez pour rejoindre");
				}
			}

			inv.setItem(i, utils.createItem(Material.WOOL, "§6Équipe " + team.getTag() + team.getName() + " §7(" + team.getPlayers().size() + "/" + main.getGameManager().getPlayersPerTeam() + "§7)", team.getWoolData(), lore));
		}
		
		inv.setItem(inv.getSize() - 1, utils.cancelItem());
		inv.setItem(inv.getSize() - 2, randomTeamItem(player));
	}

	@Override
	public int getSize() {
		return 9;
	}

	private ItemStack randomTeamItem(Player player) {
		ItemStack it = utils.randomItem();
		ItemMeta meta = it.getItemMeta();
		
		meta.setDisplayName("§6Aléatoire");
		
		List<String> lore = new ArrayList<>();
		lore.add("");
		if(main.getTeamsManager().getTeam(player) == null)
		{
			lore.add("§4» §cVous êtes déjà dans une équipe aléatoire !");
			meta.setLore(lore);
			it.setItemMeta(meta);
			return main.getApi().getLeemonUtils().addGlow(it);
			
		} else {
			lore.add("§6» §eCliquez pour choisir une équipe aléatoire !");
			meta.setLore(lore);
			it.setItemMeta(meta);
			return it;
		}
	}
	
	@Override
	public String name() {
		return "§6Choisir une équipe";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(Player player, Inventory inv, ItemStack it, int slot) {
		
		if(it == null) return;
		
		List<Team> teams = main.getTeamsManager().getTeams();
		for(int i = 0; i < teams.size(); i++)
		{
			Team team = teams.get(i);
			if(it.getType() == team.getIcon(utils).getType() && it.getData().getData() == team.getWoolData())
			{
				main.getTeamsManager().addPlayerToTeam(player, team);
				player.closeInventory();
			}
		}
		
		switch(it.getType()) {
			case BARRIER:
				player.closeInventory();
				break;
			case SKULL_ITEM:
				if(main.getTeamsManager().getTeam(player) == null) {
					break;
				} else {
					player.closeInventory();
					main.getTeamsManager().randomTeam(player);
				}
				break;
			default:
				break;
		}
		
	}

	@Override
	public void onRightClick(Player arg0, Inventory arg1, ItemStack arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	
	
}
