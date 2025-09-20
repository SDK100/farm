package game;

public class Plot {
	public boolean hoed = false;
	public boolean watered = false;
	
	public String cropId = null;
	public double plantedAtMinutes = 0.0;
	public int growthStage = 0;
	
	public boolean isEmpty() {return !hoed && cropId == null;}
	public boolean isPlanted() {return cropId != null;}
	public boolean isMature(Cropdef def) {return isPlanted() && grothStage >= def.stages;}

}
