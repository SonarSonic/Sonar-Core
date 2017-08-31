package sonar.core.listener;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PlayerListener implements ISonarListener {

	public EntityPlayerMP player;

	public PlayerListener(EntityPlayer player) {
		this((EntityPlayerMP)player);
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
		if (obj != null && obj instanceof PlayerListener) {
			return player.equals(((PlayerListener) obj).player);
		}
		return false;
	}

}
