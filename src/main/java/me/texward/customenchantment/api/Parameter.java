package me.texward.customenchantment.api;

import java.util.ArrayList;
import java.util.List;

import me.texward.texwardlib.util.StringUtils;

public class Parameter {
	private List<String> parameter = new ArrayList<String>();

	public Parameter(List<String> parameter) {
		this.parameter = parameter;
	}

	public Parameter(String line) {
		this.parameter = StringUtils.split(line, ":", 0);
	}

	public boolean isSet(int index) {
		return index < parameter.size();
	}

	public String getString(int index) {
		return getString(index, null);
	}

	public String getString(int index, String s) {
		return isSet(index) ? parameter.get(index) : s;
	}

	public Long getLong(int index) {
		return getLong(index, null);
	}

	public Long getLong(int index, Long l) {
		try {
			return isSet(index) ? Long.valueOf(parameter.get(index)) : l;
		} catch (Exception e) {
			return l;
		}
	}

	public Double getDouble(int index) {
		return getDouble(index, null);
	}

	public Double getDouble(int index, Double d) {
		try {
			return isSet(index) ? Double.valueOf(parameter.get(index)) : d;
		} catch (Exception e) {
			return d;
		}
	}

	public Integer getInteger(int index) {
		return getInteger(index, null);
	}

	public Integer getInteger(int index, Integer i) {
		try {
			return isSet(index) ? Integer.valueOf(parameter.get(index)) : i;
		} catch (Exception e) {
			return i;
		}
	}

	public Byte getByte(int index) {
		return getByte(index, null);
	}

	public Byte getByte(int index, Byte b) {
		try {
			return isSet(index) ? Byte.valueOf(parameter.get(index)) : b;
		} catch (Exception e) {
			e.printStackTrace();
			return b;
		}
	}

	public int size() {
		return parameter.size();
	}
}
