package sonar.core.integration.multipart;

import java.util.Optional;

import javax.annotation.Nullable;

import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.multipart.MultipartHelper;
import mcmultipart.api.ref.MCMPCapabilities;
import mcmultipart.slot.SlotRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.inventory.ISonarInventory;
import sonar.core.inventory.ISonarInventoryTile;
import sonar.core.network.PacketRequestMultipartSync;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.SyncableList;

public class TileInventoryMultipart extends TileSonarMultipart implements ISonarInventoryTile {

	protected ISonarInventory inv;

	public TileInventoryMultipart() {}

	public ISonarInventory inv() {
		return inv;
	}

}
