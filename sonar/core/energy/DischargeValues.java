package sonar.core.energy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import sonar.core.integration.SonarAPI;
import cpw.mods.fml.common.registry.GameRegistry;


/**an Array List with the Discharge Values of specific Items*/
public class DischargeValues
{
  private static final DischargeValues discharge = new DischargeValues();
  
  private Map powerList = new HashMap();  
  
  /**@return an instance of the Discharge Lists*/
  public static DischargeValues discharge()
  {
    return discharge;
  }  
  private DischargeValues()
  {
    addItem(Items.redstone, 1000);
    addItem(Items.coal, 500);
    addBlock(Blocks.coal_block, 4500);
    addBlock(Blocks.redstone_block, 9000);
    if(SonarAPI.calculatorLoaded()){
    addItem(GameRegistry.findItem("Calculator", "EnrichedCoal"), 3000);
    addItem(GameRegistry.findItem("Calculator", "FireCoal"), 10000);
    addItem(GameRegistry.findItem("Calculator", "PurifiedCoal"), 10000);
    addItem(GameRegistry.findItem("Calculator", "CoalDust"), 250);
    }

  }
  private void addBlock(Block input, int power)
  {
    addItem(Item.getItemFromBlock(input), power);
  }
  
  private void addItem(Item input, int power)
  {
    addRecipe(new ItemStack(input, 1, 32767), power);
  }
  
  private void addRecipe(ItemStack input, int power)
  {
    this.powerList.put(input, power);
  }
  

  /**@param stack Item Stack to get the RF value of
   * @return the RF value of the stack*/
  public int value(ItemStack stack) {
    Iterator iterator = this.powerList.entrySet().iterator();
    
    Map.Entry entry;
    do
    {
      if (!iterator.hasNext())
      {
        return 0;
      }
      
      entry = (Map.Entry)iterator.next();
    }
    while (!equalStacks(stack, (ItemStack)entry.getKey()));
    
    
    return (Integer) entry.getValue();
  }
  
  private boolean equalStacks(ItemStack stack1, ItemStack stack2) {
    return (stack2.getItem() == stack1.getItem()) && ((stack2.getItemDamage() == 32767) || (stack2.getItemDamage() == stack2.getItemDamage()));
  }
  /**@return discharge value array list*/  
  public Map getPowerList() {
    return this.powerList;
  }

  /**@param input short to upcast
   * @return upcasted input (by 65536)*/   
  public static int upcastShort(int input)
  {
    if (input < 0) input += 65536;
    return input;
  }
}
