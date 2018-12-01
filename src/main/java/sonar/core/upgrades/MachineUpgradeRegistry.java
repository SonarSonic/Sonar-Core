package sonar.core.upgrades;

import net.minecraft.item.Item;
import sonar.core.SonarCore;
import sonar.core.helpers.SimpleRegistry;

public class MachineUpgradeRegistry extends SimpleRegistry<String, Item> {

    public static MachineUpgradeRegistry instance(){
        return SonarCore.instance.machine_upgrades;
    }

    public void log(String name, Item item){
        SonarCore.logger.info("Machine Upgrade: " + name + " = " + item);
    }

}
