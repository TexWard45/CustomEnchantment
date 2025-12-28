package com.bafmc.customenchantment.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTemporaryStorage extends CEPlayerExpansion {
	private ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<String, Object>();

	public PlayerTemporaryStorage(CEPlayer cePlayer) {
		super(cePlayer);
	}

	public void onJoin() {

	}

	public void onQuit() {

	}

	public void set(String key, Object value) {
		if (value == null) {
			unset(key);
			return;
		}
		map.put(key, value);
	}
	
	public void unset(String key) {
		map.remove(key);
	}
	
	public List<String> getKeys() {
		return new ArrayList<String>(map.keySet());
	}
	
	public Object get(String key) {
		return map.get(key);
	}

	public Object get(String key, Object value) {
		return map.containsKey(key) ? map.get(key) : value;
	}

	public boolean isSet(String key) {
		return map.containsKey(key);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String value) {
		return map.containsKey(key) ? map.get(key).toString() : value;
	}

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean value) {
        return map.containsKey(key) ? Boolean.valueOf(map.get(key).toString()) : value;
    }

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int value) {
		try {
			return Integer.parseInt(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

	public double getDouble(String key) {
		return getDouble(key, 0d);
	}

	public double getDouble(String key, double value) {
		try {
			return Double.parseDouble(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

	public long getLong(String key) {
		return getLong(key, 0);
	}

	public long getLong(String key, long value) {
		try {
			return Long.parseLong(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

	public byte getByte(String key) {
		return getByte(key, (byte) 0);
	}

	public byte getByte(String key, byte value) {
		try {
			return Byte.parseByte(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

	public float getFloat(String key) {
		return getFloat(key, 0f);
	}

	public float getFloat(String key, float value) {
		try {
			return Float.valueOf(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

	public boolean isBoolean(String key) {
		return isBoolean(key, false);
	}

	public boolean isBoolean(String key, boolean value) {
		try {
			return Boolean.valueOf(map.get(key).toString());
		} catch (Exception e) {
			return value;
		}
	}

    public void removeStartsWith(String key) {
        List<String> removeList = new ArrayList<>();
        for (String k : map.keySet()) {
            if (k.startsWith(key)) {
                removeList.add(k);
            }
        }
        for (String k : removeList) {
            map.remove(k);
        }
    }

    public void setAll(Map<String, Object> map) {
        this.map.putAll(map);
    }
}
