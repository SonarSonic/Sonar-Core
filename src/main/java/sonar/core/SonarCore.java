package sonar.core;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sonar.core.api.SonarAPI;
import sonar.core.api.nbt.INBTSyncable;
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
//import sonar.core.network.PacketTextField;
import sonar.core.network.PacketTileSync;
import sonar.core.network.SonarCommon;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.registries.EnergyProviderRegistry;
import sonar.core.registries.EnergyTypeRegistry;
import sonar.core.registries.FluidProviderRegistry;
import sonar.core.registries.InventoryProviderRegistry;
import sonar.core.upgrades.MachineUpgradeRegistry;

@Mod(modid = SonarCore.modid, name = "SonarCore", version = SonarCore.version)
public class SonarCore {

	public static final String modid = "SonarCore";
	public static final String version = "2.0.0";

	@SidedProxy(clientSide = "sonar.core.network.SonarClient", serverSide = "sonar.core.network.SonarCommon")
	public static SonarCommon proxy;

	@Instance(modid)
	public static SonarCore instance;

	public static InventoryProviderRegistry inventoryProviders = new InventoryProviderRegistry();
	public static FluidProviderRegistry fluidProviders = new FluidProviderRegistry();
	public static EnergyProviderRegistry energyProviders = new EnergyProviderRegistry();
	public static EnergyTypeRegistry energyTypes = new EnergyTypeRegistry();
	public static MachineUpgradeRegistry machineUpgrades = new MachineUpgradeRegistry();
	public static SimpleNetworkWrapper network;

	public static Logger logger = (Logger) LogManager.getLogger(modid);

	// common blocks
	public static Block reinforcedStoneBlock, reinforcedStoneBrick, reinforcedDirtBlock, reinforcedDirtBrick, stableStone, stablestonerimmedBlock, stablestonerimmedblackBlock, stableGlass, clearStableGlass;
	//public static Block toughenedStoneBlock, toughenedStoneBrick;
	//public static Block toughenedDirtBlock, toughenedDirtBrick;
	public static Block reinforcedStoneStairs, reinforcedStoneBrickStairs, reinforcedDirtStairs, reinforcedDirtBrickStairs;
	public static Block reinforcedStoneFence, reinforcedStoneBrickFence, reinforcedDirtFence, reinforcedDirtBrickFence;
	public static Block reinforcedStoneGate, reinforcedStoneBrickGate, reinforcedDirtGate, reinforcedDirtBrickGate;
	public static Block reinforcedStoneSlab_half, reinforcedStoneBrickSlab_half, reinforcedDirtSlab_half, reinforcedDirtBrickSlab_half;
	public static Block reinforcedStoneSlab_double, reinforcedStoneBrickSlab_double, reinforcedDirtSlab_double, reinforcedDirtBrickSlab_double;

	public static CreativeTabs tab = new CreativeTabs("SonarCore") {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(reinforcedStoneBlock);
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Initilising API");
		SonarAPI.init();
		logger.info("Initilised API");

		logger.info("Registering Packets");
		registerPackets();
		logger.info("Register Packets");

		logger.info("Registering Blocks");
		SonarBlocks.registerBlocks();
		logger.info("Register Blocks");

		logger.info("Registering Crafting Recipes");
		SonarCrafting.registerCraftingRecipes();
		logger.info("Register Crafting Recipes");

		logger.info("Registering Renderers");
		proxy.registerRenderThings();
		logger.info("Registered Renderers");
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
		machineUpgrades.register();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("Adding Discharge Values");
		DischargeValues.addValues();
		logger.info("Added" + DischargeValues.dischargeList.size() + "Discharge Values");
		for (Map.Entry<ItemStack, Integer> entry : DischargeValues.dischargeList.entrySet()) {
			logger.info("Discharge Values: " + entry.toString());
		}
		logger.info("Registered " + energyTypes.getObjects().size() + " Energy Types");
		logger.info("Registered " + inventoryProviders.getObjects().size() + " Inventory Providers");
		logger.info("Registered " + fluidProviders.getObjects().size() + " Fluid Providers");
		logger.info("Registered " + energyProviders.getObjects().size() + " Energy Providers");
		logger.info("Registered " + machineUpgrades.getMap().size() + " Machine Upgrades");
	}

	public static void registerPackets() {
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, 0, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, 1, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, 2, Side.SERVER);
			//network.registerMessage(PacketTextField.Handler.class, PacketTextField.class, 3, Side.SERVER);
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
			if (!tile.getWorld().isRemote) {
				SonarCore.network.sendToAllAround(new PacketByteBufClient((IByteBufTile) object, tile.getPos(), id), new TargetPoint(tile.getWorld().provider.getDimensionId(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
			} else {
				SonarCore.network.sendToServer(new PacketByteBufServer((IByteBufTile) object, tile.getPos(), id));
			}
		} else {
			TileHandler handler = FMPHelper.getHandler(object);
			if (handler != null && handler instanceof IByteBufTile) {

				if (!tile.getWorld().isRemote) {
					SonarCore.network.sendToAllAround(new PacketByteBufClient((IByteBufTile) handler, tile.getPos(), id), new TargetPoint(tile.getWorld().provider.getDimensionId(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
				} else {
					SonarCore.network.sendToServer(new PacketByteBufServer((IByteBufTile) handler, tile.getPos(), id));
				}
			}
		}
	}

	public static void sendFullSyncAround(TileEntity tile, int spread) {
		Object object = FMPHelper.checkObject(tile);
		if (object != null && object instanceof INBTSyncable) {
			NBTTagCompound tag = new NBTTagCompound();
			((INBTSyncable) object).writeData(tag, SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(new PacketTileSync(tile.getPos(), tag), new TargetPoint(tile.getWorld().provider.getDimensionId(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
			}
		}
	}

}
