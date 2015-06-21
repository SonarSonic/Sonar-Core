package sonar.core.utils.helpers;

import java.math.BigDecimal;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class FontHelper {

	/**
	 * @param colour 0 = grey, 1 = black, 2 = white
	 */
	public static void text(String info, int x, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		switch (colour) {
		case 0:
			render.drawString(info, x, y, 4210752);
			break;
		case 1:
			render.drawString(info, x, y, 1);
			break;
		case 2:
			render.drawString(info, x, y, -1);
			break;
		default:
			render.drawString(info, x, y, colour);
			break;
		}
	}

	/**
	 * @param colour 0 = grey, 1 = black, 2 = white
	 */
	public static void textCentre(String info, int xSize, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		switch (colour) {
		case 0:
			render.drawString(info, xSize / 2 - width(info) / 2, y, 4210752);
			break;
		case 1:
			render.drawString(info, xSize / 2 - width(info) / 2, y, 1);
			break;
		case 2:
			render.drawString(info, xSize / 2 - width(info) / 2, y, -1);
			break;
		default:
			render.drawString(info, xSize / 2 - width(info) / 2, y, colour);
			break;
		}
	}

	public static int width(String info) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		return render.getStringWidth(info);
	}

	/**
	 * @param info string information
	 * @param xCentre where you want txt to be centred
	 * @param y y coordinate
	 * @param colour 0 = Gray, 1= Black, 2 = White
	 */
	public static void textOffsetCentre(String info, int xCentre, int y, int colour) {
		FontRenderer render = Minecraft.getMinecraft().fontRenderer;
		switch (colour) {
		case 0:
			render.drawString(info, xCentre - width(info) / 2, y, 4210752);
			break;
		case 1:
			render.drawString(info, xCentre - width(info) / 2, y, 1);
			break;
		case 2:
			render.drawString(info, xCentre - width(info) / 2, y, -1);
			break;
		default:
			render.drawString(info, xCentre - width(info) / 2, y, colour);
			break;
		}
	}

	/** sends a chat message to the Player */
	public static void sendMessage(String string, World world, EntityPlayer player) {
		if (!world.isRemote) {
			player.addChatComponentMessage(new ChatComponentText(string));
		}
	}
	public static String formatStorage(int power) {
		if ((power <= 1000)) {
			return power + " RF";
		} else if ((power <= 1000000)) {
			return roundValue(1, (float) power / 1000) + " KRF";
		} else if ((power <= 1000000000)) {
			return roundValue(1, (float) power / 1000000) + " MRF";
		}
		return roundValue(2, (float) power / 1000000000) + " BRF";

	}
	public static String formatOutput(int power) {
		if ((power <= 1000)) {
			return power + " RF/T";
		} else if ((power <= 1000000)) {
			return roundValue(1, (float) power / 1000) + " KRF/T";
		} else if ((power <= 1000000000)) {
			return roundValue(1, (float) power / 1000000) + " MRF/T";
		}
		return roundValue(2, (float) power / 1000000000) + " BRF/T";

	}

	public static Float roundValue(int decimalPlace, Float d) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}
}
