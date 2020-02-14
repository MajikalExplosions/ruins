package majikalexplosions.ruins;

import majikalexplosions.ruins.blocks.rails.BlockDelayedDetectorRail;
import majikalexplosions.ruins.blocks.rails.BlockOneWayRail;
import majikalexplosions.ruins.proxy.CommonProxy;
import majikalexplosions.ruins.tileentity.TileEntityDelayedDetectorRail;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = RuinsMain.MOD_ID, name = RuinsMain.MOD_NAME, version = RuinsMain.MOD_VERSION + " " + RuinsMain.MOD_VERSION_NAME)
@Mod.EventBusSubscriber
public class RuinsMain {
	public static final String MOD_ID = "majikalexplosions_ruins";
	public static final String MOD_NAME = "Ecumenopolis Dimension";
	public static final String MOD_VERSION = "0.1.0";
	public static final String MOD_VERSION_NAME = "Bambiraptor";
	
	public static ModBlocks blocks = new ModBlocks();
	
	@SidedProxy(serverSide = "majikalexplosions.ruins.proxy.CommonProxy", clientSide = "majikalexplosions.ruins.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RuinsMain instance;
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//Logger.getGlobal().log(Level.INFO, "[!] Starting up Ruins mod...");
		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(proxy);
	}
	
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent event) {
		proxy.init(event);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPreInitializationEvent event) {
		proxy.postInit(event);
	}
	
	
	@Mod.EventBusSubscriber
	public static class EventHandler {
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			event.getRegistry().registerAll(
					blocks.delayedDetectorRail,
					blocks.oneWayPoweredRail
			);
			
			GameRegistry.registerTileEntity(TileEntityDelayedDetectorRail.class, new ResourceLocation(RuinsMain.MOD_ID, "delayed_detector_rail"));
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			event.getRegistry().registerAll(
					blocks.delayedDetectorRail.createItemBlock(),
					blocks.oneWayPoweredRail.createItemBlock()
			);
		}
		@SubscribeEvent
		public static void registerModels(ModelRegistryEvent event) {
			blocks.delayedDetectorRail.initItemModel();
			blocks.oneWayPoweredRail.initItemModel();
		}

	}

}
