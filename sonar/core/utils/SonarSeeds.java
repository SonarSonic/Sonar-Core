package sonar.core.utils;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.helpers.FontHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SonarSeeds extends Item implements IPlantable
{
    private Block cropBlock;
    private Block soilBlockID;
	public int greenhouseTier;

    public SonarSeeds(Block p_i45352_1_, Block p_i45352_2_, int tier)
    {
        this.cropBlock = p_i45352_1_;
        this.soilBlockID = p_i45352_2_;
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.greenhouseTier=tier;
    }
    @Override
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
      super.addInformation(stack, player, list, par4);
      
      switch(greenhouseTier){
      case 0: break;
      case 1: list.add(FontHelper.translate("planting.basic"));break;
      case 2: list.add(FontHelper.translate("planting.advanced"));break;
      case 3: list.add(FontHelper.translate("planting.flawless"));break;
      }
      }
    
    @Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	if(this.greenhouseTier==0){
        if (meta != 1)
        {
            return false;
        }
        else if (player.canPlayerEdit(x, y, z, meta, stack) && player.canPlayerEdit(x, y + 1, z, meta, stack))
        {
            if (world.getBlock(x, y, z).canSustainPlant(world, x, y, z, ForgeDirection.UP, this) && world.isAirBlock(x, y + 1, z))
            {
            	
            	world.setBlock(x, y + 1, z, this.cropBlock);
                --stack.stackSize;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    	}
    	return false;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z)
    {
        return cropBlock == Blocks.nether_wart ? EnumPlantType.Nether : EnumPlantType.Crop;
    }

    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z)
    {
        return cropBlock;
    }

    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z)
    {
        return 0;
    }
    
    public boolean canTierUse(int tier) {
		if (tier >= this.greenhouseTier) {
			return true;
		}
		return false;
	}
}