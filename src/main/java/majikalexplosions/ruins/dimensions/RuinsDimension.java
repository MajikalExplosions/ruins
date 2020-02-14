package majikalexplosions.ruins.dimensions;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class RuinsDimension {
	
	public static DimensionType dimensionType;
	public final static String DIMENSION_NAME = "ruins";

    public static void init() {
    	dimensionType = DimensionType.register(DIMENSION_NAME, "_" + DIMENSION_NAME, 191, RuinsWorldProvider.class, false);
    	DimensionManager.registerDimension(191, dimensionType);
    }
}
