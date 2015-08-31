package sonar.core.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class SonarPackets {

	public static SimpleNetworkWrapper network;

	public static void registerPackets() {
		if (network == null) {
			network = NetworkRegistry.INSTANCE.newSimpleChannel("Sonar-Packets");
			network.registerMessage(PacketMachineButton.Handler.class, PacketMachineButton.class, 0, Side.SERVER);
			network.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, 1, Side.CLIENT);
			network.registerMessage(PacketSonarSides.Handler.class, PacketSonarSides.class, 2, Side.CLIENT);
			network.registerMessage(PacketInventorySync.Handler.class, PacketInventorySync.class, 3, Side.CLIENT);
			network.registerMessage(PacketRequestSync.Handler.class, PacketRequestSync.class, 4, Side.SERVER);
		}
	}

}
