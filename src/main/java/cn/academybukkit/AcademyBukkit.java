package cn.academybukkit;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author EAirPeter
 */
public final class AcademyBukkit extends JavaPlugin {
    
    private static final String ADAPTER =
        "cn.academy.support.bukkit.BukkitAdapter";
    
    private static final Set<String> plugs = new HashSet<String>();;
    
    static {
        plugs.add("PlotMe");
        plugs.add("Residence");
    }
    
    private static List<RegisteredListener> lrlBlockBreak;
    
    public static void init() throws Throwable {
        Class<?> adapter = Class.forName(ADAPTER);
        adapter.getMethod("init", Class.class).
            invoke(null, AcademyBukkit.class);
        dummyUuid = (UUID) adapter.getField("dummy").get(null);
        DummyPlayer.name = "__DummyPlayer<=>" + dummyUuid;
        lrlBlockBreak = new LinkedList<RegisteredListener>();
        for (RegisteredListener rl :
            BlockBreakEvent.getHandlerList().getRegisteredListeners())
        {
            Plugin plug = rl.getPlugin();
            if (plug.isEnabled() && plugs.contains(plug.getName()))
                lrlBlockBreak.add(rl);
        }
    }
    
    static UUID dummyUuid = null;
    
    private static Player dummyPlayer = new DummyPlayer();
    private static Player player = dummyPlayer;
    private static World world = null;
    
    // Assumes world is valid
    public static void setWorld(String world_) {
        world = Bukkit.getWorld(world_);
    }
    
    // Assumes world is set
    public static void setPlayer(UUID uuid_) {
        player = uuid_.equals(dummyUuid) ? dummyPlayer :
            Bukkit.getServer().getPlayer(uuid_);
    }
    
    public static boolean checkBlockDestroy(int x, int y, int z) {
        if (world == null)
            return true;
        if (player == dummyPlayer)
            DummyPlayer.world = world;
        BlockBreakEvent event =
            new BlockBreakEvent(world.getBlockAt(x, y, z), player);
        for (RegisteredListener rl : lrlBlockBreak)
            try {
                rl.callEvent(event);
            } catch (Throwable e) {
                log.log(Level.SEVERE, "Could not pass event " +
                    event.getEventName() + " to " +
                    rl.getPlugin().getDescription().getFullName(), e);
            }
        return event.isCancelled();
    }
    
    private static Logger log;
    
    @Override
    public void onEnable() {
        log = getLogger();
        try {
            init();
            log.info("AcademyBukkit is enabled.");
        }
        catch (Throwable e) {
            log.log(Level.SEVERE, "Failed to initialize AcademyBukkit", e);
        }
    }
    
}
