package majikalexplosions.ruins.dimensions;

import majikalexplosions.ruins.proxy.CommonProxy;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class RuinsWorldType extends WorldType {
	public RuinsWorldType()
    {
        super(RuinsDimension.DIMENSION_NAME);
    }
	
	@Override
    public float getCloudHeight()
    {
        return 256.0F;
    }
	
	@Override
    public BiomeProvider getBiomeProvider(World world)
    {
        return new BiomeProviderSingle(CommonProxy.bWasteland);
    }
	
	@Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return new RuinsChunkGenerator(world);
    }
}
