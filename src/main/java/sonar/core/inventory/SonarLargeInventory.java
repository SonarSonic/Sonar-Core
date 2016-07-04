package sonar.core.inventory;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.DirtyPart;

public class SonarLargeInventory extends DirtyPart implements ISonarInventory {

	// public StoredItemStack[] slots;
	public ArrayList<ItemStack>[] slots;
	public int limit = 64;
	public final TileEntity tile;
	public int numStacks = 4;
	public int size;
	//public int max;

	public SonarLargeInventory(TileEntity tile, int size, int numStacks) {
		this.size=size;
		this.slots = new ArrayList[size];
		this.tile = tile;
		this.numStacks = numStacks;
	}

	public SonarLargeInventory setStackLimit(int limit) {
		this.limit = limit;
		return this;
	}

	public StoredItemStack buildItemStack(ArrayList<ItemStack> list) {
		StoredItemStack stack = null;
		if (list == null) {
			return stack;
		}
		for (ItemStack item : list) {
			if (item != null) {
				if (stack == null) {
					stack = new StoredItemStack(item);
				} else {
					stack.add(item);
				}
			}
		}
		return stack;
	}

	public ArrayList<ItemStack> buildArrayList(StoredItemStack stack) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		while (stack.stored > 0) {
			ItemStack item = stack.getFullStack();
			list.add(item.copy());
			stack.remove(item.copy());
		}
		return list;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			NBTTagList list = nbt.getTagList("Items", 10);
			this.slots = new ArrayList[size];
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte b = compound.getByte("Slot");
				if (b >= 0 && b < this.slots.length) {
					this.slots[b] = buildArrayList(StoredItemStack.readFromNBT(compound));
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SAVE) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < this.slots.length; i++) {
				if (this.slots[i] != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					StoredItemStack stack = buildItemStack(this.slots[i]);
					if (stack!=null && stack.stored != 0 && stack.item != null) {
						StoredItemStack.writeToNBT(compound, stack);
						list.appendTag(compound);
					}
				}
			}
			nbt.setTag("Items", list);
		}
	}

	public int getSizeInventory() {
		return this.slots.length * numStacks;
	}

	public ItemStack getStackInSlot(int slot) {
		int target = (int) Math.floor(slot / numStacks);
		ArrayList<ItemStack> stacks = slots[target];
		if (stacks == null) {
			slots[target] = new ArrayList();
			return null;
		} else {
			StoredItemStack stack = buildItemStack(stacks);
			int pos = slot - target*numStacks;
			//int pos = slot - target;			
			if (pos < stacks.size()) {
				return stacks.get(pos);
			} else {
				return null;
			}
		}
	}

	public ItemStack decrStackSize(int slot, int var2) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			setChanged(true);
			if (stack.stackSize <= var2) {
				ItemStack itemstack = stack;
				setInventorySlotContents(slot, null);
				return itemstack;
			}
			ItemStack itemstack = stack.splitStack(var2);
			if (stack.stackSize == 0) {
				setInventorySlotContents(slot, null);
			}
			return itemstack;
		}

		return null;
	}

	public ItemStack removeStackFromSlot(int i) {
		ItemStack stack = getStackInSlot(i);
		if (stack != null) {
			setChanged(true);
			ItemStack itemstack = stack;
			setInventorySlotContents(i, null);
			return itemstack;
		}
		return null;
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		int target = (int) Math.floor(i / numStacks);

		ArrayList<ItemStack> stacks = slots[target];
		if (stacks == null) {
			if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
				itemstack.stackSize = getInventoryStackLimit();
			}
			slots[target] = new ArrayList();
			slots[target].add(itemstack);
		} else {
			int pos = i - target*numStacks;
			if (pos < stacks.size()) {
				stacks.set(pos, itemstack);
			} else {
				if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
					itemstack.stackSize = getInventoryStackLimit();
				}
				slots[target].add(itemstack);
			}
		}
		setChanged(true);
	}

	public int getInventoryStackLimit() {
		return limit;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	public void openInventory(EntityPlayer player) {}

	public void closeInventory(EntityPlayer player) {}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		int target = (int) Math.floor(slot / numStacks);
		if (target < slots.length) {
			StoredItemStack stack = buildItemStack(slots[target]);
			if (stack == null || stack.getStackSize() == 0 || stack.getItemStack() == null || stack.equalStack(item)) {
				return true;
			}
		}
		return false;
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

	public int getField(int id) {
		return 0;
	}

	public void setField(int id, int value) {}

	public int getFieldCount() {
		return 0;
	}

	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++)
			this.setInventorySlotContents(i, null);
		setChanged(true);
	}

	@Override
	public void markDirty() {
		tile.markDirty();
	}

}
