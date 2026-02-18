package com.bafmc.customenchantment.menu.artifactupgrade;

import com.bafmc.bukkit.bafframework.custommenu.menu.data.ExtraData;
import com.bafmc.customenchantment.item.CEItem;
import com.bafmc.customenchantment.item.artifact.CEArtifact;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArtifactUpgradeExtraData extends ExtraData {

    private ArtifactUpgradeSettings settings;

    private CEArtifact selectedArtifact;
    private CEArtifact previewArtifact;
    private List<CEItem> ingredientItems = new ArrayList<>();
    private List<Double> ingredientPoints = new ArrayList<>();
    private double totalPoint;

    public ArtifactUpgradeExtraData(ArtifactUpgradeSettings settings) {
        this.settings = settings;
    }

    public void addIngredient(CEItem ceItem, double point) {
        ingredientItems.add(ceItem);
        ingredientPoints.add(point);
        totalPoint += point;
    }

    public void removeIngredient(int index) {
        totalPoint -= ingredientPoints.get(index);
        ingredientItems.remove(index);
        ingredientPoints.remove(index);
    }

    public void clearIngredient() {
        ingredientItems.clear();
        ingredientPoints.clear();
        totalPoint = 0;
    }

    public void clearSelectedArtifact() {
        selectedArtifact = null;
        previewArtifact = null;
    }

    public boolean canUpgrade() {
        return totalPoint > 0 && selectedArtifact != null;
    }
}
