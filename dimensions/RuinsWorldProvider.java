package majikalexplosions.ruins.dimensions;

import majikalexplosions.ruins.proxy.CommonProxy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class RuinsWorldProvider extends WorldProvider {

	@Override
	public DimensionType getDimensionType() {
		return RuinsDimension.dimensionType;
	}
	
	@Override
    public String getSaveFolder() {
        return "ruins";
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new RuinsChunkGenerator(world);
    }
    
    @Override
    public BlockPos getRandomizedSpawnPoint() {
    	return new BlockPos(0, CityWorldGenerator.BUILDING_Y_VALUE + 1, 0);
    }
    
    @Override
    public boolean isSurfaceWorld()
    {
        return true;
    }
    
    @Override
    public BiomeProvider getBiomeProvider() {
    	return new BiomeProviderSingle(CommonProxy.bWasteland);
    }
}
