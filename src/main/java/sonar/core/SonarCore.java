package sonar.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.core.api.SonarAPI;
import sonar.core.api.energy.IItemEnergyHandler;
import sonar.core.api.energy.ITileEnergyHandler;
import sonar.core.api.fluids.ISonarFluidHandler;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.handlers.energy.DischargeValues;
import sonar.core.handlers.planting.PlantingHandler;
import sonar.core.helpers.ASMLoader;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.SonarLoader;
import sonar.core.integration.SonarWailaModule;
import sonar.core.network.*;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.upgrades.MachineUpgradeRegistry;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Mod(modid = SonarConstants.MODID, name = SonarConstants.NAME, version = SonarConstants.VERSION, acceptedMinecraftVersions = SonarConstants.ACCEPTED_MC_VERSION, dependencies = SonarConstants.DEPENDENCIES)
public class SonarCore {

	@SidedProxy(clientSide = "sonar.core.network.SonarClient", serverSide = "sonar.core.network.SonarCommon")
	public static SonarCommon proxy;

	@Instance(SonarConstants.MODID)
	public static SonarCore instance;

	public static List<ISonarFluidHandler> fluidHandlers;
	public static List<ITileEnergyHandler> tileEnergyHandlers;
	public static List<IItemEnergyHandler> itemEnergyHandlers;
	public static SimpleNetworkWrapper network;
	public FlexibleGuiHandler guiHandler = new FlexibleGuiHandler();
	public MachineUpgradeRegistry machine_upgrades = new MachineUpgradeRegistry();
	public PlantingHandler planting_handler = new PlantingHandler();
	public DischargeValues dischargeValues = new DischargeValues();

	public static Logger logger = (Logger) LogManager.getLogger(SonarConstants.MODID);

	// base blocks
	public static Block reinforcedStoneBlock, reinforcedStoneBrick, reinforcedDirtBlock, reinforcedDirtBrick, stableGlass, clearStableGlass;
	public static Block[] stableStone = new Block[16], stablestonerimmedBlock = new Block[16], stablestonerimmedblackBlock = new Block[16];
	public static Block reinforcedStoneStairs, reinforcedStoneBrickStairs, reinforcedDirtStairs, reinforcedDirtBrickStairs;
	public static Block reinforcedStoneFence, reinforcedStoneBrickFence, reinforcedDirtFence, reinforcedDirtBrickFence;
	public static Block reinforcedStoneGate, reinforcedStoneBrickGate, reinforcedDirtGate, reinforcedDirtBrickGate;

	public static final Random rand = new Random();

	public static CreativeTabs tab = new CreativeTabs("SonarCore") {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Item.getItemFromBlock(reinforcedStoneBlock));
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Initialising API");
		SonarAPI.init();
		logger.info("Initialised API");

		logger.info("Registering Blocks");
		SonarBlocks.registerBlocks();
		logger.info("Loaded Blocks");

		logger.info("Registering Crafting Recipes");
		SonarCrafting.registerCraftingRecipes();
		logger.info("Register Crafting Recipes");

		for (int i = 0; i < 16; i++) {
			OreDictionary.registerOre("sonarStableStone", SonarCore.stableStone[i]);
			OreDictionary.registerOre("sonarStableStone", SonarCore.stablestonerimmedBlock[i]);
			OreDictionary.registerOre("sonarStableStone", SonarCore.stablestonerimmedblackBlock[i]);
		}
		ASMLoader.load(event.getAsmData());
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("Checking Loaded Mods");
		SonarLoader.initLoader();

		logger.info("Registering Packets");
		registerPackets();
		logger.info("Register Packets");

		if (SonarLoader.wailaLoaded) {
			SonarWailaModule.register();
			logger.info("Integrated with WAILA");
		} else {
			logger.warn("'WAILA' - unavailable or disabled in config");
		}
		logger.info("Registered Events");
		proxy.load(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("Adding Discharge Values");
		dischargeValues.register();
		logger.info("Added " + dischargeValues.getRegisterCount() + " Discharge Values");
		for (Map.Entry<Item, Integer> entry : dischargeValues.registry_ni.entrySet()) {
			logger.info("Discharge Values: " + entry.toString());
		}
		logger.info("Registered " + fluidHandlers.size() + " Fluid Providers");
		logger.info("Registered " + tileEnergyHandlers.size() + " Energy Handlers");
		logger.info("Registered " + itemEnergyHandlers.size() + " Energy Container Providers");
		//logger.info("Registered " + machineUpgrades.getMap().size() + " Machine Upgrades");
		proxy.postLoad(event);
	}

