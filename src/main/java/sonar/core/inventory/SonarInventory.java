package sonar.core.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.helpers.SonarHelper;

public class SonarInventory extends AbstractSonarInventory<SonarInventory> {

	public final TileEntity tile;

	public SonarInventory(TileEntity tile, int size) {
		super(size);
		this.tile = tile;
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

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return stack;
		}
		if (!(tile instanceof ISidedInventory) || (SonarHelper.intContains(((ISidedInventory) tile).getSlotsForFace(face), slot) && ((ISidedInventory) tile).canInsertItem(slot, stack, face))) {
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		ItemStack stack = slots.get(slot);
		if (stack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		if (!(tile instanceof ISidedInventory) || (SonarHelper.intContains(((ISidedInventory) tile).getSlotsForFace(face), slot) && ((ISidedInventory) tile).canExtractItem(slot, stack, face))) {
			return super.extractItem(slot, amount, simulate);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void markDirty() {
		markChanged();		
	}
}