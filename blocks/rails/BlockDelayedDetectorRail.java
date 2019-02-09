package majikalexplosions.ruins.blocks.rails;

import majikalexplosions.ruins.RuinsMain;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDelayedDetectorRail extends BlockRailCustomBase {
	
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
	
	public void initItemModel() {
		RuinsMain.proxy.registerItemModel(this, 0, getRegistryName().getResourcePath());
	}
	
	@Override
	public float getRailMaxSpeed(World world, net.minecraft.entity.item.EntityMinecart cart, BlockPos pos)
    {
        return 1.2f;
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
		boolean flag1 = world.isBlockPowered(pos) || this.findPoweredRailSignal(world, pos, state, true, 0) || this.findPoweredRailSignal(world, pos, state, false, 0);
		if (delay <= 0 - ACTIVATION_TIME) {
			triggered = false;
		}
		else if (flag1 != flag && triggered && delay <= 0) {
			world.setBlockState(pos, state.withProperty(POWERED, flag1), 3);
			world.notifyNeighborsOfStateChange(pos.down(), this, false);
		}
	}
}
