package sonar.core;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.energy.DischargeValues;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.SonarWailaModule;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketByteBufClient;
import sonar.core.network.PacketByteBufServer;
import sonar.core.network.PacketInventorySync;
import sonar.core.network.PacketMachineButton;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketSonarSides;
import sonar.core.network.PacketTextField;
import sonar.core.network.PacketTileSync;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.network.utils.ISyncTile;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = SonarCore.modid, name = "SonarCore", version = SonarCore.version)
public class SonarCore {

	public static final String modid = "SonarCore";
	public static final String version = "1.0.5";

	@Instance(modid)
	public static SonarCore instance;

	public static SimpleNetworkWrapper network;

	public static Logger logger = (Logger) LogManager.getLogger(modid);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Registering Packets");
		registerPackets();
		logger.info("Register Packets");

		DischargeValues.addValues();
		logger.info("Added Discharge Values");

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (SonarAPI.wailaLoaded()) {
			SonarWailaModule.register();
			logger.info("Integrated with WAILA");
		} else {
			logger.warn("'WAILA' - unavailable or disabled in config");
		}
	}

	public static void registerPackets() {
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketMachineButton.Handler.class, PacketMachineButton.class, 0, Side.SERVER);
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, 1, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, 2, Side.CLIENT);
			network.registerMessage(PacketInventorySync.Handler.class, PacketInventorySync.class, 3, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, 4, Side.SERVER);
			network.registerMessage(PacketTextField.Handler.class, PacketTextField.class, 5, Side.SERVER);
			network.registerMessage(PacketByteBufClient.HandlerClient.class, PacketByteBufClient.class, 6, Side.CLIENT);
			network.registerMessage(PacketByteBufServer.HandlerServer.class, PacketByteBufServer.class, 7, Side.SERVER);
		}
	}

	public static void sendPacketAround(TileEntity tile, int spread, int id) {
		Object object = FMPHelper.checkObject(tile);

		if (object != null && object instanceof IByteBufTile) {
			if (!tile.getWorldObj().isRemote) {
				SonarCore.network.sendToAllAround(new PacketByteBufClient((IByteBufTile) object, tile.xCoord, tile.yCoord, tile.zCoord, id), new TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, spread));
			} else {
				SonarCore.network.sendToServer(new PacketByteBufServer((IByteBufTile) object, tile.xCoord, tile.yCoord, tile.zCoord, id));
			}
		} else {
			TileHandler handler = FMPHelper.getHandler(object);
			if (handler != null && handler instanceof IByteBufTile) {

				if (!tile.getWorldObj().isRemote) {
					SonarCore.network.sendToAllAround(new PacketByteBufClient((IByteBufTile) handler, tile.xCoord, tile.yCoord, tile.zCoord, id), new TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, spread));
				} else {
					SonarCore.network.sendToServer(new PacketByteBufServer((IByteBufTile) handler, tile.xCoord, tile.yCoord, tile.zCoord, id));
				}
			}
		}
	}

	public static void sendFullSyncAround(TileEntity tile, int spread) {
		Object object = FMPHelper.checkObject(tile);
		if (object != null && object instanceof ISyncTile) {
			NBTTagCompound tag = new NBTTagCompound();
			((ISyncTile) object).writeData(tag, SyncType.SYNC);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(new PacketTileSync(tile.xCoord, tile.yCoord, tile.zCoord, tag), new TargetPoint(tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, spread));
			}
		}
	}

}
