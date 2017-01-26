package sonar.core.helpers;

import java.math.BigDecimal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import sonar.core.utils.CustomColour;

public class FontHelper {

	public static int text(String info, int x, int y, CustomColour colour) {
		return text(info, x, y, colour.getRGB());
	}

	/** @param colour 0 = grey, 1 = black, 2 = white */
	public static int text(String info, int x, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRendererObj;
		switch (colour) {
		case 0:
			return render.drawString(info, x, y, 4210752);
		case 1:
			return render.drawString(info, x, y, 1);
		case 2:
			return render.drawString(info, x, y, -1);
		default:
			return render.drawString(info, x, y, colour);
		}
	}

	public static int textCentre(String info, int xSize, int y, CustomColour colour) {
		return textCentre(info, xSize, y, colour.getRGB());
	}

	/** @param colour 0 = grey, 1 = black, 2 = white
	 * @return */
	public static int textCentre(String info, int xSize, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRendererObj;
		switch (colour) {
		case 0:
			return render.drawString(info, xSize / 2 - width(info) / 2, y, 4210752);
		case 1:
			return render.drawString(info, xSize / 2 - width(info) / 2, y, 1);
		case 2:
			return render.drawString(info, xSize / 2 - width(info) / 2, y, -1);
		default:
			return render.drawString(info, xSize / 2 - width(info) / 2, y, colour);
		}
	}

	public static int width(String info) {
		FontRenderer render = Minecraft.getMinecraft().fontRendererObj;
		return render.getStringWidth(info);
	}

	public static int textOffsetCentre(String info, int xCentre, int y, CustomColour colour) {
		return textOffsetCentre(info, xCentre, y, colour.getRGB());
	}

	/** @param info string information
	 * @param xCentre where you want txt to be centred
	 * @param y y coordinate
	 * @param colour 0 = Gray, 1= Black, 2 = White */
	public static int textOffsetCentre(String info, int xCentre, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRendererObj;
		switch (colour) {
		case 0:
			return render.drawString(info, xCentre - width(info) / 2, y, 4210752);
		case 1:
			return render.drawString(info, xCentre - width(info) / 2, y, 1);
		case 2:
			return render.drawString(info, xCentre - width(info) / 2, y, -1);
		default:
			return render.drawString(info, xCentre - width(info) / 2, y, colour);
		}
	}

	/** sends a chat message to the Player & translates it */
	public static void sendMessage(String string, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.addChatComponentMessage(new TextComponentTranslation(string));
		}
	}

	public static void sendMessage(ITextComponent component, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.addChatComponentMessage(component);
		}
	}

	public static String formatStorage(long power) {
		if ((power < 1000)) {
			return power + " RF";
		} else if ((power < 1000000)) {
			return roundValue(1, (float) power / 1000) + " KRF";
		} else if ((power < 1000000000)) {
			return roundValue(1, (float) power / 1000000) + " MRF";
		}
		return roundValue(2, (float) power / 1000000000) + " BRF";

	}

	public static String formatOutput(long power) {
		if ((power < 1000)) {
			return power + " RF/t";
		} else if ((power < 1000000)) {
			return roundValue(1, (float) power / 1000) + " KRF/t";
		} else if ((power < 1000000000)) {
			return roundValue(1, (float) power / 1000000) + " MRF/t";
		}
		return roundValue(2, (float) power / 1000000000) + " BRF/t";

	}

	public static String formatStackSize(long stackSize) {
		if ((stackSize < 10000)) {
			return " " + stackSize;
		} else if ((stackSize < 1000000)) {
			return roundValue(1, (float) stackSize / 1000) + " K";
		} else if ((stackSize < 1000000000)) {
			return roundValue(1, (float) stackSize / 1000000) + " M";
		}
		return roundValue(2, (float) stackSize / 1000000000) + " B";

	}

	public static String formatFluidSize(long fluidSize) {
		if ((fluidSize < 10000)) {
			return " " + fluidSize + " mB";
		} else if ((fluidSize < 1000000)) {
			return roundValue(1, (float) fluidSize / 1000) + " KmB";
		} else if ((fluidSize < 1000000000)) {
			return roundValue(1, (float) fluidSize / 1000000) + " MmB";
		}
		return roundValue(2, (float) fluidSize / 1000000000) + " BmB";

	}

	public static Float roundValue(int decimalPlace, Float d) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	public static boolean addDigitsToString(GuiTextField box, char c, int i) {
		switch (c) {
		case '\001':
			return box.textboxKeyTyped(c, i);
		case '\003':
			return box.textboxKeyTyped(c, i);
		case '\026':
			return false;
		case '\030':
			return box.textboxKeyTyped(c, i);
		}
		switch (i) {
		case 14:
			return box.textboxKeyTyped(c, i);
		case 199:
			return box.textboxKeyTyped(c, i);
		case 203:
			return box.textboxKeyTyped(c, i);
		case 205:
			return box.textboxKeyTyped(c, i);
		case 207:
			return box.textboxKeyTyped(c, i);
		case 211:
			return box.textboxKeyTyped(c, i);
		}
		if (Character.isDigit(c)) {
			return box.textboxKeyTyped(c, i);
		}
		return false;
	}

	/* public static String translate(String string) { String local = FontHelper.translate(string); if (!local.equals(string)) { return local; } else { return StatCollector.translateToFallback(string); } } public static String fullTranslate(String s) { String ret = LanguageRegistry.instance().getStringLocalization(s); if (ret.length() == 0) ret = LanguageRegistry.instance().getStringLocalization(s, "en_US"); if (ret.length() == 0) ret = translate(s); if (ret.length() == 0) return s; return ret; } */

	public static String translate(String string) {
		return new TextComponentTranslation(string).getFormattedText();
	}

	public static int getIntFromColor(int red, int green, int blue) {
		red = (red << 16) & 0x00FF0000;
		green = (green << 8) & 0x0000FF00;
		blue = blue & 0x000000FF;

		return 0xFF000000 | red | green | blue;
	}
}
