package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.Page;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PagePicture implements Page {

	private ResourceLocation pictureLoc;
	private TranslationHolder subtext;
	private Entry entry;

	public PagePicture(Entry entry, JsonObject jsonElement) {
		if (jsonElement.get("subtext") != null)
			subtext = TranslationHolder.Companion.fromJson(jsonElement.get("subtext"));
		pictureLoc = new ResourceLocation(jsonElement.get("picture").getAsString());
		this.entry = entry;
	}

	@Nonnull
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public List<GuiComponent> createBookComponents(@Nonnull IBookGui book, @Nonnull Vec2d size) {
		ComponentVoid content = new ComponentVoid(0, 0);

		int x = (int)(-4 + 100 / 2.0 - 24.0 - 16d);
		int y = (int)(-16 + 100 / 2.0 - 16D - 8.0);

		content.add(new ComponentSprite(new Sprite(pictureLoc), x, y, 100, 100));

		if (subtext != null) {
			ComponentText text = new ComponentText(size.getXi() / 2, size.getYi() * 3 / 4,
					ComponentText.TextAlignH.CENTER, ComponentText.TextAlignV.TOP);
			text.getText().setValue(subtext.toString());
			text.getWrap().setValue(size.getXi() * 3 / 4);
			content.add(text);
		}

		return new ArrayList<>(Collections.singleton(content));
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
	public @Nonnull Entry getEntry() {
		return this.entry;
	}
}
