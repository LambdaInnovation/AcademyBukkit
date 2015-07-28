package cn.academybukkit.adapter;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;

public class ResidenceAdapter {

	public static void init(Class<?> adapter) throws Throwable {
		adapter.getMethod("init", Class.class).invoke(null, ResidenceAdapter.class);
		dummy = (UUID) adapter.getField("dummy").get(null);
	}
	
	private static UUID dummy = null;
	
	private static Player player = null;
	private static World world = null;
	private static String pname = null;
	private static String gname = null;
	private static String wname = null;
	
	//Assumes _world is valid
	public static void setWorld(String _world) {
		world = Bukkit.getWorld(_world);
		wname = world.getName();
	}
	
	//Assumes world and wname is set
	public static void setPlayer(UUID _player) {
		if (_player.equals(dummy)) {
			player = null;
			pname = null;
			gname = null;
		}
		else {
			player = Bukkit.getServer().getPlayer(_player);
			pname = player.getName();
			gname = Residence.getPermissionManager().getGroupNameByPlayer(pname, wname);
		}
	}
	
	//ResidenceBlockListener.onBlockBreak();
	public static boolean checkBlockDestroy(int x, int y, int z) {
		Block block = world.getBlockAt(x, y, z);
		Material mat = block.getType();
		if (Residence.getItemManager().isIgnored(mat, gname, wname))
			return false;
		ClaimedResidence res = Residence.getResidenceManager().getByLoc(block.getLocation());
		if (res != null && Residence.getConfigManager().enabledRentSystem() && Residence.getConfigManager().preventRentModify() && Residence.getRentManager().isRented(res.getName()))
			return true;
		if (res != null && res.getItemIgnoreList().isListed(mat))
			return false;
		FlagPermissions perms = null;
		if (res != null)
			perms = res.getPermissions();
		else {
			if (gname == null)
				perms = Residence.getWorldFlags().getPerms(wname);
			else
				perms = Residence.getWorldFlags().getPerms(wname, gname);
		}
		if (pname == null) {
			if (!perms.has("destroy", perms.has("build", true)))
				return true;
		}
		else {
			if (!perms.playerHas(pname, wname, "destroy", perms.playerHas(pname, wname, "build", true)));
				return true;
		}
		if (mat.equals(Material.CHEST)) {
			if (pname == null) {
				if (!perms.has("container", true))
					return true;			
			}
			else {
				if (!perms.playerHas(pname, wname, "container", true))
					return true;
			}
		}
		return false;
	}
	
}
