package sonar.core.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryStoredCrafting extends InventoryCrafting {

	private IInventory inventory;
	private int inventoryWidth, offset, size;
    private Container container;

	/**@param container Container for crafting
	 * @param width crafting matrix width
	 * @param height crafting matrix height
	 * @param inventory inventory storing the crafting items
	 * @param offset number to add to the matrix position
	 * @param size overriding the size of the matrix*/
	public InventoryStoredCrafting(Container container, int width, int height, IInventory inventory, int offset, int size) {
		super(container, width, height);
		this.inventory=inventory;
		this.inventoryWidth=width;
		this.container=container;
		this.offset=offset;
		this.size=size;
		
	}
	/**@param container Container for crafting
	 * @param width crafting matrix width
	 * @param height crafting matrix height
	 * @param inventory inventory storing the crafting items*/
	public InventoryStoredCrafting(Container container, int width, int height, IInventory inventory) {
		super(container, width, height);
		this.inventory=inventory;
		this.inventoryWidth=width;
		this.container=container;
		
	}
	@Override
    public int getSizeInventory ()
    {
		if(size==0){
	        return inventory.getSizeInventory()-1;
		}
		return size;
    }

    @Override
    public ItemStack getStackInSlot (int slot)
    {
        return slot >= this.getSizeInventory() ? null : inventory.getStackInSlot(slot+1+offset);
    }
    @Override
    public ItemStack getStackInRowAndColumn (int row, int column)
    {
        if (row >= 0 && row < this.inventoryWidth)
        {
            int k = row + column * this.inventoryWidth;
            return this.getStackInSlot(k);
        }
        else
        {
            return null;
        }
    }

    public String getInvName ()
    {
        return "container.crafting";
    }

    public boolean isInvNameLocalized ()
    {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing (int par1)
    {
        return null;
    }

    @Override
    public ItemStack decrStackSize (int slotID, int par2)
    {
        ItemStack stack = inventory.getStackInSlot(slotID + 1+offset);
        if (stack != null)
        {
            ItemStack itemstack;

            if (stack.stackSize <= par2)
            {
                itemstack = stack.copy();
                stack = null;
                inventory.setInventorySlotContents(slotID + 1+offset, null);
                this.container.onCraftMatrixChanged(this);
                return itemstack;
            }
            else
            {
                itemstack = stack.splitStack(par2);

                if (stack.stackSize == 0)
                {
                    stack = null;
                }

                this.container.onCraftMatrixChanged(this);
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public void setInventorySlotContents (int slot, ItemStack itemstack)
    {
    	inventory.setInventorySlotContents(slot + 1+offset, itemstack);
        this.container.onCraftMatrixChanged(this);
    }

    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    @Override
    public void markDirty ()
    {
    }

    @Override
    public boolean isUseableByPlayer (EntityPlayer par1EntityPlayer)
    {
        return true;
    }


}
