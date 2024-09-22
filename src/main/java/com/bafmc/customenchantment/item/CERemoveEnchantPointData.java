package com.bafmc.customenchantment.item;

import lombok.Getter;
import lombok.Setter;
import com.bafmc.customenchantment.api.MaterialList;

import java.util.List;

public class CERemoveEnchantPointData extends CEItemData {
    private List<Data> dataList;

    @Getter
    @Setter
    public static class Data {
        private String enchantPointType;
        private MaterialList appliesMaterialList;
        private int extraPointRequired;
    }

	public CERemoveEnchantPointData() {
	}

	public CERemoveEnchantPointData(String pattern, List<Data> dataList) {
		super(pattern);
        this.dataList = dataList;
	}

    public List<Data> getDataList() {
        return dataList;
    }

    public void setDataList(List<Data> dataList) {
        this.dataList = dataList;
    }
}
