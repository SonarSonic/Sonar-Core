package sonar.core;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.api.SonarAPI;
import sonar.core.energy.DischargeValues;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.SonarWailaModule;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketBlockInteraction;
import sonar.core.network.PacketByteBufClient;
import sonar.core.network.PacketByteBufServer;
import sonar.core.network.PacketInvUpdate;
import sonar.core.network.PacketRequestSync;
import sonar.core.network.PacketSonarSides;
import sonar.core.network.PacketStackUpdate;
import sonar.core.network.PacketTextField;
import sonar.core.network.PacketTileSync;
import sonar.core.network.SonarCommon;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.network.utils.ISyncTile;
import sonar.core.registries.EnergyProviderRegistry;
import sonar.core.registries.EnergyTypeRegistry;
import sonar.core.registries.FluidProviderRegistry;
import sonar.core.registries.InventoryProviderRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = SonarCore.modid, name = "SonarCore", version = SonarCore.version)
public class SonarCore {

	public static final String modid = "SonarCore";
	public static final String version = "1.1.2";

	@SidedProxy(clientSide = "sonar.core.network.SonarClient", serverSide = "sonar.core.network.SonarCommon")
	public static SonarCommon proxy;

	@Instance(modid)
	public static SonarCore instance;

	public static InventoryProviderRegistry inventoryProviders = new InventoryProviderRegistry();
	public static FluidProviderRegistry fluidProviders = new FluidProviderRegistry();
	public static EnergyProviderRegistry energyProviders = new EnergyProviderRegistry();
	public static EnergyTypeRegistry energyTypes = new EnergyTypeRegistry();
	public static SimpleNetworkWrapper network;

	public static Logger logger = (Logger) LogManager.getLogger(modid);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SonarAPI.init();
		logger.info("Initilised API");
		
		logger.info("Registering Packets");
		registerPackets();
		logger.info("Register Packets");

	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (SonarLoader.wailaLoaded()) {
			SonarWailaModule.register();
			logger.info("Integrated with WAILA");
		} else {
			logger.warn("'WAILA' - unavailable or disabled in config");
		}
		MinecraftForge.EVENT_BUS.register(new SonarEvents());
		logger.info("Registered Events");
		energyTypes.register();		
		inventoryProviders.register();
		fluidProviders.register();
		energyProviders.register();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("Adding Discharge Values");
		DischargeValues.addValues();
		logger.info("Added" + DischargeValues.getPowerList().size() + "Discharge Values");
		for (Map.Entry<ItemStack, Integer> entry : DischargeValues.getPowerList().entrySet()) {
			logger.info("Discharge Values: " + entry.toString() + " = " + entry.getValue());
		}
		logger.info("Registered " + energyTypes.getObjects().size() + " Energy Types");
		logger.info("Registered " + inventoryProviders.getObjects().size() + " Inventory Providers");
		logger.info("Registered " + fluidProviders.getObjects().size() + " Fluid Providers");
		logger.info("Registered " + energyProviders.getObjects().size() + " Energy Providers");
	}

	public static void registerPackets() {
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, 0, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, 1, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, 2, Side.SERVER);
			network.registerMessage(PacketTextField.Handler.class, PacketTextField.class, 3, Side.SERVER);
			network.registerMessage(PacketByteBufClient.Handler.class, PacketByteBufClient.class, 4, Side.CLIENT);
			network.registerMessage(PacketByteBufServer.Handler.class, PacketByteBufServer.class, 5, Side.SERVER);
			network.registerMessage(PacketBlockInteraction.Handler.class, PacketBlockInteraction.class, 6, Side.SERVER);
			network.registerMessage(PacketStackUpdate.Handler.class, PacketStackUpdate.class, 7, Side.CLIENT);
			network.registerMessage(PacketInvUpdate.Handler.class, PacketInvUpdate.class, 8, Side.CLIENT);
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
