package majikalexplosions.ruins.blocks.rails;


import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;


/*
 * Credits to shadowfacts; I only half know what's going on here.
 * 
 * */
public abstract class BlockRailCustomBase extends BlockRailBase {
	
	public static final PropertyEnum<EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, dir ->
		dir == EnumRailDirection.NORTH_SOUTH || dir == EnumRailDirection.EAST_WEST
	);
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	
	protected BlockRailCustomBase() {
		super(true);
		setHardness(0.2f);
		this.setCreativeTab(CreativeTabs.TRANSPORTATION);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState()
				.withProperty(SHAPE, EnumRailDirection.NORTH_SOUTH)
				.withProperty(FACING, EnumFacing.NORTH)
				.withProperty(POWERED, false));
	}
	
	
	public abstract void initItemModel();
	
	public Item createItemBlock() {
		return new ItemBlock(this).setRegistryName(getRegistryName());
	}
	
	@Nonnull
	@Override
	public IProperty<EnumRailDirection> getShapeProperty() {
		return SHAPE;
	}
	
	@Nonnull
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		EnumFacing facing = EnumFacing.fromAngle(placer.rotationYawHead);
		EnumRailDirection shape = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH ? EnumRailDirection.NORTH_SOUTH : EnumRailDirection.EAST_WEST;
		return getDefaultState().withProperty(FACING, facing).withProperty(SHAPE, shape);
	}
	
	@Override
	public boolean canMakeSlopes(IBlockAccess world, BlockPos pos) {
		return false;
	}
	
	protected void propelMinecart(World world, BlockPos pos, IBlockState state, EntityMinecart minecart) {
		BlockRailBase.EnumRailDirection dir = getRailDirection(world, pos, state, minecart);
		EnumFacing facing = state.getValue(FACING);
		if (dir == BlockRailBase.EnumRailDirection.EAST_WEST) {
			if (facing == EnumFacing.EAST) {
				minecart.motionX += this.getRailMaxSpeed(world, minecart, pos) / 8;
			} else {
				minecart.motionX += -this.getRailMaxSpeed(world, minecart, pos) / 8;
			}
		} else if (dir == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
			if (facing == EnumFacing.SOUTH) {
				minecart.motionZ += this.getRailMaxSpeed(world, minecart, pos) / 8;
			} else {
				minecart.motionZ += -this.getRailMaxSpeed(world, minecart, pos) / 8;
			}
		}
	}
	
	protected List<? extends EntityMinecart> findMinecarts(World world, BlockPos pos) {
		return world.getEntitiesWithinAABB(EntityMinecart.class, getDetectionBox(pos));
	}
	
	protected AxisAlignedBB getDetectionBox(BlockPos pos) {
		return new AxisAlignedBB((double)((float)pos.getX() + 0.2F), (double)pos.getY(), (double)((float)pos.getZ() + 0.2F), (double)((float)(pos.getX() + 1) - 0.2F), (double)((float)(pos.getY() + 1) - 0.2F), (double)((float)(pos.getZ() + 1) - 0.2F));
	}
	
	@Override
	protected void updateState(IBlockState state, World world, BlockPos pos, Block block) {
		boolean flag = state.getValue(POWERED);
		boolean flag1 = world.isBlockPowered(pos) || this.findPoweredRailSignal(world, pos, state, true, 0) || this.findPoweredRailSignal(world, pos, state, false, 0);

		if (flag1 != flag) {
			world.setBlockState(pos, state.withProperty(POWERED, flag1), 3);
			world.notifyNeighborsOfStateChange(pos.down(), this, false);
		}
	}
	
	private boolean findPoweredRailSignal(World world, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_) {
		if (p_176566_5_ >= 8) {
			return false;
		} else {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			BlockRailBase.EnumRailDirection dir = state.getValue(SHAPE);

			switch (dir) {
				case NORTH_SOUTH:

					if (p_176566_4_) {
						++z;
					} else {
						--z;
					}

					break;
				case EAST_WEST:

					if (p_176566_4_) {
						--x;
					} else {
						++x;
					}

					break;
			}

			return isSameRailWithPower(world, new BlockPos(x, y, z), p_176566_4_, p_176566_5_, dir) || isSameRailWithPower(world, new BlockPos(x, y - 1, z), p_176566_4_, p_176566_5_, dir);
		}
	}

	private boolean isSameRailWithPower(World world, BlockPos pos, boolean p_176567_3_, int distance, EnumRailDirection dir) {
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() != this) {
			return false;
		} else {
			EnumRailDirection shape = state.getValue(SHAPE);
			return (dir != EnumRailDirection.EAST_WEST || shape != EnumRailDirection.NORTH_SOUTH && shape != EnumRailDirection.ASCENDING_NORTH && shape != EnumRailDirection.ASCENDING_SOUTH) && ((dir != EnumRailDirection.NORTH_SOUTH || shape != EnumRailDirection.EAST_WEST && shape != EnumRailDirection.ASCENDING_EAST && shape != EnumRailDirection.ASCENDING_WEST) && (state.getValue(POWERED) && (world.isBlockPowered(pos) || findPoweredRailSignal(world, pos, state, p_176567_3_, distance + 1))));
		}
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POWERED, SHAPE, FACING);
	}

	@Nonnull
	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		boolean powered = (meta & 1) == 1;
		EnumRailDirection shape = EnumRailDirection.byMetadata((meta >> 1) & 1);
		EnumFacing facing = EnumFacing.getHorizontal(meta >> 2);

		return getDefaultState()
				.withProperty(SHAPE, shape)
				.withProperty(FACING, facing)
				.withProperty(POWERED, powered);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumRailDirection shape = state.getValue(SHAPE);
		EnumFacing facing = state.getValue(FACING);
		boolean powered = state.getValue(POWERED);
		int meta = powered ? 1 : 0;
		meta |= shape.getMetadata() << 1;
		meta |= facing.getHorizontalIndex() << 2;
		return meta;
	}
}