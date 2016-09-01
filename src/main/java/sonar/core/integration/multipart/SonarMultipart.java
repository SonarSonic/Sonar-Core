package sonar.core.integration.multipart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mcmultipart.multipart.Multipart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.SonarCore;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.IRemovable;
import sonar.core.utils.IWorldPosition;

public abstract class SonarMultipart extends Multipart implements ITickable, INBTSyncable, IWorldPosition, IRemovable {

	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();
	public AxisAlignedBB collisionBox = null;
	public boolean wasRemoved = false;
	public boolean firstTick = false;
	protected boolean forceSync;

	public SonarMultipart(AxisAlignedBB collisionBox) {
		super();
		this.collisionBox = collisionBox;
	}

	public SonarMultipart() {
		super();
	}

	public void update() {
		if (!firstTick) {
			this.onFirstTick();
			firstTick = true;
		}
		for (ISyncPart part : syncParts) {
			if (part != null && part.hasChanged()) {
				markDirty();
				break;
			}
		}
	}

	public void onFirstTick() {
	}

	public UUID getUUID() {
		if (getContainer() != null) {
			return getContainer().getPartID(this);
		}
		return null;
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		if (collisionBox != null)
			list.add(collisionBox);
	}

	@Override
	public void addCollisionBoxes(AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
		if (collisionBox != null) {
			if (collisionBox.intersectsWith(mask)) {
				list.add(collisionBox);
			}
		}
	}

	public BlockCoords getCoords() {
		return new BlockCoords(getContainer().getPosIn(), getContainer().getWorldIn());
	}

	@Override
	public void onRemoved() {
		wasRemoved = true;
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeData(tag, SyncType.SAVE);
	}

	@Override
	public final void readFromNBT(NBTTagCompound tag) {
		readData(tag, SyncType.SAVE);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, this.syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		NBTHelper.writeSyncParts(nbt, type, this.syncParts, forceSync);
		return nbt;
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

	@Override
	public ItemStack getPickBlock(EntityPlayer player, PartMOP hit) {
		return getItemStack();
	}

	public abstract ItemStack getItemStack();

	public void forceNextSync() {
		forceSync = true;
	}

	public boolean wasRemoved() {
		return wasRemoved;
	}

	public boolean isClient() {
		if (getWorld() == null) {
			return FMLCommonHandler.instance().getEffectiveSide().isClient();
		}
		return this.getWorld().isRemote;
	}

	public boolean isServer() {
		if (getWorld() == null) {
			return FMLCommonHandler.instance().getEffectiveSide().isServer();
		}
		return !this.getWorld().isRemote;
	}

	public void openBasicGui(EntityPlayer player, int id) {
		SonarCore.proxy.openBasicMultipart(getUUID(), player, getWorld(), getPos(), id);
	}
}
