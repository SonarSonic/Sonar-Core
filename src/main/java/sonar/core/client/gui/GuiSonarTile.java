package sonar.core.client.gui;

import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.network.PacketByteBuf;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IWorldPosition;

public abstract class GuiSonarTile extends GuiSonar {

	public IWorldPosition entity;

	public GuiSonarTile(Container container, IWorldPosition entity) {
		super(container);
		if (entity != null)
			this.entity = entity;
	}

	@Override
	public void onButtonClicked(int i) {
		SonarCore.network.sendToServer(new PacketByteBuf((IByteBufTile) entity, entity.getCoords().getBlockPos(), i));
	}

}
