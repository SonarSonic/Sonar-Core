package sonar.core.handlers.energy;

import me.modmuss50.fr.mutlipart.PipeMultipart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Loader;
import reborncore.mcmultipart.block.TileMultipartContainer;
import reborncore.mcmultipart.microblock.IMicroblock;
import reborncore.mcmultipart.microblock.IMicroblock.IFaceMicroblock;
import reborncore.mcmultipart.multipart.IMultipartContainer;
import reborncore.mcmultipart.multipart.ISlottedPart;
import reborncore.mcmultipart.multipart.MultipartHelper;
import reborncore.mcmultipart.multipart.PartSlot;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.ActionType;

public class FluxedRedstoneHandler extends EnergyHandler {

	public static String name = "Fluxed-Redstone-Provider";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideEnergy(TileEntity tile, EnumFacing dir) {
		if (tile != null && tile instanceof TileMultipartContainer) {
			IMultipartContainer container = MultipartHelper.getPartContainer(tile.getWorld(), tile.getPos());
			if (container != null) {
				if (dir != null) {
		            ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(dir));
		            if (part instanceof IMicroblock.IFaceMicroblock && !((IFaceMicroblock) part).isFaceHollow()) {
		                return false;
		            }
		        }				
				ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
				if (part instanceof PipeMultipart) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void getEnergy(StoredEnergyStack energyStack, TileEntity tile, EnumFacing dir) {
		//doesn't allow this
	}

	@Override
	public StoredEnergyStack addEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		//cables don't allow adding
		return transfer;
	}

	@Override
	public StoredEnergyStack removeEnergy(StoredEnergyStack transfer, TileEntity tile, EnumFacing dir, ActionType action) {
		//cables don't allow removing
		return transfer;
	}

	@Override
	public EnergyType getProvidedType() {
		return EnergyType.RF;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("fluxedredstone") && Loader.isModLoaded("reborncore");
	}
}