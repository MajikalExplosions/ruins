package majikalexplosions.ruins.blocks.rails;


import majikalexplosions.ruins.RuinsMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockOneWayRail extends BlockRailCustomBase {
	
	
	public BlockOneWayRail() {
		setRegistryName("one_way_powered_rail");
		setUnlocalizedName(getRegistryName().toString());
	}
	
	public void initItemModel() {
		RuinsMain.proxy.registerItemModel(this, 0, getRegistryName().getResourcePath());
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityMinecart) {
			EntityMinecart minecart = (EntityMinecart)entity;
			boolean powered = state.getValue(POWERED);
			if (powered) {
				propelMinecart(world, pos, state, minecart);
			} else {
				if (minecart.motionX > this.getRailMaxSpeed(world, minecart, pos) / 8) minecart.motionX -= this.getRailMaxSpeed(world, minecart, pos) / 8;
				else if (minecart.motionX < -this.getRailMaxSpeed(world, minecart, pos) / 8) minecart.motionX += this.getRailMaxSpeed(world, minecart, pos) / 8;
				else minecart.motionX = 0;
				minecart.motionY = 0;
				if (minecart.motionZ > this.getRailMaxSpeed(world, minecart, pos) / 8) minecart.motionZ -= this.getRailMaxSpeed(world, minecart, pos) / 8;
				else if (minecart.motionZ < -this.getRailMaxSpeed(world, minecart, pos) / 8) minecart.motionZ += this.getRailMaxSpeed(world, minecart, pos) / 8;
				else minecart.motionZ = 0;
				world.scheduleUpdate(new BlockPos(pos), this, tickRate(world));
			}
		}
	}
}
