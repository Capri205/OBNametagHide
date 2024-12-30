package net.obmc.OBNametagHide;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerCommandListener implements CommandExecutor {

	static Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// for now only op can use the command
		if (!sender.isOp()) {
			sender.sendMessage(Component.text("Sorry, command is reserved for server operators.", NamedTextColor.RED));
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
								sender.sendMessage(Component.text("Hiding player nametags in world '" + world + "'", NamedTextColor.LIGHT_PURPLE));
							} else {
								sender.sendMessage(Component.text("Nametag hiding already on for world '" + world + "'", NamedTextColor.LIGHT_PURPLE));
							}
						} else {
							sender.sendMessage(Component.text("world '" + world + "' is not a valid world.", NamedTextColor.RED));
						}
					}
					UpdatePlayers(sender);
					break;
				// disable hiding for a world - has to be a valid world in the server
				case "off":
					for (String world : worlds) {
						if (OBNametagHide.getInstance().checkWorldExists(world)) {
							if (OBNametagHide.getInstance().delWorld(world)) {
								sender.sendMessage(Component.text("Player nametags now visible in world '" + world +"'", NamedTextColor.LIGHT_PURPLE));
							} else {
								sender.sendMessage(Component.text("Nametag hiding isn't on for world '" + world + "'", NamedTextColor.LIGHT_PURPLE));
							}
						} else {
							sender.sendMessage(Component.text("world '" + world + "' is not a valid world.", NamedTextColor.RED));
						}
					}
                    UpdatePlayers(sender);
                    break;
                // remove a world from the working list directly - useful for worlds that were set but no longer exist
				case "remove":
					for (String world : worlds) {
						if (OBNametagHide.getInstance().checkWorld(world)) {
							if (OBNametagHide.getInstance().delWorld(world)) {
								sender.sendMessage(Component.text("Removed world '" + world + "' from configuration", NamedTextColor.LIGHT_PURPLE));
							} else {
								sender.sendMessage(Component.text("world '" + world + "' not registered for nametag hiding", NamedTextColor.LIGHT_PURPLE));
							}
						}
						
					}
					UpdatePlayers(sender);
					break;
				// list up worlds current set for nametag hiding 
				case "list":
					if (args.length == 1 || (worlds.contains("on") && args.length > 1)) {
						if (OBNametagHide.getInstance().hideIn.size() == 0) {
							sender.sendMessage(Component.text("Nametag hiding is not enabled on any worlds", NamedTextColor.LIGHT_PURPLE));
						} else {
							sender.sendMessage(Component.text("Enabled for the following worlds:", NamedTextColor.LIGHT_PURPLE));
							for (String world : OBNametagHide.getInstance().hideIn) {
								sender.sendMessage(Component.text("    " + world, NamedTextColor.LIGHT_PURPLE));
							}
						}
					} else if (worlds.contains("worlds") && args.length > 1) {
						sender.sendMessage(Component.text("Worlds on this server:", NamedTextColor.LIGHT_PURPLE));
						for (World w : Bukkit.getWorlds()) {
							if (OBNametagHide.getInstance().hideIn.contains(w.getName())) {
								sender.sendMessage(Component.text("   *" + w.getName(), NamedTextColor.LIGHT_PURPLE));
							} else {
								sender.sendMessage(Component.text("    " + w.getName(), NamedTextColor.LIGHT_PURPLE));
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
        sender.sendMessage(Component.text("/nametaghide", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - Display this menu", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/nametaghide on  <worldname> ...", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - Switch on  for worlds (space separated)", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/nametaghide off <worldname> ...", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - Switch off for worlds (space separated)", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/nametaghide list [on]", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - List worlds currently hiding player nametags", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/nametaghide list worlds", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - List worlds in the server", NamedTextColor.GOLD)));
        sender.sendMessage(Component.text("/nametaghide remove <worldname>", NamedTextColor.LIGHT_PURPLE).append(Component.text(" - remove world from config", NamedTextColor.GOLD)));
    }
}
