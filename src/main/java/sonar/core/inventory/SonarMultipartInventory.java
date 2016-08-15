package sonar.core.inventory;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.network.sync.ISyncPart;

public class SonarMultipartInventory extends AbstractSonarInventory<SonarMultipartInventory> implements ISyncPart {

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