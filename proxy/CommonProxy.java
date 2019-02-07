package majikalexplosions.ruins.proxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import majikalexplosions.ruins.biomes.*;
import majikalexplosions.ruins.dimensions.CityWorldGenerator;
import majikalexplosions.ruins.dimensions.RuinsDimension;
import majikalexplosions.ruins.dimensions.RuinsWorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy {
	public static Biome bWasteland;
	public void preInit(FMLPreInitializationEvent e) {
		bWasteland = new BiomeWasteland((new Biome.BiomeProperties("Desolate Wasteland")).setTemperature(3F).setRainfall(0.3F).setBaseHeight(-0.25F).setHeightVariation(0F).setRainDisabled());
		BiomeManager.addBiome(BiomeType.WARM, new BiomeEntry(bWasteland, 1500));
		bWasteland.setRegistryName(bWasteland.getBiomeName().toLowerCase().replace(' ', '_'));
		BiomeManager.addSpawnBiome(bWasteland);
		
		GameRegistry.registerWorldGenerator(new CityWorldGenerator(), 19000);
		
		RuinsDimension.init();
		RuinsWorldType rwt = new RuinsWorldType();
	}
	
	public void init(FMLPreInitializationEvent e) {
		
	}
	
	public void postInit(FMLPreInitializationEvent e) {
		
	}
	
	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event) {
		event.getRegistry().register(bWasteland);
		BiomeDictionary.addTypes(bWasteland, BiomeDictionary.Type.SANDY, BiomeDictionary.Type.HOT, BiomeDictionary.Type.WASTELAND);
	}
}
