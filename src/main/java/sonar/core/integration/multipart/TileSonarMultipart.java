package sonar.core.integration.multipart;

import java.util.Optional;

import javax.annotation.Nullable;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.ref.MCMPCapabilities;
import mcmultipart.slot.SlotRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketRequestMultipartSync;
import sonar.core.utils.IWorldTile;

public class TileSonarMultipart extends TileEntitySonar implements IMultipartTile, ITickable, IWorldTile {

	public IPartInfo info;

	public Optional<IPartInfo> getInfo() {
		return Optional.ofNullable(info);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		loaded = true; // Multiparts are added again, this makes sure onFirstTick is called again
	}

	public void setPartInfo(IPartInfo info) {
		this.info = info;
	}

	public int getSlotID() {
		return info == null ? -1 : SlotRegistry.INSTANCE.getSlotID(info.getSlot());
	}

	//// UPDATE \\\\

	public void markBlockForUpdate() {
		if (isServer()) {
			markDirty();
			SonarMultipartHelper.sendMultipartSyncAround(this, 128);
		} else {
			getWorld().markBlockRangeForRenderUpdate(pos, pos);
			getWorld().getChunkFromBlockCoords(getPos()).setModified(true);
		}
	}

	//// PACKETS \\\\

	public IMessage createSyncPacket(NBTTagCompound tag, SyncType type) {
		return SonarMultipartHelper.buildSyncPacket(this, tag, type);
	}

	public void requestSyncPacket() {
		int slotID = getSlotID();
		if (slotID == -1) {
			super.requestSyncPacket();
		} else {
			SonarCore.network.sendToServer(new PacketRequestMultipartSync(getPos(), slotID));
		}
	}

	public void sendSyncPacket() {
		if (!this.getWorld().isRemote) {
			SonarMultipartHelper.sendMultipartSyncAround(this, 64);
		} else {
			SonarMultipartHelper.sendMultipartSyncToServer(this);
		}
	}

	public void sendByteBufPacket(int id) {
		if (!this.getWorld().isRemote) {
			SonarMultipartHelper.sendMultipartPacketAround(this, id, 64);
		} else {
			SonarMultipartHelper.sendMultipartPacketToServer(this, id);
		}
	}

	public final void openGui(EntityPlayer player, Object mod) {
		player.openGui(mod, getSlotID(), getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
	}

	/** opens a GUI using {@link IFlexibleGui} */
	public final void openFlexibleGui(EntityPlayer player, int id) {
		SonarCore.instance.guiHandler.openBasicMultipart(false, getSlotID(), player, getWorld(), getPos(), id);
	}

	public final void changeFlexibleGui(EntityPlayer player, int id) {
		SonarCore.instance.guiHandler.openBasicMultipart(true, getSlotID(), player, getWorld(), getPos(), id);
	}

	//// CAPABILTIES \\\\
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (capability == MCMPCapabilities.MULTIPART_TILE) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == MCMPCapabilities.MULTIPART_TILE) {
			return (T) this;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public World getActualWorld() {
		return (info == null || info.getContainer() == null) ? getWorld() : info.getActualWorld();
	}

	@Override
	public World getPartWorld() {
		return (info == null || info.getContainer() == null) ? getWorld() : info.getPartWorld();
	}
}
