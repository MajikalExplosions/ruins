package majikalexplosions.ruins.tileentity;

import majikalexplosions.ruins.blocks.rails.BlockDelayedDetectorRail;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityDelayedDetectorRail extends TileEntity {
	
	public int delay;
	public boolean triggered;
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		if (newState.getBlock().getClass().equals(BlockDelayedDetectorRail.class) && oldState.getBlock().getClass().equals(BlockDelayedDetectorRail.class)) return false;
		return true;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("delay", delay);
		tag.setBoolean("triggered", triggered);
		this.markDirty();
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		delay = tag.getInteger("delay");
		triggered = tag.getBoolean("triggered");
	}
	
	
}