	private void registerPackets() {
		int id = 0;
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, id++, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, id++, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, id++, Side.SERVER);
			network.registerMessage(PacketByteBuf.Handler.class, PacketByteBuf.class, id++, Side.CLIENT);
			network.registerMessage(PacketByteBuf.Handler.class, PacketByteBuf.class, id++, Side.SERVER);
			network.registerMessage(PacketStackUpdate.Handler.class, PacketStackUpdate.class, id++, Side.CLIENT);
			network.registerMessage(PacketInvUpdate.Handler.class, PacketInvUpdate.class, id++, Side.CLIENT);
			network.registerMessage(PacketTileSyncUpdate.Handler.class, PacketTileSyncUpdate.class, id++, Side.CLIENT);

			if (SonarLoader.mcmultipartLoaded) {
				network.registerMessage(PacketMultipartSync.Handler.class, PacketMultipartSync.class, id++, Side.CLIENT);
				network.registerMessage(PacketByteBufMultipart.Handler.class, PacketByteBufMultipart.class, id++, Side.CLIENT);
				network.registerMessage(PacketByteBufMultipart.Handler.class, PacketByteBufMultipart.class, id++, Side.SERVER);
				network.registerMessage(PacketRequestMultipartSync.Handler.class, PacketRequestMultipartSync.class, id++, Side.SERVER);
			}
			network.registerMessage(PacketFlexibleOpenGui.Handler.class, PacketFlexibleOpenGui.class, id++, Side.CLIENT);
			network.registerMessage(PacketFlexibleContainer.Handler.class, PacketFlexibleContainer.class, id++, Side.CLIENT);
			network.registerMessage(PacketFlexibleContainer.Handler.class, PacketFlexibleContainer.class, id++, Side.SERVER);
			network.registerMessage(PacketFlexibleCloseGui.Handler.class, PacketFlexibleCloseGui.class, id++, Side.CLIENT);
			network.registerMessage(PacketFlexibleCloseGui.Handler.class, PacketFlexibleCloseGui.class, id++, Side.SERVER);
			network.registerMessage(PacketFlexibleMultipartChangeGui.Handler.class, PacketFlexibleMultipartChangeGui.class, id++, Side.SERVER);
			network.registerMessage(PacketFlexibleItemStackChangeGui.Handler.class, PacketFlexibleItemStackChangeGui.class, id++, Side.SERVER);
		}
	}

	public static void sendPacketAround(TileEntity tile, int spread, int id) {
		if (tile instanceof IByteBufTile) {
			if (!tile.getWorld().isRemote) {
				SonarCore.network.sendToAllAround(new PacketByteBuf((IByteBufTile) tile, tile.getPos(), id), new TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
			} else {
				SonarCore.network.sendToServer(new PacketByteBuf((IByteBufTile) tile, tile.getPos(), id));
			}
		}
	}

	public static void sendFullSyncAround(TileEntity tile, int spread) {
		if (!tile.getWorld().isRemote && tile instanceof INBTSyncable) {
			NBTTagCompound tag = ((INBTSyncable) tile).writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(new PacketTileSync(tile.getPos(), tag), new TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
			}
		}
	}

	public static void sendFullSyncAroundWithRenderUpdate(TileEntity tile, int spread) {
		if (tile != null && !tile.getWorld().isRemote && tile instanceof INBTSyncable) {
			NBTTagCompound tag = ((INBTSyncable) tile).writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE);
			if (!tag.hasNoTags()) {
				SonarCore.network.sendToAllAround(new PacketTileSyncUpdate(tile.getPos(), tag), new TargetPoint(tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), spread));
			}
		}
	}

	public static void refreshFlexibleContainer(EntityPlayer player) {
		if (player.getEntityWorld().isRemote) {
			SonarCore.network.sendToServer(new PacketFlexibleContainer(player));
		} else {
			SonarCore.network.sendTo(new PacketFlexibleContainer(player), (EntityPlayerMP) player);
		}
	}

	public static void sendPacketToServer(TileEntity tile, int id) {
		if (tile.getWorld().isRemote)
			SonarCore.network.sendToServer(new PacketByteBuf((IByteBufTile) tile, tile.getPos(), id));
	}

	public static int randInt(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}
}
