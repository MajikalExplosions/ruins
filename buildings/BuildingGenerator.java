package majikalexplosions.ruins.buildings;

import majikalexplosions.ruins.OpenSimplexNoise;
import net.minecraft.world.gen.structure.template.Template;

public class BuildingGenerator {
	private OpenSimplexNoise noiseBuilding;
	
	private final double BUILDING_SCALE = 1d / 4d;
    private final double BUILDING_DIGIT = 1d / 1000000d;
    
    private final Template[] buildings;
    private int buildingCount;
    
	
	public BuildingGenerator(OpenSimplexNoise nB, int numBuildings) {
		noiseBuilding = nB;
		buildingCount = numBuildings;
		buildings = new Template[buildingCount];
		
		for (int i = 0; i < buildingCount; i++) {
			//Load templates from file
			
			//buildings[i] = buildArray(i);
		}
	}
	
	public Template getBuilding(int x, int z) {
		double buildingID = noiseBuilding.eval((double)x * BUILDING_SCALE, (double)z * BUILDING_SCALE);
        buildingID = Math.abs((double)((double)buildingID % (double)BUILDING_DIGIT) * (double)buildingCount * (double)(1d / (double)BUILDING_DIGIT));
        
        //BuildingID is between 0 and 9 inclusive (buildingCount is 10). Can be scaled if necessary.
        return buildings[(int) buildingID];
	}
}
