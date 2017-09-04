package sonar.core.common.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

/**
 * special materials, to allow machines not to break
 */
public class SonarMaterials extends Material {

    /**
     * standard machine material
     */
    public static final Material machine = new Material(MapColor.BLACK);
    public static final Material fence = new Material(MapColor.BROWN);

	public SonarMaterials() {
		super(MapColor.BLACK);
	}
}
