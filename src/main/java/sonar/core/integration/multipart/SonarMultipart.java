package sonar.core.integration.multipart;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import mcmultipart.multipart.Multipart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketRequestMultipartSync;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncableList;
import sonar.core.utils.IRemovable;
import sonar.core.utils.IUUIDIdentity;
import sonar.core.utils.IWorldPosition;
import sonar.core.utils.Pair;

public abstract class SonarMultipart extends Multipart implements ISyncableListener, ITickable, INBTSyncable, IWorldPosition, IRemovable {

	public SyncableList syncList = new SyncableList(this);
	public AxisAlignedBB collisionBox = null;
	public boolean wasRemoved = false;
	public boolean isValid = false;
	protected boolean forceSync;
	public boolean isDirty = false;

	public SonarMultipart(AxisAlignedBB collisionBox) {
		super();
		this.collisionBox = collisionBox;
	}

	public SonarMultipart() {
		super();
	}

	public void update() {
		if (!isValid && !wasRemoved) {
			this.validate();
			isValid = true;
		}
		if (isDirty) {
			this.markDirty();
			isDirty = !isDirty;
		}
	}

	public void validate() {
		isValid = true;
	}

	public void invalidate() {
		isValid = false;
	}

	@Override
	public void onRemoved() {
		super.onRemoved();
		invalidate();
		wasRemoved = true;
	}

	@Override
	public void onUnloaded() {
		super.onUnloaded();
		invalidate();
	}

	public UUID getUUID() {
		if (getContainer() != null) {
			return getContainer().getPartID(this);
		}
		return IUUIDIdentity.INVALID_UUID;
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
		if (getContainer() == null) {
			return null;
		}
		return new BlockCoords(getContainer().getPosIn(), getContainer().getWorldIn());
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return this.writeData(tag, SyncType.SAVE);
	}

	@Override
	public final void readFromNBT(NBTTagCompound tag) {
		this.readData(tag, SyncType.SAVE);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncList);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (forceSync && type == SyncType.DEFAULT_SYNC) {
			type = SyncType.SYNC_OVERRIDE;
			forceSync = false;
		}
		NBTHelper.writeSyncParts(nbt, type, syncList, forceSync);
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

	@Override
	public List<ItemStack> getDrops() {
		return Lists.newArrayList(this.getItemStack());
	}

	public abstract ItemStack getItemStack();

	// change it to get the next valid rotation whatever it is;
	public Pair<Boolean, EnumFacing> rotatePart(EnumFacing face, EnumFacing axis) {
		EnumFacing[] valid = getValidRotations();
		if (valid != null) {
			int pos = -1;
			for (int i = 0; i < valid.length; i++) {
				if (valid[i] == face) {
					pos = i;
					break;
				}
			}
			if (pos != -1) {
				int current = pos;
				boolean fullCycle = false;
				while (!fullCycle && getContainer().getPartInSlot(PartSlot.getFaceSlot(valid[current])) != null) {
					current++;
					if (current >= valid.length) {
						current = 0;
					}
					if (current == pos) {
						return new Pair(false, face);
					}
				}
				if (current != -1 && isServer()) {
					face = valid[current];
					return new Pair(true, face);
				}
			}
		}
		return new Pair(false, face);
	}

	public void onSyncPacketRequested(EntityPlayer player) {
	}

	public void requestSyncPacket() {
		SonarCore.network.sendToServer(new PacketRequestMultipartSync(this.getPos(), this.getUUID()));
	}

	public void forceNextSync() {
		forceSync = true;
	}

	public boolean wasRemoved() {
		return wasRemoved;
	}

	/** called when a part is changed */
	@Override
	public void markChanged(IDirtyPart part) {
		if (this.isServer()) {
			syncList.markSyncPartChanged(part);
			isDirty = true;
		}
	}

	/** if this Multipart is in a client world */
	public final boolean isClient() {
		if (getWorld() == null) {
			return FMLCommonHandler.instance().getEffectiveSide().isClient();
		}
		return this.getWorld().isRemote;
	}

	/** if this multipart is in a server world */
	public final boolean isServer() {
		if (getWorld() == null) {
			return FMLCommonHandler.instance().getEffectiveSide().isServer();
		}
		World world = getWorld();
		return !this.getWorld().isRemote;
	}

	/** opens a standard GUI */
	public final void openGui(EntityPlayer player, Object mod) {
		player.openGui(mod, getUUID().hashCode(), getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
	}

	/** opens a GUI using {@link IFlexibleGui} */
	public final void openFlexibleGui(EntityPlayer player, int id) {
		SonarCore.instance.guiHandler.openBasicMultipart(false, getUUID(), player, getWorld(), getPos(), id);
	}

	public final void changeFlexibleGui(EntityPlayer player, int id) {
		SonarCore.instance.guiHandler.openBasicMultipart(true, getUUID(), player, getWorld(), getPos(), id);
	}
}
