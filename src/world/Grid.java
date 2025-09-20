package world;

import org.joml.Vector3f;

public class Grid {
	
	private final float cell;
	private final float originX, originZ;
	
	public Grid(float cellSize, float originX, float originZ) {
		this.cell = cellSize;
		this.originX = originX;
		this.originZ = originZ;
	}
	
	
	//world -> integer cell (i,j)
	public void worldToCell(Vector3f p, int[] outIJ) {
		int i = (int)Math.floor((p.x - originX) / cell);
		int j = (int)Math.floor((p.z - originZ)/ cell);
		outIJ[0] = i; outIJ[1] = j;
	}
	
	//integer cell -> world center
	public void cellCenter(int i, int j, Vector3f out) {
		out.set(originX + (i + 0.5f) * cell, 0f, originZ + (j + 0.5f) * cell);
	}
	
	public float cellSize() {return cell;}

}
