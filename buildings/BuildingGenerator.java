package majikalexplosions.ruins.buildings;

import majikalexplosions.ruins.OpenSimplexNoise;
import net.minecraft.block.state.IBlockState;

public class BuildingGenerator {
	private OpenSimplexNoise noiseBuilding;
	
	private final double BUILDING_SCALE = 1d / 4d;
    private final double BUILDING_DIGIT = 1d / 1000000d;
    
    private final IBlockState[][][][] buildings;
    private int buildingCount;
	
	public BuildingGenerator(OpenSimplexNoise nB, int numBuildings) {
		noiseBuilding = nB;
		buildingCount = numBuildings;
		buildings = new IBlockState[buildingCount][][][];
		
		for (int i = 0; i < buildingCount; i++) { buildings[i] = buildArray(i); }
	}
	
	public IBlockState[][][] getBuilding(int x, int z) {
		double buildingID = noiseBuilding.eval((double)x * BUILDING_SCALE, (double)z * BUILDING_SCALE);
        buildingID = Math.abs((double)((double)buildingID % (double)BUILDING_DIGIT) * (double)buildingCount * (double)(1d / (double)BUILDING_DIGIT));
        
        //BuildingID is between 0 and 9 inclusive (buildingCount is 10). Can be scaled if necessary.
        
        return buildArray((int) buildingID);
	}
	
	private IBlockState[][][] buildArray(int id) {
		IBlockState[][][] building = new IBlockState[16][224][16];
		switch(id) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			default:
				return null;
		}
		return building;
	}
}
