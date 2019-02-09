package majikalexplosions.ruins.blocks.rails;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


import com.google.common.base.Predicate;

import majikalexplosions.ruins.RuinsMain;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDelayedDetectorRail extends BlockRailCustomBase {
	
	public static final int DELAY = 150;//20 is one second
	public static final int ACTIVATION_TIME = 50;
	private int delay;
	private boolean triggered;
	
	public void initItemModel() {
		RuinsMain.proxy.registerItemModel(this, 0, getRegistryName().getResourcePath());
	}
	
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockDelayedDetectorRail()
    {
    	setRegistryName("delayed_detector_rail");
		setUnlocalizedName(getRegistryName().toString());
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower(IBlockState state)
    {
        return true;
    }
    
    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote && ! triggered)
        {
        	this.updatePoweredState(worldIn, pos, state);
        }
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            this.updatePoweredState(worldIn, pos, state);
        }
    }

    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return ((Boolean)blockState.getValue(POWERED)).booleanValue() ? 15 : 0;
    }

    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        if (!((Boolean)blockState.getValue(POWERED)).booleanValue())
        {
            return 0;
        }
        else
        {
            return side == EnumFacing.UP ? 15 : 0;
        }
    }

    private void updatePoweredState(World worldIn, BlockPos pos, IBlockState state)
    {
        boolean isPowered = ((Boolean)state.getValue(POWERED)).booleanValue();
        
        List<EntityMinecart> list = this.<EntityMinecart>findMinecarts(worldIn, pos, EntityMinecart.class);
        
        boolean hasMinecart = ! list.isEmpty();
        Logger.getGlobal().log(Level.INFO, "[Debug] " + triggered + " " + delay);
        if (! triggered && hasMinecart) {//start timer
        	delay = DELAY;
        	triggered = true;
        	
        	worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            this.updateConnectedRails(worldIn, pos, state, false);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            
            worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
        else if (triggered) {
        	delay -= this.tickRate(worldIn);
        	if (delay < 0 - ACTIVATION_TIME) {
        		triggered = false;
        		worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
                this.updateConnectedRails(worldIn, pos, state, false);
                worldIn.notifyNeighborsOfStateChange(pos, this, false);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
        	}
        	else if (delay > 0) {
        		worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        	}
        	else if (delay <= 0) {
        		worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
                this.updateConnectedRails(worldIn, pos, state, true);
                worldIn.notifyNeighborsOfStateChange(pos, this, false);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
        		worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        	}
        }
        /*
        if (hasMinecart && ! isPowered) {
        	
        }

        if (!hasMinecart && isPowered)//if there is no minecart and it's on
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            this.updateConnectedRails(worldIn, pos, state, false);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            this.updateConnectedRails(worldIn, pos, state, true);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }
        
        if (hasMinecart)//if there's a minecart
        {
            worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
        */
    }

    protected void updateConnectedRails(World worldIn, BlockPos pos, IBlockState state, boolean powered)
    {
        BlockRailBase.Rail blockrailbase$rail = new BlockRailBase.Rail(worldIn, pos, state);

        for (BlockPos blockpos : blockrailbase$rail.getConnectedRails())
        {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate != null)
            {
                iblockstate.neighborChanged(worldIn, blockpos, iblockstate.getBlock(), pos);
            }
        }
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.updatePoweredState(worldIn, pos, state);
    }

    protected <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> clazz, Predicate<Entity>... filter)
    {
        AxisAlignedBB axisalignedbb = this.getDectectionBox(pos);
        return filter.length != 1 ? worldIn.getEntitiesWithinAABB(clazz, axisalignedbb) : worldIn.getEntitiesWithinAABB(clazz, axisalignedbb, filter[0]);
    }

    protected AxisAlignedBB getDetectionBox(BlockPos pos)
    {
        float f = 0.2F;
        return new AxisAlignedBB((double)((float)pos.getX() + 0.2F), (double)pos.getY(), (double)((float)pos.getZ() + 0.2F), (double)((float)(pos.getX() + 1) - 0.2F), (double)((float)(pos.getY() + 1) - 0.2F), (double)((float)(pos.getZ() + 1) - 0.2F));
    }
    
	/*
	public static final int DELAY = 100;//waits for 5 seconds
	public static final int ACTIVATION_TIME = 20;
	private int delay;
	private boolean triggered;
	
	
	//TODO change this
	public BlockDelayedDetectorRail() {
		setRegistryName("delayed_detector_rail");
		setUnlocalizedName(getRegistryName().toString());
		triggered = false;
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityMinecart) {
			if (delay >= 0 - ACTIVATION_TIME) delay -= tickRate(world);
			if (! triggered) {
				triggered = true;
				delay = DELAY;
			}
			world.scheduleUpdate(new BlockPos(pos), this, tickRate(world));
		}
	}
	
	@Override
	protected void updateState(IBlockState state, World world, BlockPos pos, Block block) {
		boolean flag = state.getValue(POWERED);
		Logger.getGlobal().log(Level.INFO, "[Debug] " + flag + " " + delay);
		boolean flag1 = world.isBlockPowered(pos) || this.findPoweredRailSignal(world, pos, state, true, 0) || this.findPoweredRailSignal(world, pos, state, false, 0);
		if (delay <= 0 - ACTIVATION_TIME) {
			triggered = false;
			world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            this.updateConnectedRails(world, pos, state, false);
			world.notifyNeighborsOfStateChange(pos, this, false);
            world.notifyNeighborsOfStateChange(pos.down(), this, false);
            world.markBlockRangeForRenderUpdate(pos, pos);
		}
		if (flag1 != flag) {
			world.setBlockState(pos, state.withProperty(POWERED, flag1), 3);
			world.notifyNeighborsOfStateChange(pos.down(), this, false);
			if (triggered && delay <= 0) {
				world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
	            this.updateConnectedRails(world, pos, state, false);
				world.notifyNeighborsOfStateChange(pos, this, false);
	            world.notifyNeighborsOfStateChange(pos.down(), this, false);
	            world.markBlockRangeForRenderUpdate(pos, pos);
			}			
		}
	}
	
	protected void updateConnectedRails(World worldIn, BlockPos pos, IBlockState state, boolean powered)
    {
        BlockRailBase.Rail blockrailbase$rail = new BlockRailBase.Rail(worldIn, pos, state);

        for (BlockPos blockpos : blockrailbase$rail.getConnectedRails())
        {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate != null)
            {
                iblockstate.neighborChanged(worldIn, blockpos, iblockstate.getBlock(), pos);
            }
        }
    }
    */
}
