package cn.academybukkit;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class AcademyBukkit extends JavaPlugin {

	@Override
	public void onEnable() {
		setupPlugin("Residence");
	}
	
	//Invokes cn.academybukkit.adapter.$(name)Adapter.init(cn.academy.support.bukkit.$(name)Adapter.class)
	public void setupPlugin(String name) {
		if (getServer().getPluginManager().isPluginEnabled(name)) {
			getLogger().info("Found \'" + name + "\', hooking...");
			try {
				Class.forName(PACKAGE_PLUGIN + name + "Adapter").getMethod("init", Class.class).invoke(null, Class.forName(PACKAGE_MOD + name + "Adapter"));
				getLogger().info("Successfully hooked into \'" + name + "\'");
			}
			catch (Throwable e) {
				getLogger().log(Level.SEVERE, "Failed to hook into \'" + name + "\'", e);
			}
		}
		else
			getLogger().info("\'" + name + "\' doesn\'t exist...");
	}
	
	private static String PACKAGE_PLUGIN = "cn.academybukkit.adapter.";
	private static String PACKAGE_MOD = "cn.academy.support.bukkit.";
	
}
