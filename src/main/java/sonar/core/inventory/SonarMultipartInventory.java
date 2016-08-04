package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.sync.DirtyPart;

public class SonarMultipartInventory extends AbstractSonarInventory<SonarMultipartInventory> {

	public final SonarMultipart part;

	public SonarMultipartInventory(SonarMultipart part, int size) {
		super(size);
		this.part = part;
	}

	@Override
	public void markDirty() {
        part.getWorld().markChunkDirty(part.getPos(), null);
	}
	public String getName() {
		return part.getItemStack().getDisplayName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(part.getItemStack().getUnlocalizedName());
	}
	
}