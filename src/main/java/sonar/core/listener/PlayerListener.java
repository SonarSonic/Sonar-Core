package sonar.core.listener;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerListener implements ISonarListener {

    public EntityPlayerMP player;

    public PlayerListener(EntityPlayer player) {
        this((EntityPlayerMP) player);
    }

    public PlayerListener(EntityPlayerMP player) {
        this.player = player;
    }

    @Override
    public boolean isValid() {
        return !player.isDead;// TODO
    }

    public int hashCode() {
        return player.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof PlayerListener && player.equals(((PlayerListener) obj).player);
    }

}