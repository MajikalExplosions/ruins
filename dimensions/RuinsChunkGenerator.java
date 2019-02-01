package majikalexplosions.ruins.dimensions;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
    
    private NormalTerrainGenerator terraingen = new NormalTerrainGenerator();
    
    private NoiseGeneratorOctaves pNoise;

    public RuinsChunkGenerator(World worldObj) {
        this.worldObj = worldObj;
        long seed = worldObj.getSeed();
        this.random = new Random((seed + 101) * 12719);
        //terraingen.setup(worldObj, random);
        pNoise = new NoiseGeneratorOctaves(random, 8);
    }

    @Override
    public Chunk generateChunk(int x, int z) {
        ChunkPrimer chunkprimer = new ChunkPrimer();
        double[] heightmap = new double[256];
        Logger.getGlobal().log(Level.INFO, "[ !! ] " + x + " " + z);
        heightmap = pNoise.generateNoiseOctaves(heightmap, x, 0, z, 16, 1, 16, 1024.19D, 1024.19D, 1024.19D);//what's the range?
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
                chunkprimer.setBlockState(x2, 0, z2, Blocks.BEDROCK.getDefaultState());
            }
    	}
        Logger.getGlobal().log(Level.INFO, "[ !!! ] " + max);
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        double[] depthBuffer = new double[256];
        NoiseGeneratorPerlin surfaceNoise = new NoiseGeneratorPerlin(random, 4);
        depthBuffer = surfaceNoise.getRegion(depthBuffer, (x * 16), (z * 16), 16, 16, 0.0625D, 0.0625D, 1.0D);
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                BiomeWasteland biome = (BiomeWasteland) biomesForGeneration[0];//Because there's only one biome to begin with
                biome.generateBiomeBlocks(this.worldObj, this.random, chunkprimer, x * 16 + i, z * 16 + j, depthBuffer[j + i * 16]);
            }
        }
        //this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        /*
        // Setup biomes for terraingen
        
        terraingen.setBiomesForGeneration(biomesForGeneration);
        terraingen.generate(x, z, chunkprimer);

        // Setup biomes again for actual biome decoration
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        // This will replace stone with the biome specific stones
        */
        //terraingen.replaceBiomeBlocks(x, z, chunkprimer, this, biomesForGeneration);
        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        
        chunk.generateSkylightMap();
        return chunk;
    }

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
