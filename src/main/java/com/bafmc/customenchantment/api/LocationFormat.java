package com.bafmc.customenchantment.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.bafmc.bukkit.utils.MathUtils;
import com.bafmc.bukkit.utils.StringUtils;

public class LocationFormat {
	private String locationFormat;

	public LocationFormat(String locationFormat) {
		this.locationFormat = locationFormat;
	}

	public Location getLocation(Player player, Player enemy) {
		Location pLocation = player != null ? player.getLocation() : null;
		Location eLocation = enemy != null ? enemy.getLocation() : null;

		return getLocation(pLocation, eLocation);
	}

	public Location getLocation(Entity player, Entity enemy) {
		Location pLocation = player != null ? player.getLocation() : null;
		Location eLocation = enemy != null ? enemy.getLocation() : null;

		return getLocation(pLocation, eLocation);
	}

	public Location getLocation(Location pLocation, Location eLocation) {
		Location location = null;
		// world, x, y, z, yaw, pitch
		World world = null;
		Double x = null;
		Double y = null;
		Double z = null;
		Float yaw = null;
		Float pitch = null;
		String locationFormat = this.locationFormat;
		try {
			if (pLocation == null && eLocation == null) {
				return null;
			}

			double distance = 0;
			if (pLocation != null && eLocation != null && pLocation.getWorld() == eLocation.getWorld()) {
				distance = pLocation.distance(eLocation);
				locationFormat = locationFormat.replace("distance", "" + distance);
			}

			if (pLocation != null)
				locationFormat = locationFormat.replace("xP", "" + pLocation.getX())
						.replace("yP", "" + pLocation.getY()).replace("zP", "" + pLocation.getZ())
						.replace("eyawP", "" + getExactYaw(pLocation.getYaw())).replace("yawP", "" + pLocation.getYaw())
						.replace("pitchP", "" + pLocation.getPitch());

			if (eLocation != null)
				locationFormat = locationFormat.replace("xE", "" + eLocation.getX())
						.replace("yE", "" + eLocation.getY()).replace("zE", "" + eLocation.getZ())
						.replace("eyawE", "" + getExactYaw(eLocation.getYaw())).replace("yawE", "" + eLocation.getYaw())
						.replace("pitchE", "" + eLocation.getPitch());

			List<String> lFormat = StringUtils.split(locationFormat, "_", 0);
			if (lFormat.size() > 0) {
				String worldFormat = lFormat.get(0);
				if (worldFormat.equals("~")) {
					world = pLocation.getWorld();
				} else {
					world = Bukkit.getWorld(worldFormat);
					if (world == null) {
						System.out.println("Null2");
						return null;
					}
				}
			}

			if (lFormat.size() > 3) {
				if (lFormat.get(1).indexOf("~") != -1) {
					lFormat.set(1, lFormat.get(1).replace("~", "" + pLocation.getX()));
				}
				x = MathUtils.evalDouble(lFormat.get(1));
				if (lFormat.get(2).indexOf("~") != -1) {
					lFormat.set(2, lFormat.get(2).replace("~", "" + pLocation.getY()));
				}
				y = MathUtils.evalDouble(lFormat.get(2));
				if (lFormat.get(3).indexOf("~") != -1) {
					lFormat.set(3, lFormat.get(3).replace("~", "" + pLocation.getZ()));
				}
				z = MathUtils.evalDouble(lFormat.get(3));
			}

			if (lFormat.size() > 5) {
				if (lFormat.get(4).indexOf("~") != -1) {
					lFormat.set(4, lFormat.get(4).replace("~", "" + pLocation.getYaw()));
				}
				yaw = (float) ((double) MathUtils.evalDouble(lFormat.get(4)));
				if (lFormat.get(5).indexOf("~") != -1) {
					lFormat.set(5, lFormat.get(5).replace("~", "" + pLocation.getPitch()));
				}
				pitch = (float) ((double) MathUtils.evalDouble(lFormat.get(5)));
			}

			if (lFormat.size() <= 4) {
				location = new Location(world, x, y, z);
			} else if (lFormat.size() <= 6) {
				location = new Location(world, x, y, z, yaw, pitch);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	public List<Material> transparent = Arrays.asList(Material.AIR, Material.WATER, Material.LAVA);

	public Location getLegitLocation(Location from, Location to) {
		Location location = null;

		Vector startVector = from.toVector();
		Vector startDirection = to.toVector().subtract(from.toVector());
		int maxDistance = (int) from.distance(to);
		int maxLength = 0;
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		double offSet = 0;

		ArrayList<Block> blocks = new ArrayList<Block>();
		Iterator<Block> itr = new BlockIterator(from.getWorld(), startVector, startDirection, offSet, maxDistance);
		while (itr.hasNext()) {
			Block block = (Block) itr.next();
			blocks.add(block);
			if ((maxLength != 0) && (blocks.size() > maxLength)) {
				blocks.remove(0);
			}
			Material material = block.getType();
			if (transparent == null ? !material.equals(Material.AIR) : !transparent.contains(material)) {
				break;
			}
			location = block.getLocation();
		}

		if (location != null) {
			if (Math.floor(to.getX()) == location.getX() && Math.floor(to.getY()) == location.getY()
					&& Math.floor(to.getZ()) == location.getZ()) {
				return to;
			}

			location.setX(location.getX() + 0.5);
			location.setZ(location.getZ() + 0.5);
			location.setYaw(to.getYaw());
			location.setPitch(to.getPitch());
			return location;
		}

		return from;
	}

	public static float getExactYaw(float yaw) {
		if (yaw < 0) {
			yaw = 360 + yaw;
		}

		yaw = 270 - yaw;
		return yaw;
	}
}
