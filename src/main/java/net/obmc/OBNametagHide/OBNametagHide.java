package net.obmc.OBNametagHide;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

public class OBNametagHide extends JavaPlugin {

    static Logger log = Logger.getLogger("Minecraft");
	
	public static OBNametagHide instance;
	
    private PlayerJoinListener listen;
	
	private Scoreboard score;
    public List<String> hideIn;
    
    public OBNametagHide() {
    	instance = this;
    }
    
    // make our (public) main class methods and variables available to other classes
    public static OBNametagHide getInstance() {
    	return instance;
    }
    
    // enable the plugin
    public void onEnable() {
        // load up the worlds where we want to hide nametags in
        this.saveDefaultConfig();
        hideIn = this.getConfig().getStringList("hideIn");

    	this.score = this.getServer().getScoreboardManager().getMainScoreboard();
        this.listen = new PlayerJoinListener();
        this.getServer().getPluginManager().registerEvents((Listener)this.listen, (Plugin)this);
        this.getCommand("nametaghide").setExecutor(new PlayerCommandListener());
    }

    // disable the plugin
    public void onDisable() {
    	this.saveConfig();
    }

    // return the player join listener if needed 
    public PlayerJoinListener getListen() {
        return this.listen;
    }

	// check if a world is set for nametag hiding in our working list
	public boolean checkWorld(String world) {
		boolean check = false;
		if (hideIn.contains(world)) {
    		check = true;
    	}
    	return check;
	}

	// add world to our working list (in memory - it's saved on server stop)
	public boolean addWorld(String world) {
		if (!hideIn.contains(world)) {
			hideIn.add(world);			
		} else {
			return false;
		}
		return true;
	}

	// check a world actually exists in the server
	public boolean checkWorldExists(String world) {
		for (World w : Bukkit.getWorlds()) {
			if (w.getName().equals(world)) {
				return true;
			}
		}
		return false;
	}

	// directly remove world from our working list
	// useful when a world no longer exists
	public boolean delWorld(String world) {
		if (hideIn.contains(world)) {
			hideIn.remove(hideIn.indexOf(world));
			this.getConfig().set("hideIn", hideIn);
			this.saveConfig();
		} else {
			return false;
		}
		return true;
	}

	// check if player already has team in the scoreboard (team name is player name)
	// if not add a new team with player name as the team name, and add the player
	// and set visibility accordingly
    public void checkPlayer(final Player player) {
    	Boolean hasteam = false;
    	for (Team tt : this.score.getTeams()) {
    		if (tt.getName().equals(player.getName())) {
    			hasteam = true;
    			break;
    		}
    	}
    	if (!hasteam) {
    		if (checkWorld(player.getWorld().getName())) {
    			try {
    				Team newteam = this.score.registerNewTeam(player.getName());
    				newteam.addEntry(player.getName());
    				newteam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	} else {
    		if (!checkWorld(player.getWorld().getName())) {
    	    	for (Team tt : this.score.getTeams()) {
    	    		if (tt.getName().equals(player.getName())) {
    	    			tt.unregister();
    	    			break;
    	    		}
    	    	}
    		}
    	}
    }
}