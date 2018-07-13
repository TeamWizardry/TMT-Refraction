package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.provided.book.IBookGui;
import com.teamwizardry.librarianlib.features.gui.provided.book.TranslationHolder;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.entry.Entry;
import com.teamwizardry.librarianlib.features.gui.provided.book.hierarchy.page.Page;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
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

	@NotNull
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	public List<GuiComponent> createBookComponents(@NotNull IBookGui book, @NotNull Vec2d size) {
		List<GuiComponent> comps = new ArrayList<>();
		comps.add( new ComponentPicture(0, 0, size.getXi(), size.getYi(), pictureLoc, subtext));
		return comps;
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

	@NotNull
	@Override
	public Entry getEntry() {
		return this.entry;
	}
}
