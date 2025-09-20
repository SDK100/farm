package game;

import java.util.Map;

public final class Farm {
    private final int w, h;
    private final Plot[][] plots;
    private final Map<String, CropDef> crops;

    public Farm(int w, int h, Map<String, CropDef> crops) {
        this.w = w; this.h = h;
        this.crops = crops;
        plots = new Plot[w][h];
        for (int i=0;i<w;i++) for (int j=0;j<h;j++) plots[i][j] = new Plot();
    }

    public boolean inBounds(int i, int j) { return i>=0 && j>=0 && i<w && j<h; }
    public Plot get(int i, int j) { return plots[i][j]; }
    public CropDef crop(String id) { return crops.get(id); }

    public void tickGrowth(double dtRealSeconds, GameTime time) {
        double now = time.minutesTotal();
        for (int i=0;i<w;i++) for (int j=0;j<h;j++) {
            Plot p = plots[i][j];
            if (!p.isPlanted() || !p.watered) continue;
            CropDef def = crops.get(p.cropId);
            if (def == null || def.stages() == 0) continue;

            double elapsed = now - p.plantedAtMinutes;
            int targetStage = (int)Math.floor(elapsed / def.minutesPerStage);
            targetStage = Math.min(targetStage, def.stages()); // mature at last stage index
            if (targetStage > p.growthStage) p.growthStage = targetStage;
        }
    }

    public void dryAll() {
        for (int i=0;i<w;i++) for (int j=0;j<h;j++) plots[i][j].watered = false;
    }
}
