package data;

import java.util.LinkedHashMap;
import java.util.Map;

public class RecipeDef {
	public final String id;
	public final Map<String, Integer> requires = new LinkedHashMap<>();
	public final int cookMinutes;
	public final String outputItemId;
	public final int outputCount;
	
	public RecipeDef(String id, int cookMinutes, String outputItemId, int outputCount) {
        this.id = id; this.cookMinutes = cookMinutes;
        this.outputItemId = outputItemId; this.outputCount = outputCount;
    }
	

}
