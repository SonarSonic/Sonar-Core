package sonar.core.handlers.inventories;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.handlers.energy.DischargeValues;
import sonar.core.handlers.energy.EnergyTransferHandler;
import sonar.core.handlers.inventories.containers.ContainerSonar;

public class TransferSlotsManager<T extends IInventory> {
	public static TransferSlotsManager<IInventory> DEFAULT = new TransferSlotsManager<IInventory>() {
		{
			addPlayerInventory();
		}
	};
	public static TransferSlots DISCHARGE_SLOT =  new TransferSlots<IInventory>(TransferType.TILE_INV, 1) {
        @Override
		public boolean canInsert(EntityPlayer player, IInventory inv, Slot slot, int pos, int slotID, ItemStack stack) {
			return DischargeValues.getValueOf(stack) > 0 || EnergyTransferHandler.INSTANCE_SC.getItemHandler(stack) != null;
		}
	};
    private List<TransferSlots<T>> slots = new ArrayList<>();
	public int current;
	public int playerInvStart;
	public int playerInvEnd;
    public boolean hasPlayerInv;

    public TransferSlotsManager() {}

    public TransferSlotsManager(int tileSize){
    	this.addTransferSlot(new TransferSlots(TransferType.TILE_INV, tileSize));
    	this.addPlayerInventory();
	}

	public void addTransferSlot(TransferSlots transferSlots) {
		transferSlots.start = current;
		transferSlots.end = current + transferSlots.size;
		current += transferSlots.size;
		slots.add(transferSlots);
	}

	public void addPlayerInventory() {
		playerInvStart = current;
		addTransferSlot(new TransferSlots(TransferType.PLAYER_INV, 9 * 3));
		addTransferSlot(new TransferSlots(TransferType.PLAYER_HOTBAR, 9));
		playerInvEnd = current;
		hasPlayerInv = true;
	}
	
	public void addPlayerMainInventory() {
		playerInvStart = current;
		addTransferSlot(new TransferSlots(TransferType.PLAYER_INV, 9 * 3));
		playerInvEnd = current;
		hasPlayerInv = true;
	}
	
	public void addPlayerHotbar() {
		playerInvStart = current;
		addTransferSlot(new TransferSlots(TransferType.PLAYER_HOTBAR, 9));
		playerInvEnd = current;
		hasPlayerInv = true;
	}

	public TransferSlots<T> getTransferSettings(int slotID) {
		for (TransferSlots<T> slot : slots) {
			if (slot.start <= slotID && slot.end > slotID) {
				return slot;
			}
		}
		return slots.get(slots.size() - 1);
	}

	public ItemStack transferStackInSlot(ContainerSonar c, T inv, EntityPlayer player, int slotID) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = c.inventorySlots.get(slotID);

        if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			TransferSlots<T> settings = getTransferSettings(slotID);
			if (settings.type.isPlayerInv()) {
				for (TransferSlots<T> tileSlots : slots) {
					if (tileSlots != settings && tileSlots.canInsert(player, inv, slot, slotID - tileSlots.start, slotID, itemstack1)) {
						if (!c.mergeSonarStack(itemstack1, tileSlots.start, tileSlots.end, false)) {
							return ItemStack.EMPTY;
						}
					}
				}
                slot.onSlotChange(itemstack1, itemstack);
			} else if (this.hasPlayerInv && !c.mergeSonarStack(itemstack1, playerInvStart, playerInvEnd, false)) {
				return ItemStack.EMPTY;
			}
			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}

    public enum TransferType {
		TILE_INV, PLAYER_INV, PLAYER_HOTBAR;

		public boolean isPlayerInv() {
			return this != TILE_INV;
		}
	}

	public static class DisabledSlots<T extends IInventory> extends TransferSlots<T> {

		public DisabledSlots(TransferType type, int size) {
			super(type, size);
		}

        @Override
		public boolean canInsert(EntityPlayer player, T inv, Slot slot, int pos, int slotID, ItemStack stack) {
			return false;
		}
	}

	public static class TransferSlotsValid<T extends IInventory> extends TransferSlots<T> {

		public TransferSlotsValid(TransferType type, int size) {
			super(type, size);
		}

        @Override
		public boolean canInsert(EntityPlayer player, T inv, Slot slot, int pos, int slotID, ItemStack stack) {
			return slot.isItemValid(stack);
		}
	}

	public static class TransferSlots<T extends IInventory> {
		public TransferType type;
		public int start, end;
		public int size;

		public TransferSlots(TransferType type, int size) {
			this.type = type;
			this.size = size;
		}

		public boolean canInsert(EntityPlayer player, T inv, Slot slot, int pos, int slotID, ItemStack stack) {
			return true;
		}
	}
}
