package com.teamwizardry.refraction.api.book;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.context.Bookmark;
import com.teamwizardry.librarianlib.features.gui.provided.book.helper.TranslationHolder;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.Page;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class PageTextModular implements Page, ITextModularCallback {

	private TranslationHolder langText;
	private Entry entry;
	private static List<ITextModularParser> parsers = new ArrayList<>();
	private Map<String, GuiComponent> customComponents = new HashMap<>();

	public static void registerParser(ITextModularParser parser) {
		parsers.add(parser);
	}

	public PageTextModular(Entry entry, JsonObject jsonElement) {
		langText = TranslationHolder.Companion.fromJson(jsonElement.get("text"));
		this.entry = entry;
	}

	@NotNull
	@Override
	public Entry getEntry() {
		return this.entry;
	}

	@NotNull
	@Override
	public List<Function0<GuiComponent>> createBookComponents(@NotNull IBookGui book, @NotNull Vec2d size) {
		List<Function0<GuiComponent>> pages = new ArrayList<>();
		String text = langText.toString().replace("<br>", "\n");
		for ( ITextModularParser parser : parsers) {
			text = parser.parse(this, text);
		}

		int lineCount = lineCount(size);
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		fr.setBidiFlag(true);
		fr.setUnicodeFlag(true);
		List<String> lines = fr.listFormattedStringToWidth(text, size.getXi());

		List<String> sections = new ArrayList<>();

		List<String> page = new ArrayList<>();
		for (String line : lines) {
			String trim = line.trim();
			if (!trim.isEmpty()) {
				page.add(trim);
				if (page.size() >= lineCount) {
					sections.add(String.join("\n", page));
					page.clear();
				}
			}
		}

		if (!page.isEmpty())
			sections.add(String.join("\n", page));

		for (String section : sections) {
			pages.add(() -> createSection(section, size));
		}
		return pages;
	}

	private GuiComponent createSection(String text, @NotNull Vec2d size) {
		ComponentVoid content = new ComponentVoid(16,16);
		StringBuilder prevText = new StringBuilder(text.length());

		for (String word : text.split(" ")) {
			if (customComponents.containsKey(word)) {
				if ( prevText.length() > 0) {
					content.add(createTextPart(prevText.toString(), size));
					prevText = new StringBuilder(text.length() - prevText.length());
				}
				content.add(customComponents.get(word));
			} else {
				prevText.append(word);
				prevText.append(" ");
			}
		}

		if ( prevText.length() > 0)
			content.add(createTextPart(prevText.toString(), size));
		return content;
	}

	private GuiComponent createTextPart(String text, @NotNull Vec2d size) {
		ComponentText textComp = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
		textComp.getText().setValue(text);
		textComp.getWrap().setValue(size.getXi());
		textComp.getUnicode().setValue(true);
		return textComp;
	}

	@SideOnly(Side.CLIENT)
	private int lineCount(Vec2d size) {
		return (int)(Math.ceil(size.getY() / Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) - 1);
	}

	@Nullable
	@Override
	public Collection<String> getSearchableStrings() {
		return null;
	}

	@Nullable
	@Override
	public Collection<String> getSearchableKeys() {
		return null;
	}

	@Override
	public String addCustomComponent(GuiComponent component) {
		String newKey = "[~~~" + customComponents.size() + "~~~]";
		customComponents.put( newKey, component);
		return newKey;
	}

	@NotNull
	@Override
	public List<Bookmark> getExtraBookmarks() {
		return new ArrayList<>();
	}
}
