package com.teamwizardry.refraction.client.gui;

import com.google.gson.JsonArray;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;

/**
 * Created by LordSaad.
 */
public class PageWrapper {

	private JsonArray text;

	public PageWrapper(JsonArray text) {
		this.text = text;
	}

	public ComponentVoid get() {
		ComponentVoid plate = new ComponentVoid(0, 0);

		/*String longString = "";
		for (int i = 0; i < text.size(); i++) {
			JsonElement element = text.get(i);
			if (element.isJsonPrimitive()) {
				longString += element.getAsJsonPrimitive();
			} else if (element.isJsonObject()) {
				JsonObject object = element.getAsJsonObject();
				if (object.has("recipe") && object.get("recipe").isJsonPrimitive()) {
					String item = object.get("item").getAsString();
					IRecipeLayoutDrawable drawable = getDrawableFromItem(item);
					if (drawable != null) {
						drawable.draw(Minecraft.getMinecraft(), 0, 0);
						ComponentText text = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
					} else longString += "<NULL ITEM>";
				}
			}
		}

		String text = formatString(longString);
		ComponentText componentText = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		componentText.getText().setValue(text);
		plate.add(componentText);
		longString = "";*/

		return plate;
	}



	private String formatString(String toFormat) {
		return toFormat.replaceAll("&([0-9a-fA-Fl-oL-OrR])", "§\1");
	}
}
