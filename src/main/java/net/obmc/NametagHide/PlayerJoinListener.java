package net.obmc.NametagHide;

import java.util.logging.Logger;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener
{
	static Logger log = Logger.getLogger("Minecraft");
	
    public PlayerJoinListener() {
    }
    
    // catch player join events
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        NametagHide.getInstance().checkPlayer(event.getPlayer());
    }
    
    // catch player changing world events
    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
    	NametagHide.getInstance().checkPlayer(event.getPlayer());
    }
}

