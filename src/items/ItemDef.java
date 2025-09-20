package items;

public class ItemDef {
	public enum Kind {TOOL,SEED,VEG,SLICE,DISH,OTHER}
	
	public final String id;
	public final Kind kind;
	public final String iconPath;
	
	
	public ItemDef(String id, Kind kind, String iconPath) {
		super();
		this.id = id;
		this.kind = kind;
		this.iconPath = iconPath;
	}
	
	

}
