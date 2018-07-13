package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.Page;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.refraction.api.book.ITextModularParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PageTextModular implements Page {

	private TranslationHolder langText;
	private Entry entry;
	private static List<ITextModularParser> parsers = new ArrayList<>();

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
	public List<GuiComponent> createBookComponents(@NotNull IBookGui book, @NotNull Vec2d size) {
		List<GuiComponent> pages = new ArrayList<>();
		String text = langText.toString();
		for ( ITextModularParser parser : parsers) {
			text = parser.parse(text);
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
			ComponentText sectionComponent = new ComponentText(0, 0, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.TOP);
			sectionComponent.getText().setValue(section);
			sectionComponent.getWrap().setValue(size.getXi());
			sectionComponent.getUnicode().setValue(true);

			pages.add(sectionComponent);
		}
		return pages;
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
}
