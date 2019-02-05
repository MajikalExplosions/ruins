package majikalexplosions.ruins.dimensions;

import java.util.List;
import java.util.Random;
import com.google.common.collect.ImmutableList;

import majikalexplosions.ruins.OpenSimplexNoise;
import majikalexplosions.ruins.biomes.BiomeWasteland;
import majikalexplosions.ruins.buildings.BuildingGenerator;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.structure.template.PlacementSettings;

public class RuinsChunkGenerator implements IChunkGenerator {
	private final World worldObj;
    private Random random;
    private Biome[] biomesForGeneration;
    
    private OpenSimplexNoise noiseMainA, noiseMainB;
    
    private BuildingGenerator buildingGenerator;
    
    private static final double MAIN_SCALE_A = 1d / 8d;
    private static final double MAIN_SCALE_B = 1d / 2d;

    public RuinsChunkGenerator(World worldObj) {
        this.worldObj = worldObj;
        long seed = worldObj.getSeed();
        this.random = new Random((seed + 127) * 712);
        
        noiseMainA = new OpenSimplexNoise(random.nextLong());
        noiseMainB = new OpenSimplexNoise(random.nextLong());
        buildingGenerator = new BuildingGenerator(new OpenSimplexNoise(random.nextLong()), 10);
    }
    
    @Override
    public Chunk generateChunk(int x, int z) {
    	//Logger.getGlobal().log(Level.INFO, "[ !! ] " + x + " " + z);
    	
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        double[] heightmap = new double[256];
        
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	heightmap[x2 * 16 + z2] = noiseMainA.eval((double) ((double)x + ((double)x2 / 16)) * MAIN_SCALE_A, (double) ((double)z + ((double)z2 / 16d)) * MAIN_SCALE_A) / 2;
            	heightmap[x2 * 16 + z2] += noiseMainB.eval((double) ((double)x + ((double)x2 / 16)) * MAIN_SCALE_B, (double) ((double)z + ((double)z2 / 16d)) * MAIN_SCALE_B) / 2;
            }
    	}
        
        double chunkAverageHeight = 0;
        
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	
                int currentHeight = (int) (heightmap[x2 * 16 + z2] * 2D + 30D);//4/3 b/c program only uses 3/4 of range
                
                chunkAverageHeight += currentHeight;
                
                for (int i = currentHeight; i > 1; i--)
                	chunkprimer.setBlockState(x2, i, z2, Blocks.STONE.getDefaultState());
                
                if (random.nextFloat() < 0.25f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.WATER.getDefaultState());
                else if (random.nextFloat() < 0.1f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.LAVA.getDefaultState());
                else chunkprimer.setBlockState(x2, 1, z2, Blocks.BEDROCK.getDefaultState());
            }
    	}
        
        chunkAverageHeight = Math.round(chunkAverageHeight / 256d);
        
        //Setup Biomes
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        NoiseGeneratorPerlin surfaceNoise = new NoiseGeneratorPerlin(random, 4);
        
        double[] depthBuffer = new double[256];
        depthBuffer = surfaceNoise.getRegion(depthBuffer, (x * 16), (z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                BiomeWasteland biome = (BiomeWasteland) biomesForGeneration[0];//Because there's only one biome to begin with
                biome.generateBiomeBlocks(this.worldObj, this.random, chunkprimer, x * 16 + i, z * 16 + j, depthBuffer[j + i * 16]);
            }
        }
        
        //Replace blocks with building blocks
        buildingGenerator.getBuilding(x, z).addBlocksToWorld(worldObj, new BlockPos(x, (chunkAverageHeight), z), (new PlacementSettings()));
        
    	//Setup chunk object and return
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public void populate(int x, int z) {
    	/*
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
        
        
        // Add biome decorations (like flowers, grass, trees, ...)
        biome.decorate(this.worldObj, this.random, blockpos);

        // Make sure animals appropriate to the biome spawn here when the chunk is generated
        WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i + 8, j + 8, 16, 16, this.random);
        */
    }

    @Override
    public boolean generateStructures(Chunk chunkIn, int x, int z) {
        return false;
    }

    @Override
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) { return ImmutableList.of(); }
    
    @Override
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
        return null;
    }

    @Override
    public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
        return false;
    }

    @Override
    public void recreateStructures(Chunk chunkIn, int x, int z) {

    }
}
