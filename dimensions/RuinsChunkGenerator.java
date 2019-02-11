package majikalexplosions.ruins.dimensions;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;

import majikalexplosions.ruins.OpenSimplexNoise;
import majikalexplosions.ruins.biomes.BiomeWasteland;
import majikalexplosions.ruins.buildings.BuildingGenerator;
import net.minecraft.world.WorldServer;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class RuinsChunkGenerator implements IChunkGenerator {
	private final World world;
    private Random random;
    private Biome[] biomesForGeneration;
    
    private OpenSimplexNoise noiseMainA, noiseMainB, noiseRoofA, noiseRoofB;
    
    
    private static final double MAIN_SCALE_A = 1d / 8d;
    private static final double MAIN_SCALE_B = 1d / 4d;
    private static final double ROOF_SCALE_A = 1d * 2d;
    private static final double ROOF_SCALE_B = 1d / 1d;

    public RuinsChunkGenerator(World w) {
    	//Logger.getGlobal().log(Level.INFO, "[!] Initializing chunk generator...");
        world = w;
        world.setSpawnPoint(new BlockPos(0, 36, 0));
        long seed = world.getSeed();
        random = new Random((seed + 127) * 712);
        
        noiseMainA = new OpenSimplexNoise(random.nextLong());
        noiseMainB = new OpenSimplexNoise(random.nextLong());
        noiseRoofA = new OpenSimplexNoise(random.nextLong());
        noiseRoofB = new OpenSimplexNoise(random.nextLong());
    }
    
    @Override
    public Chunk generateChunk(int x, int z) {
    	
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        //Get heightmap
        double[] heightmap = new double[256];
        double[] roofHeightmap = new double[256];
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	heightmap[x2 * 16 + z2] = noiseMainA.eval((double) ((double)x + ((double)x2 / 16)) * MAIN_SCALE_A, (double) ((double)z + ((double)z2 / 16d)) * MAIN_SCALE_A) / 3 * 2;
            	heightmap[x2 * 16 + z2] += noiseMainB.eval((double) ((double)x + ((double)x2 / 16)) * MAIN_SCALE_B, (double) ((double)z + ((double)z2 / 16d)) * MAIN_SCALE_B) / 3;
            	roofHeightmap[x2 * 16 + z2] = noiseRoofA.eval((double) ((double)x + ((double)x2 / 16)) * ROOF_SCALE_A, (double) ((double)z + ((double)z2 / 16d)) * ROOF_SCALE_A) / 3 * 2;
            	roofHeightmap[x2 * 16 + z2] += noiseRoofB.eval((double) ((double)x + ((double)x2 / 16)) * ROOF_SCALE_B, (double) ((double)z + ((double)z2 / 16d)) * ROOF_SCALE_B) / 3;
            	
            }
    	}
        
        biomesForGeneration = world.getBiomeProvider().getBiomes(biomesForGeneration, x * 16, z * 16, 16, 16);
        NoiseGeneratorPerlin surfaceNoise = new NoiseGeneratorPerlin(random, 4);
        
        double[] depthBuffer = new double[256];
        depthBuffer = surfaceNoise.getRegion(depthBuffer, (x * 16), (z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
        
        //Set base blocks
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	
            	//ground
                int currentHeight = (int) (heightmap[x2 * 16 + z2] * 2D + 30D);
                
                for (int i = currentHeight; i > 1; i--)
                	chunkprimer.setBlockState(x2, i, z2, Blocks.STONE.getDefaultState());
                
                if (random.nextFloat() < 0.25f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.WATER.getDefaultState());
                else if (random.nextFloat() < 0.1f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.LAVA.getDefaultState());
                else chunkprimer.setBlockState(x2, 1, z2, Blocks.BEDROCK.getDefaultState());
                
                //biomes
                BiomeWasteland biome = (BiomeWasteland) biomesForGeneration[x2 * 16 + z2];//Because there's only one biome to begin with
                biome.generateBiomeBlocks(world, random, chunkprimer, x * 16 + x2, z * 16 + z2, depthBuffer[z2 + x2 * 16]);
                
                //roof
                currentHeight = (int) (roofHeightmap[x2 * 16 + z2] * 6D + 250D);
                chunkprimer.setBlockState(x2, 255, z2, Blocks.BEDROCK.getDefaultState());
                for (int i = 254; i >= currentHeight; i--) {
                	chunkprimer.setBlockState(x2, i, z2, Blocks.STONE.getDefaultState()); 
                }
            }
    	}
        
    	//Setup chunk object and return
        Chunk chunk = new Chunk(world, chunkprimer, x, z);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {}

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) { return false; }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) { return ImmutableList.of(); }
    
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) { return null; }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) { return false; }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {}
}
