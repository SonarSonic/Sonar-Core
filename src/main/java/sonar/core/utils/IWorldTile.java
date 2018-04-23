package sonar.core.utils;

import net.minecraft.world.World;

public interface IWorldTile {

	/**returns the actual world, regardless of the multipart wrapper*/
	public World getActualWorld();
	
	/**will return the multipart wrapper world if it exists*/
	public World getPartWorld();
	
}
