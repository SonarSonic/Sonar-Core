package sonar.core.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class SonarPackets {

	public static SimpleNetworkWrapper network;

	public static void registerPackets() {
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketMachineButton.Handler.class, PacketMachineButton.class, 0, Side.SERVER);
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, 1, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, 2, Side.CLIENT);
			network.registerMessage(PacketInventorySync.Handler.class, PacketInventorySync.class, 3, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, 4, Side.SERVER);
			network.registerMessage(PacketTextField.Handler.class, PacketTextField.class, 5, Side.SERVER);
			network.registerMessage(PacketByteBuf.Handler.class, PacketByteBuf.class, 6, Side.CLIENT);
			network.registerMessage(PacketByteBuf.Handler.class, PacketByteBuf.class, 7, Side.SERVER);
		}
	}

	public static void sendPacketAround(TileEntity tile, int spread, int id) {
		Object object = FMPHelper.checkObject(tile);
		if (object != null && object instanceof IByteBufTile) {
			if (!tile.getWorldObj().isRemote)
				SonarPackets.network.sendToAllAround(new PacketByteBuf(tile, id), new TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, spread));
			else
				SonarPackets.network.sendToServer(new PacketByteBuf(tile, id));

		}
	}

	public static void sendFullSyncAround(TileEntity tile, int spread) {
		Object object = FMPHelper.checkObject(tile);
		if (object != null && object instanceof ISyncTile) {
			NBTTagCompound tag = new NBTTagCompound();
			((ISyncTile) object).writeData(tag, SyncType.SYNC);

			if (!tag.hasNoTags()) {
				SonarPackets.network.sendToAllAround(new PacketTileSync(tile.xCoord, tile.yCoord, tile.zCoord, tag), new TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, spread));
			}
		}
	}

}
