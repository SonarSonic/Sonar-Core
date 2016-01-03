package sonar.core.integration;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;

/** Integrations with WAILA - Registers all HUDs */
public class SonarWailaModule {

	public static void register() {
		ModuleRegistrar.instance().registerBodyProvider(new HUDSonar(), TileEntitySonar.class);
	}

	public static class HUDSonar implements IWailaDataProvider {

		@Override
		public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
			return tag;
		}

		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			TileEntity handler = accessor.getTileEntity();
			if (handler == null)
				return currenttip;

			if (accessor.getTileEntity() instanceof TileEntitySonar) {
				TileEntitySonar tile = (TileEntitySonar) handler;
				tile.getWailaInfo(currenttip);
			}
			return currenttip;
		}

		@Override
		public final ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return accessor.getStack();
		}

		@Override
		public final List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

	}

	public static class HUDSonarFMP implements IWailaFMPProvider {

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			Object handler = accessor.getTileEntity();
			handler = FMPHelper.checkObject(handler);
			if (handler == null)
				return currenttip;

			if (handler instanceof IWailaInfo) {
				IWailaInfo tile = (IWailaInfo) handler;
				tile.getWailaInfo(currenttip);
			}
			
			return currenttip;
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

	}
}