package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;

public class SonarInventory extends AbstractSonarInventory<SonarInventory> {

	public final TileEntity tile;

	public SonarInventory(TileEntity tile, int size) {
		super(size);
		this.tile = tile;
	}

	@Override
	public void markDirty() {
		tile.markDirty();
	}
	public String getName() {
		return tile.getBlockType().getUnlocalizedName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(tile.getBlockType().getUnlocalizedName());
	}
	
}