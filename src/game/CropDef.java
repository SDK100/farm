package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CropDef {
	public final String id;
	public final double minutesPerStage;
	private final List<String> stageObjPaths;
	
	public CropDef(String id, double minutesPerStage, List<String> stageObjPaths) {
        this.id = id;
        this.minutesPerStage = Math.max(1.0, minutesPerStage);
        this.stageObjPaths = List.copyOf(stageObjPaths);
    }
	
	public int stages() {return stageObjPaths.size();}
	public boolean hasStages() {return !stageObjPaths.isEmpty();}
	
	//index -> valid range
	public int clampStage(int s) {
		if (s < 0) return 0;
		int max = Math.max(0, stageObjPaths.size() - 1);
		return Math.min(s, max);
	}
	
	//returns obj for stage
	public String objForStage(int stage) {
		if (stageObjPaths.isEmpty()) return null;
		return stageObjPaths.get(clampStage(stage));
	}
	
	 public static final class Builder {
	        private final String id;
	        private double minutesPerStage = 60.0;
	        private final List<String> stagePaths = new ArrayList<>();
	        public Builder(String id) { this.id = id; }
	        public Builder minutesPerStage(double m) { this.minutesPerStage = m; return this; }
	        public Builder addStagePath(String path) { this.stagePaths.add(path); return this; }
	        public CropDef build() { return new CropDef(id, minutesPerStage, stagePaths); }
	    }

	    
	    public List<String> stagePaths() { return Collections.unmodifiableList(stageObjPaths); }

}
