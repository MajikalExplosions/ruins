package majikalexplosions.ruins.dimensions;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import majikalexplosions.ruins.OpenSimplexNoise;
import majikalexplosions.ruins.biomes.BiomeWasteland;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class RuinsChunkGenerator implements IChunkGenerator {
	private final World worldObj;
    private Random random;
    private Biome[] biomesForGeneration;

    private List<Biome.SpawnListEntry> mobs;
    
    //private NormalTerrainGenerator terraingen = new NormalTerrainGenerator();
    
    private NoiseGeneratorOctaves pNoise;
    private NoiseGeneratorPerlin rNoise;
    
    private OpenSimplexNoise osNoiseOct1, osNoiseOct2, osNoiseOct3, osNoiseOct4;

    public RuinsChunkGenerator(World worldObj) {
        this.worldObj = worldObj;
        long seed = worldObj.getSeed();
        this.random = new Random((seed + 127) * 712);
        
        //terraingen.setup(worldObj, random);
        
        //pNoise = new NoiseGeneratorOctaves(random, 4);
        //rNoise = new NoiseGeneratorPerlin(random, 4);
        
        osNoiseOct1 = new OpenSimplexNoise(random.nextLong());
        osNoiseOct2 = new OpenSimplexNoise(random.nextLong());
        osNoiseOct3 = new OpenSimplexNoise(random.nextLong());
        osNoiseOct4 = new OpenSimplexNoise(random.nextLong());
    }
    
    @Override
    public Chunk generateChunk(int x, int z) {
    	//Logger.getGlobal().log(Level.INFO, "[ !! ] " + x + " " + z);
    	
        ChunkPrimer chunkprimer = new ChunkPrimer();
        
        double[] heightmap = new double[256];
        
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	
            	double o1 = osNoiseOct1.eval((double) ((double)x + ((double)x2 / 16d)) / 8d, (double) ((double)z + ((double)z2 / 16d)) / 8d);//4d is the scale
            	double o2 = osNoiseOct2.eval((double) ((double)x + ((double)x2 / 16d)) / 4d, (double) ((double)z + ((double)z2 / 16d)) / 4d);
            	double o3 = osNoiseOct3.eval((double) ((double)x + ((double)x2 / 16d)) / 2d, (double) ((double)z + ((double)z2 / 16d)) / 2d);
            	
            	double o4 = osNoiseOct4.eval((double) ((double)x + ((double)x2 / 16d)) / 8d, (double) ((double)z + ((double)z2 / 16d)) / 8d);
            	
            	heightmap[x2 * 16 + z2] = (o1 / 2d) + (o2 / 4d) + (o3 / 8d);
            	heightmap[x2 * 16 + z2] *= 8d / 7d;
            	//heightmap[x2 * 16 + z2] *= o4 * 0.9d + 0.1d;//Will this make the terrain flatter in certain areas?  no it won't
            	heightmap[x2 * 16 + z2] -= 0.5;
            }
    	}
        
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	
                int currentHeight = (int) (heightmap[x2 * 16 + z2] * 12D + 32D);//4/3 b/c program only uses 3/4 of range
                for (int i = currentHeight; i > 1; i--)
                	chunkprimer.setBlockState(x2, i, z2, Blocks.STONE.getDefaultState());
                
                if (random.nextFloat() < 0.25f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.WATER.getDefaultState());
                else if (random.nextFloat() < 0.1f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.LAVA.getDefaultState());
                else chunkprimer.setBlockState(x2, 1, z2, Blocks.BEDROCK.getDefaultState());
            }
    	}
        
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
    	
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        
        chunk.generateSkylightMap();
        return chunk;
    }
    
    /*
    @Override
    public Chunk generateChunk(int x, int z) {
    	Logger.getGlobal().log(Level.INFO, "[ !! ] " + x + " " + z);
    	
        ChunkPrimer chunkprimer = new ChunkPrimer();
        double[] heightmap = new double[256];
        
        
        heightmap = pNoise.generateNoiseOctaves(heightmap, x * 4, 0, z * 4, 16, 1, 16, 8.55515D, 4.277575D, 8.55515D);//what's the range?
        //heightmap = rNoise.getRegion(heightmap, x * 16, z * 16, 16, 16, 0.0625D, 0.0625D, 1.0D);
        
        double max = 0;
        for (int x2 = 0; x2 < 16; x2++) {
            for (int z2 = 0; z2 < 16; z2++) {
            	//Logger.getGlobal().log(Level.INFO, "[ !! ] " + String.valueOf(heightmap[x2 * 16 + z2]));
            	if (heightmap[x2 * 16 + z2] > max) max = heightmap[x2 * 16 + z2];
            	
            	heightmap[x2 * 16 + z2] = (heightmap[x2 * 16 + z2] + 100) / 200;
            	
                int currentHeight = (int) (heightmap[x2 * 16 + z2] * 4D + 28D);
                for (int i = currentHeight; i > 1; i--)
                	chunkprimer.setBlockState(x2, i, z2, Blocks.STONE.getDefaultState());
                
                if (random.nextFloat() < 0.333f)
                	chunkprimer.setBlockState(x2, 1, z2, Blocks.BEDROCK.getDefaultState());
                else chunkprimer.setBlockState(x2, 1, z2, Blocks.WATER.getDefaultState());
            }
    	}
        Logger.getGlobal().log(Level.INFO, "[ !!! ] " + max);
        
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
        //this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        */
        /*
        // Setup biomes for terraingen
        
        terraingen.setBiomesForGeneration(biomesForGeneration);
        terraingen.generate(x, z, chunkprimer);

        // Setup biomes again for actual biome decoration
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        // This will replace stone with the biome specific stones
        */
        //terraingen.replaceBiomeBlocks(x, z, chunkprimer, this, biomesForGeneration);
    	/*
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        
        chunk.generateSkylightMap();
        return chunk;
    }
	*/

    @Override
    public void populate(int x, int z) {
        int i = x * 16;
        int j = z * 16;
        BlockPos blockpos = new BlockPos(i, 0, j);
        Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
        
        /*
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
    public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
        // If you want normal creatures appropriate for this biome then uncomment the
        // following two lines:
//        Biome biome = this.worldObj.getBiome(pos);
//        return biome.getSpawnableList(creatureType);

        if (creatureType == EnumCreatureType.MONSTER){
            return mobs;
        }
        return ImmutableList.of();

    }
    
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
