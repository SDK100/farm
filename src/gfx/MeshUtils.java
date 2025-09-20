package gfx;

public class MeshUtils {
	
	public static MeshPNT makeGroundPlane(float size, float uvRepeat) {
		float h = size * 0.5f;
        float[] v = {
            -h,0,-h,  0,1,0,   0,0,
             h,0,-h,  0,1,0,   uvRepeat,0,
             h,0, h,  0,1,0,   uvRepeat,uvRepeat,
            -h,0, h,  0,1,0,   0,uvRepeat
        };
        int[] idx = {0,1,2, 2,3,0};
        return new MeshPNT(v, idx);
	}
	
	 public static MeshPNT makeUnitTileQuad() {
	        float s = 0.5f;
	        float[] v = {
	            -s,0,-s,  0,1,0,  0,0,
	             s,0,-s,  0,1,0,  1,0,
	             s,0, s,  0,1,0,  1,1,
	            -s,0, s,  0,1,0,  0,1
	        };
	        int[] idx = {0,1,2, 2,3,0};
	        return new MeshPNT(v, idx);
	    }

}
