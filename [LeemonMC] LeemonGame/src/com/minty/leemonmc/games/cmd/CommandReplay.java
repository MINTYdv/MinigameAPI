package com.minty.leemonmc.games.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minty.leemonmc.basics.core.GameState;
import com.minty.leemonmc.basics.core.ServerType;
import com.minty.leemonmc.games.LeemonGame;

public class CommandReplay implements CommandExecutor {

	private LeemonGame main = LeemonGame.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args)
	{
		if(!(sender instanceof Player)) {
			sender.sendMessage("§cErreur: Cette commande peut uniquement être exécutée par un joueur !");
			return false;
		}
		Player player = (Player) sender;
		
		if(main.getApi().getServerManager().getServerType() != ServerType.MINIGAME)
		{
			player.sendMessage("§cCette commande n'a pas pu être trouvée.");
			return false;
		}
		
		if(main.getGameManager().getCurrentState() != GameState.FINISH)
		{
			player.sendMessage("§6§l" + main.getGameManager().getMinigameName() + " §f» §cCette commande est exécutable uniquement en fin de partie !");
			return false;
		}
		
		main.getApi().getQueueManager().queue(player, main.getApi().getServerManager().getServer().getServerGroup());
		return false;
	}
	
	
	
}
