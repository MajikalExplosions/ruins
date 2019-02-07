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
	
	private static final double BUILDING_SCALE = 1d / 16d;
	private static final double BUILDING_TYPE_DIGIT = 1d / 1d;
	private static final double BUILDING_NUMBER_DIGIT = 1d / 10000d;
    
    private final Template[] T_ROADS;
    private final Template[] T_HOUSES;
    private final Template[] T_PARKS;
    private final Template[] T_SPAWN;
    private final Template[] T_CITY;
    
  //TODO build more houses and parks
    private final String[] P_SPAWN = {
    		"spawn_1_1_1",
    		"spawn_1_1_2"
    };
    
    private final String[] P_ROADS = {
    		"road_1_1_1",
    		"road_1_1_2"
    };
    
    private final String[] P_HOUSES = {
    		"house_1_1_1",
    		"house_1_1_2"/*,
    		"house_1_1_3",
    		"house_1_1_4",
    		"house_1_1_5",
    		"house_1_1_6",
    		"house_1_1_7"
    		*/
    };
    
    private final String[] P_PARKS = {//also used for filler tiles
    		"park_1_1_1"
    };
    
    private final String[] P_CITY = {//used for highrises
    		"city_1_1_1"
    };
    
	
	public BuildingGenerator(OpenSimplexNoise nB, TemplateManager tm, MinecraftServer ms) {
		noiseBuilding = nB;
		templateManager = tm;
		mcServer = ms;
		
		//Create templates
		T_SPAWN = new Template[P_SPAWN.length];
		T_ROADS = new Template[P_ROADS.length];
		T_HOUSES = new Template[P_HOUSES.length + P_PARKS.length];
		T_PARKS = new Template[P_PARKS.length];
		T_CITY = new Template[P_CITY.length];
		
		for (int i = 0; i < P_SPAWN.length; i++) { T_SPAWN[i] = getTemplateFromFile(P_SPAWN[i]); }//Load all spawn buildings to templates
		for (int i = 0; i < P_ROADS.length; i++) { T_ROADS[i] = getTemplateFromFile(P_ROADS[i]); }//Load all roads to templates
		for (int i = 0; i < P_HOUSES.length; i++) { T_HOUSES[i] = getTemplateFromFile(P_HOUSES[i]); }//Load all houses to templates
		for (int i = 0; i < P_PARKS.length; i++) { T_PARKS[i] = getTemplateFromFile(P_PARKS[i]); }//Load all parks to templates
		for (int i = 0; i < P_CITY.length; i++) { T_CITY[i] = getTemplateFromFile(P_CITY[i]); }//Load all city tiles to templates
		
		for (int i = 0; i < P_PARKS.length; i++) { T_HOUSES[i + P_HOUSES.length] = T_PARKS[i]; }//Adds park tiles to house tile set.
	}
	
	public Template getBuilding(int x, int z) {
		
		
		if (x == 0 && z == 0) { return T_SPAWN[0]; }//main spawn building
		if (x <= 1 && z <= 1 && x >= -1 && z >= -1) { return T_SPAWN[1]; }//outer spawn tiles
		
		if (Math.abs(x) % 4 == 2 && Math.abs(z) % 4 == 2) { return T_ROADS[1]; }//intersection
		if (Math.abs(x) % 4 == 2 || Math.abs(z) % 4 == 2) { return T_ROADS[0]; }//straight road
		
		
		//bNumber is between 0 and 1
		double noiseValue = noiseBuilding.eval((double)x * BUILDING_SCALE, (double)z * BUILDING_SCALE);
        
		//Filler tiles
        if (Math.abs(x) % 4 == 0 && Math.abs(z) % 4 == 0) {
        	double num = getDigit(noiseValue, BUILDING_NUMBER_DIGIT) * T_PARKS.length;
        	return T_PARKS[(int) num];
    	}
        
        double buildingType = getDigit(noiseValue, BUILDING_TYPE_DIGIT);
        
        //City tiles
        if ((int)buildingType == 9 || (int) buildingType == 8) {
        	double num = getDigit(noiseValue, BUILDING_NUMBER_DIGIT) * T_CITY.length;
        	return T_CITY[(int) num];
        }
        
        double num = getDigit(noiseValue, BUILDING_NUMBER_DIGIT) * T_HOUSES.length;
    	return T_HOUSES[(int) num];
	}
	
	private Template getTemplateFromFile(String fileName) {
		return templateManager.getTemplate(mcServer, new ResourceLocation(RuinsMain.MOD_ID, fileName));
	}
	
	public static Rotation getRotation(int x, int z) {
		//TODO fix the fact that rotations tend to move the buildings around for some reason.
		
		/*
		if (x == 0 && z == 0) {
			return Rotation.NONE;
		}
		else if (x <= 1 && z <= 1 && x >= -1 && z >= -1) {
			//Return spawn building outskirts
		}
		else if (Math.abs(x) % 4 == 2) {//the following two cases are right; I checked.
			return Rotation.CLOCKWISE_90;
		}
		else if (Math.abs(z) % 4 == 2) {
			return Rotation.NONE;
		}
		else {
			//TODO add in rotation code for buildings
		}
		*/
		
		return Rotation.NONE;//should never run as long as the else loop returns stuff(which it should)
	}
	
	private static double getDigit(double num, double digit) {
		return Math.abs( (double) ((num % (double)digit) * (double)(1d / (double)digit)));
	}
}
