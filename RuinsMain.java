package majikalexplosions.ruins;

import majikalexplosions.ruins.proxy.CommonProxy;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = RuinsMain.MOD_ID, name = RuinsMain.MOD_NAME, version = RuinsMain.MOD_VERSION + " " + RuinsMain.MOD_VERSION_NAME)
@Mod.EventBusSubscriber
public class RuinsMain {
	public static final String MOD_ID = "majikalexplosions_ruins";
	public static final String MOD_NAME = "Ruins Dimension";
	public static final String MOD_VERSION = "0.1.0";
	public static final String MOD_VERSION_NAME = "Abydosaurus";
	
	
	@SidedProxy(serverSide = "majikalexplosions.ruins.proxy.CommonProxy", clientSide = "majikalexplosions.ruins.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	@Mod.Instance(MOD_ID)
	public static RuinsMain instance;
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Logger.getGlobal().log(Level.INFO, "[!] Starting up Ruins mod...");
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
	public static class RegistrationHandler {
		
	}
}
