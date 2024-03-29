package net.obmc.OBNametagHide;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommandListener implements CommandExecutor {

	static Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// for now only op can use the command
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "Sorry, command is reserved for server operators.");
			return true;
		}

		// usage if no arguments passed
		if (args.length == 0) {
			Usage(sender);
			return true;
		}

		// create a list of worlds from the arguments list passed in
		ArrayList<String> worlds = new ArrayList<String>();
		for (int i = 1; i < args.length; i++) {
			worlds.add(args[i]);
		}
		if (worlds.size() == 0) {
			worlds.add(Bukkit.getServer().getPlayer(sender.getName()).getWorld().getName());
		}
		
		// process the command and any arguments
		if (command.getName().equalsIgnoreCase("nametaghide") || command.getName().equalsIgnoreCase("nth")) {
			switch (args[0].toLowerCase()) {
			
				// enable hiding for a world - has to be a valid world in the server and not already be set for hiding of course
				case "on":
					for (String world : worlds) {
						if (OBNametagHide.getInstance().checkWorldExists(world)) {
							if (OBNametagHide.getInstance().addWorld(world)) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Hiding player nametags in " + world);
							} else {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Nametag hiding already on for " + world);
							}
						} else {
							sender.sendMessage(ChatColor.RED + world + " is not a valid world.");
						}
					}
					UpdatePlayers(sender);
					break;
				// disable hiding for a world - has to be a valid world in the server
				case "off":
					for (String world : worlds) {
						if (OBNametagHide.getInstance().checkWorldExists(world)) {
							if (OBNametagHide.getInstance().delWorld(world)) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Player nametags now visible in " + world);
							} else {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Nametag hiding isn't on for " + world);
							}
						} else {
							sender.sendMessage(ChatColor.RED + world + " is not a valid world.");
						}
					}
                    UpdatePlayers(sender);
                    break;
                // remove a world from the working list directly - useful for worls that were set but no longer exist
				case "remove":
					for (String world : worlds) {
						if (OBNametagHide.getInstance().checkWorld(world)) {
							if (OBNametagHide.getInstance().delWorld(world)) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "Removed " + world + "from configuration");
							} else {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + world + " not registered for nametag hiding");
							}
						}
						
					}
					UpdatePlayers(sender);
					break;
				// list up worlds current set for nametag hiding 
				case "list":
					if (args.length == 1 || (worlds.contains("on") && args.length > 1)) {
						if (OBNametagHide.getInstance().hideIn.size() == 0) {
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "Nametag hiding is not enabled on any worlds");
						} else {
							sender.sendMessage(ChatColor.LIGHT_PURPLE + "Enabled for the following worlds:");
							for (String world : OBNametagHide.getInstance().hideIn) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "    " + world);
							}
						}
					} else if (worlds.contains("worlds") && args.length > 1) {
						sender.sendMessage(ChatColor.LIGHT_PURPLE + "Worlds on this server:");
						for (World w : Bukkit.getWorlds()) {
							if (OBNametagHide.getInstance().hideIn.contains(w.getName())) {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "   *" + w.getName());
							} else {
								sender.sendMessage(ChatColor.LIGHT_PURPLE + "    " + w.getName());
							}
						}
					}
			        break;
				default:
					Usage(sender);
					break;
                }
                return true;
            } else {
            	Usage(sender);
            	return true;
            }
	}

	// update new state for players
	void UpdatePlayers(CommandSender sender) {
		for (Player p : sender.getServer().getOnlinePlayers()) {
        	OBNametagHide.getInstance().checkPlayer(p);
        }
	}
	
    void Usage(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide" + ChatColor.GOLD + " - Display this menu");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide on  <worldname> ..." + ChatColor.GOLD + " - Switch on  for worlds (space separated)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide off <worldname> ..." + ChatColor.GOLD + " - Switch off for worlds (space separated)");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide list [on]" + ChatColor.GOLD + " - List worlds currently hiding player nametags");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide list worlds" + ChatColor.GOLD + " - List worlds in the server");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/nametaghide remove" + ChatColor.GOLD + " - remove world from config directly");
    }
}
