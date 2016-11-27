package com.teamwizardry.refraction.api.book;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.client.gui.components.ComponentVoid;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 */
public final class TextAdapter {

    public static int wrapLength = 190;
    private int x, y;
    private ComponentVoid parent;

    public TextAdapter(int x, int y) {
        this.x = x;
        this.y = y;
        parent = new ComponentVoid(x, y);
        parent.addTag("text");
    }

    public ComponentVoid getParent() {
        return parent;
    }

    public void parseLine(@NotNull JsonElement object) {
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.setBidiFlag(true);
        fr.setUnicodeFlag(true);

        if (object.isJsonPrimitive()) {

            if (object.getAsString().isEmpty()) {
                y += fr.FONT_HEIGHT;
                x = 0;
            } else {
                TextModule text = new TextModule(object.getAsString(), x, y);
                parent.add(text.getComponent());
                x = text.x;
                y = text.y;
            }

        } else if (object.isJsonObject()) {
            JsonObject obj = object.getAsJsonObject();
            TextStyle style = TextStyle.getStyleFromObject(obj);

            if (obj.has("text") && obj.get("text").isJsonPrimitive()) {
                TextModule text = new TextModule(style.getFormattingCode() + obj.get("text").getAsString(), x, y);
                parent.add(text.getComponent());
                x = text.x;
                y = text.y;
            } else if (obj.has("type") && obj.get("type").isJsonPrimitive()
                    && obj.get("type").getAsString().equals("stack") && obj.has("id")
                    && obj.get("id").isJsonPrimitive()) {
                ItemStack stack = JEIRefractionPlugin.getStackFromString(obj.get("id").getAsString());
                StackModule text = new StackModule(stack, x, y);
                parent.add(text.getComponent());
                x = text.x;
                y = text.y;
            } else if (obj.has("type") && obj.get("type").isJsonPrimitive()
                    && obj.get("type").getAsString().equals("player")) {
                PlayerModule text = new PlayerModule(x, y);
                parent.add(text.getComponent());
                x = text.x;
                y = text.y;
            }

        } else if (object.isJsonArray()) for (JsonElement element : object.getAsJsonArray()) parseLine(element);

        fr.setBidiFlag(false);
        fr.setUnicodeFlag(false);
    }
}
