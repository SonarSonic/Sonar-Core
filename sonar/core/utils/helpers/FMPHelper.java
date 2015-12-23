package sonar.core.utils.helpers;

import java.util.List;

import sonar.core.utils.SonarAPI;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public class FMPHelper {

	public static Object checkObject(Object object) {
		if (object != null && SonarAPI.forgeMultipartLoaded() && object instanceof TileMultipart) {
			List<TMultiPart> list = ((TileMultipart) object).jPartList();
			if (0 < list.size()) {
				object = list.get(0);
			}

		}
		return object;
	}
}
