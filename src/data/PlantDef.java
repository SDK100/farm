package data;

import java.util.List;

public class PlantDef {
	public final String id;
	public final List<Integer> minutesPerStage;
	public final List<String> stageMeshPaths;
	public final String yieldItemId;
	public final int yieldCount;
	public final int slicesPerItem;
	
	public PlantDef(String id, List<Integer> minutesPerStage, List<String> stageMeshPaths, String yieldItemId, int yieldCount, int slicesPerItem) {
		this.id = id;
        this.minutesPerStage = minutesPerStage;
        this.stageMeshPaths = stageMeshPaths;
        this.yieldItemId = yieldItemId;
        this.yieldCount = yieldCount;
        this.slicesPerItem = slicesPerItem;
        if (minutesPerStage.size() != stageMeshPaths.size()) {
            throw new IllegalArgumentException("PlantDef " + id + ": minutesPerStage and stageMeshPaths must be same length");
        }
	}
	
	public int numStages() {return minutesPerStage.size();}
	public boolean isMature(int stage) {return stage >= numStages()-1;}

}
