package majikalexplosions.ruins.buildings;

import majikalexplosions.ruins.OpenSimplexNoise;

import majikalexplosions.ruins.RuinsMain;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class BuildingGenerator {
	
	private OpenSimplexNoise noiseBuilding;
	private TemplateManager templateManager;
	private MinecraftServer mcServer;
	
	private final double BUILDING_SCALE = 1d / 4d;
    private final double BUILDING_DIGIT = 1d / 1000000d;
    
    private final Template[] buildings;
    private final String[] templateFileNames = {
    		"road_1_1_1",
    		"road_1_1_2",
    		"house_1_1_1",
    		"house_1_1_2"/*,
    		"house_1_1_3",
    		"house_1_1_4",
    		"house_1_1_5",
    		"house_1_1_6",
    		"house_1_1_7",
    		"park_1_1_1",
    		"park_1_1_2",
    		"park_1_1_3"*/
    };
    
	
	public BuildingGenerator(OpenSimplexNoise nB, TemplateManager tm, MinecraftServer ms) {
		noiseBuilding = nB;
		
		buildings = new Template[templateFileNames.length];
		
		templateManager = tm;
		mcServer = ms;
		
		for (int i = 0; i < buildings.length; i++) { buildings[i] = getTemplateFromFile(templateFileNames[i]); }//Load all buildings to templates
	}
	
	public Template getBuilding(int x, int z) {
		/*
		if (x == 0 && z == 0) {
			//Return spawn building
			return getTemplateFromFile("spawn_1_1_1");
		}
		else if (x <= 1 && z <= 1 && x >= -1 && z >= -1) {
			//Return spawn building outside
			return getTemplateFromFile("spawn_1_1_2");
		}
		//check if chunk is road
		else if (Math.abs(x) % 3 == 2 && Math.abs(z) % 3 == 2) {
			//intersection
			return getTemplateFromFile("road_1_1_2");
		}
		else if (Math.abs(x) % 3 == 2 || Math.abs(z) % 3 == 2) {
			//normal road
			return getTemplateFromFile("road_1_1_1");
		}
        double buildingID = Math.abs((double)((double)noiseBuilding.eval((double)x * BUILDING_SCALE, (double)z * BUILDING_SCALE) % (double)BUILDING_DIGIT) * (double)buildings.length * (double)(1d / (double)BUILDING_DIGIT));//BuildingID is between 0 and 9 inclusive (buildingCount is 10). Can be scaled if necessary.
        */
        //TODO build houses and parks
        return buildings[0];
        //return buildings[(int) buildingID];
	}
	
	private Template getTemplateFromFile(String fileName) {
		return templateManager.getTemplate(mcServer, new ResourceLocation(RuinsMain.MOD_ID, fileName));
	}
	
	public static Rotation getRotation(int x, int z) {
		
		//Parse roads and spawn buildings
		/*
		if (x == 0 && z == 0) {
			return Rotation.NONE;
		}
		else if (x <= 1 && z <= 1 && x >= -1 && z >= -1) {
			//Return spawn building outskirts
		}
		else if (Math.abs(x) % 3 == 2) {//the following two cases are right; I checked.
			return Rotation.CLOCKWISE_90;
		}
		else if (Math.abs(z) % 3 == 2) {
			return Rotation.NONE;
		}
		else {
			//TODO add in rotation code for buildings
		}
		*/
		return Rotation.NONE;//should never run as long as the else loop returns stuff(which it should)
	}
}
