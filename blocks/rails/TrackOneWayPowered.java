package majikalexplosions.ruins.blocks.rails;

/*------------------------------------------------------------------------------
Copyright (c) CovertJaguar, 2011-2019
http://railcraft.info
This code is the property of CovertJaguar
and may only be used with explicit written
permission unless otherwise specified on the
license page at http://railcraft.info/wiki/info:license.
-----------------------------------------------------------------------------*/

import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrackOneWayPowered extends TrackPowered implements ITrackReversible {

    private static final double LOSS_FACTOR = 0.49D;
    private boolean reversed;

    @Override
    public TrackKits getTrackKitContainer() {
        return TrackKits.ONE_WAY;
    }

    @Override
    public int getRenderState() {
        int state = isPowered() ? 1 : 0;
        if (isReversed())
            state += 2;
        return state;
    }

    @Override
    public void onMinecartPass(EntityMinecart cart) {
        EnumRailDirection dir = getRailDirectionRaw();
        if (isPowered()) {
            if (TrackShapeHelper.isEastWest(dir)) {
                if (isReversed() ? cart.motionX > 0.0D : cart.motionX < 0.0D) {
                    double distX = cart.posX - (getPos().getX() + 0.5D);
                    if (isReversed() ? distX > 0.01 : distX < -0.01) {
                        cart.setPosition(getPos().getX() + 0.5D, cart.posY, cart.posZ);
                    }
                    if (!isReversed()) {
                        cart.motionX = Math.abs(cart.motionX) * LOSS_FACTOR;
                    } else {
                        cart.motionX = -Math.abs(cart.motionX) * LOSS_FACTOR;
                    }
                }
            } else if (TrackShapeHelper.isNorthSouth(dir)) {
                if (isReversed() ? cart.motionZ < 0.0D : cart.motionZ > 0.0D) {
                    double distZ = cart.posZ - (getPos().getZ() + 0.5D);
                    if (isReversed() ? distZ < -0.01 : distZ > 0.01) {
                        cart.setPosition(cart.posX, cart.posY, getPos().getZ() + 0.5D);
                    }
                    if (isReversed()) {
                        cart.motionZ = Math.abs(cart.motionZ) * LOSS_FACTOR;
                    } else {
                        cart.motionZ = -Math.abs(cart.motionZ) * LOSS_FACTOR;
                    }
                }
            }
        }
    }

    @Override
    public boolean isReversed() {
        return reversed;
    }

    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setBoolean("direction", reversed);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        reversed = nbttagcompound.getBoolean("direction");
    }

    @Override
    public void writePacketData(DataOutputStream data) throws IOException {
        super.writePacketData(data);
        data.writeBoolean(reversed);
    }

    @Override
    public void readPacketData(DataInputStream data) throws IOException {
        super.readPacketData(data);
        reversed = data.readBoolean();

        markBlockNeedsUpdate();
    }
}
