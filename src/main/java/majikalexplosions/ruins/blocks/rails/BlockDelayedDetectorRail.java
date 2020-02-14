package majikalexplosions.ruins.blocks.rails;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

import majikalexplosions.ruins.RuinsMain;
import majikalexplosions.ruins.tileentity.TileEntityDelayedDetectorRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDelayedDetectorRail extends BlockRailCustomBase {
	
	public static final int BASE_DELAY = 70;//10 is one second
	public static final int ACTIVATION_TIME = 30;
	
	public void initItemModel() {
		RuinsMain.proxy.registerItemModel(this, 0, getRegistryName().getResourcePath());
	}
	
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
    	TileEntityDelayedDetectorRail te = (TileEntityDelayedDetectorRail)worldIn.getTileEntity(pos);
        if (!worldIn.isRemote && ! te.triggered)
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
        List<? extends EntityMinecart> list = this.<EntityMinecart>findMinecarts(worldIn, pos);
        boolean hasMinecart = ! list.isEmpty();
        
        TileEntityDelayedDetectorRail te = (TileEntityDelayedDetectorRail)worldIn.getTileEntity(pos);
        
        
        if (! te.triggered && hasMinecart) {//start timer
        	te.delay = BASE_DELAY + ACTIVATION_TIME;
        	te.triggered = true;
        	worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
        	
            this.updateConnectedRails(worldIn, pos, state, false);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            
            worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        }
        else if (te.triggered) {
        	
        	te.delay = te.delay - this.tickRate(worldIn);
        	
        	if (te.delay <= 0) {
        		te.triggered = false;
        		worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
                this.updateConnectedRails(worldIn, pos, state, false);
                worldIn.notifyNeighborsOfStateChange(pos, this, false);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
        	}
        	else if (te.delay > ACTIVATION_TIME) {
        		worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
        	}
        	else if (te.delay <= ACTIVATION_TIME) {
        		worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
                this.updateConnectedRails(worldIn, pos, state, true);
                worldIn.notifyNeighborsOfStateChange(pos, this, false);
                worldIn.notifyNeighborsOfStateChange(pos.down(), this, false);
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
        		worldIn.scheduleUpdate(new BlockPos(pos), this, this.tickRate(worldIn));
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

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.updatePoweredState(worldIn, pos, state);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
    	return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
    	return new TileEntityDelayedDetectorRail();
    }
}
