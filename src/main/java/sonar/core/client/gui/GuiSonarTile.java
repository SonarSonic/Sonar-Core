package sonar.core.client.gui;

import net.minecraft.inventory.Container;
import sonar.core.SonarCore;
import sonar.core.network.PacketByteBuf;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IWorldPosition;

public abstract class GuiSonarTile extends GuiSonar {

	public IWorldPosition entity;

	public GuiSonarTile(Container container, IWorldPosition entity) {
		super(container);
		if (entity != null) {
			this.entity = entity;
		}
	}
}
