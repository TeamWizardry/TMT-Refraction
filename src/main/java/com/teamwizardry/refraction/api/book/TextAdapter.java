package com.teamwizardry.refraction.api.book;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author LordSaad
 *         <p>
 *         This class will convert a JsonArray object into a formatted text component
 */
public class TextAdapter {

    public static int wrapLength = 200;
    private ComponentText textComponent;
    private String text;
    private JsonElement object;

    public TextAdapter(@NotNull JsonElement object) {
        this.object = object;
        text = "";
        textComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
        textComponent.addTag("text");
        parse();
    }

    public ComponentText getComponent() {
        return textComponent;
    }

    private void parse() {
        convertLinesToString(object);
        FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
        fr.setBidiFlag(true);
        fr.setUnicodeFlag(true);

        textComponent.getUnicode().setValue(true);
        textComponent.getWrap().setValue(TextAdapter.wrapLength);
        textComponent.getText().setValue(text);
    }

    private void convertLinesToString(JsonElement object) {
        for (JsonElement element : object.getAsJsonArray())
            if (element.isJsonPrimitive())
                if (!element.getAsString().isEmpty()) {
                    String s = element.getAsString();
                    s = s.replace("%player%", Minecraft.getMinecraft().thePlayer.getDisplayNameString());
                    s = s.replace("&", "§");
                    TextComponentString componentString = new TextComponentString(s);
                    s = componentString.getFormattedText();

                    // TODO: theres a trailing symbol at the end of the match.
                    String[] strings = s.split(" ");
                    Pattern pattern = Pattern.compile(".*%.*%.*");
                    List<String> words = Lists.newArrayList();
                    for (String string : strings) {
                        if (pattern.asPredicate().test(string)) {
                            string = string.replace("%", "");
                            if (string.startsWith("recipe:")) {
                                String stackString = string.split("recipe:")[1];
                                ItemStack stack = JEIRefractionPlugin.getStackFromString(stackString);
                                if (stack == null) {
                                    words.add(string);
                                    continue;
                                }
                                string = string.replaceAll(string, TextFormatting.RESET + "[" + TextFormatting.BLUE + stack.getDisplayName() + TextFormatting.RESET + "]");
                                words.add(string);
                            }
                        } else words.add(string);
                    }
                    StringBuilder builder = new StringBuilder();
                    for (String str : words) builder.append(str + " ");
                    s = builder.toString();
                   /* Matcher matcher = pattern.matcher(s);
                    while (matcher.find()) {
                        String module = matcher.group(1);
                        if (module.startsWith("recipe:")) {

                        }
                    }*/
                    text += s;
                } else text += "\n";
    }
}
