package com.bafmc.customenchantment.api;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bafmc.bukkit.utils.MathUtils;

public class VectorFormat {
	private String locationFormat;

	public VectorFormat(String locationFormat) {
		this.locationFormat = locationFormat;
	}

	public Vector getVector(Player player, Player enemy) {
		Location pLocation = player != null ? player.getLocation() : null;
		Location eLocation = enemy != null ? enemy.getLocation() : null;

		return getVector(pLocation, eLocation);
	}
	
	public Vector getVector(LivingEntity player, LivingEntity enemy) {
		Location pLocation = player != null ? player.getLocation() : null;
		Location eLocation = enemy != null ? enemy.getLocation() : null;

		return getVector(pLocation, eLocation);
	}

	public Vector getVector(Location pLocation, Location eLocation) {
		Vector vector = null;
		try {
			if (pLocation == null && eLocation == null) {
				return null;
			}

			Vector pVector = pLocation != null ? pLocation.getDirection() : null;
			Vector eVector = eLocation != null ? eLocation.getDirection() : null;

			String locationFormat = this.locationFormat;

			if (pLocation != null)
				locationFormat = locationFormat.replace("xP", "" + pVector.getX()).replace("yP", "" + pVector.getY())
						.replace("zP", "" + pVector.getZ());

			if (eLocation != null)
				locationFormat = locationFormat.replace("xE", "" + eVector.getX()).replace("yE", "" + eVector.getY())
						.replace("zE", "" + eVector.getZ());

			// R - RelativeTo
			if (eLocation != null && locationFormat.indexOf("R") != -1) {
				Vector rVector = eVector.subtract(pVector);
				
				locationFormat = locationFormat.replace("xR", "" + rVector.getX()).replace("yR", "" + rVector.getY())
						.replace("zR", "" + rVector.getZ());
			}

			// x, y, z
			Double x = null;
			Double y = null;
			Double z = null;

			List<String> lFormat = Arrays.asList(locationFormat.split("_"));

			if (lFormat.size() > 2) {
				if (lFormat.get(0).indexOf("~") != -1) {
					lFormat.set(0, lFormat.get(0).replace("~", "" + pVector.getX()));
				}
				x = MathUtils.evalDouble(lFormat.get(0));
				if (lFormat.get(1).indexOf("~") != -1) {
					lFormat.set(1, lFormat.get(1).replace("~", "" + pVector.getY()));
				}
				y = MathUtils.evalDouble(lFormat.get(1));
				if (lFormat.get(2).indexOf("~") != -1) {
					lFormat.set(2, lFormat.get(2).replace("~", "" + pVector.getZ()));
				}
				z = MathUtils.evalDouble(lFormat.get(2));
			}

			if (lFormat.size() <= 3) {
				vector = new Vector(x, y, z);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	public float getExactYaw(float yaw) {
		if (yaw < 0) {
			return Math.abs(yaw);
		}
		if (yaw > 0) {
			return 360 - yaw;
		}
		return 0;
	}
}
