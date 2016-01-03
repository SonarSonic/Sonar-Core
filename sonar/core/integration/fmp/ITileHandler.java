package sonar.core.integration.fmp;

import sonar.core.integration.fmp.handlers.TileHandler;

/**implemented by Tiles which have TileHandlers*/
public interface ITileHandler {

	public TileHandler  getTileHandler();
}
