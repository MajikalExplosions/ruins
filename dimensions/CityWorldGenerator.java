package majikalexplosions.ruins.dimensions;

import java.util.Random;

import majikalexplosions.ruins.OpenSimplexNoise;
import majikalexplosions.ruins.buildings.BuildingGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraftforge.fml.common.IWorldGenerator;

public class CityWorldGenerator implements IWorldGenerator {
	
	private static BuildingGenerator buildingGenerator = null;
	private static final int BUILDING_Y_VALUE = 32 - 2;//There's a two block buffer under the structures.
	
	@Override
	public void generate(Random random, int x, int z, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		
		//Initialize buildingGenerator if necessary.
		if (buildingGenerator == null) buildingGenerator = new BuildingGenerator(new OpenSimplexNoise(random.nextLong()), ((WorldServer) world).getStructureTemplateManager(), world.getMinecraftServer());
		PlacementSettings ps = buildingGenerator.getPlacementSettings(x, z);
        buildingGenerator.getBuilding(x, z).addBlocksToWorldChunk(world, new BlockPos(x * 16 + buildingGenerator.getXOffset(ps.getRotation()), BUILDING_Y_VALUE, z * 16 + buildingGenerator.getZOffset(ps.getRotation())), ps);
	}
	
}
