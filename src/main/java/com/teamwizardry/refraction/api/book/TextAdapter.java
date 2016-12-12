package com.teamwizardry.refraction.api.book;

import com.google.gson.JsonElement;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.refraction.api.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

/**
 * @author LordSaad
 *         <p>
 *         This class will convert a JsonArray object into a formatted text component
 */
public class TextAdapter {

    public static int wrapLength = 200;
    private ComponentText textComponent;
    private JsonElement object;

    public TextAdapter(@NotNull JsonElement object) {
        this.object = object;
        textComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
        textComponent.addTag("text");
        textComponent.getUnicode().setValue(true);
        textComponent.getWrap().setValue(TextAdapter.wrapLength);
        textComponent.getText().setValue(convertLinesToString(object));
    }

    public ComponentText getComponent() {
        return textComponent;
    }

    private String convertLinesToString(JsonElement object) {
        String text = "";
        for (JsonElement element : object.getAsJsonArray())
            if (element.isJsonPrimitive() && !element.getAsString().isEmpty()) {
                String s = element.getAsString();
                s = s.replace("[player]", Minecraft.getMinecraft().thePlayer.getDisplayNameString());
                s = s.replace("&", "§");
                TextComponentString componentString = new TextComponentString(s);
                s = componentString.getFormattedText();

                for (String word : s.split(" ")) {
                    if (word.startsWith("[") && word.contains("]")) {
                        if (word.contains("recipe:")) {
                            String stackString = word.substring(word.indexOf("recipe:"), word.indexOf("]")).split("recipe:")[1];
                            ItemStack stack = Utils.HANDLER.getStackFromString(stackString);
                            if (stack != null) {
                                s = s.replace(word, TextFormatting.RESET + "[" + TextFormatting.BLUE + stack.getDisplayName() + TextFormatting.RESET + "]");
                            }
                        }
                    }
                }
                text += s;
            } else text += "\n";
        return text;
    }
}
