package sonar.core.inventory;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.integration.multipart.SonarMultipart;

public class SonarMultipartInventory extends AbstractSonarInventory<SonarMultipartInventory> {

	public final SonarMultipart part;

	public SonarMultipartInventory(SonarMultipart part, int size) {
		super(size);
		this.part = part;
	}

	@Override
	public void markDirty() {
        part.getWorld().markChunkDirty(part.getPos(), null);
	}
	public String getName() {
		return part.getItemStack().getDisplayName();
	}

	public boolean hasCustomName() {
		return false;
	}

	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(part.getItemStack().getUnlocalizedName());
	}
	
}