package com.teamwizardry.refraction.client.gui.tablet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import com.teamwizardry.refraction.api.book.TextAdapter;
import com.teamwizardry.refraction.client.gui.LeftSidebar;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class SubPage {

	public Page page;
	public String title, text = "";
	public boolean isSelected = false;
	public int id, selectedExtraID = 0;
	public List<ExtraSidebar> extraSidebars = new ArrayList<>();
	public List<String> textPages = new ArrayList<>();
	private ComponentSprite component;
	private JsonObject object;
	private TextAdapter adapter;

	public SubPage(Page page, int id, JsonObject object) {
		this.page = page;
		this.id = id;
		this.object = object;

		if (object.has("title") && object.get("title").isJsonPrimitive())
			title = object.get("title").getAsString();

		if (object.has("text") && object.get("text").isJsonArray()) {
			JsonArray pageArray = object.getAsJsonArray("text");
			adapter = new TextAdapter(this, 0, pageArray);
			text = adapter.convertLinesToString();
		}

		int letterCount = 0;
		String builder = "";
		for (String word : text.split(" ")) {
			word = word.trim();
			if (letterCount < 950) {
				letterCount += word.length();
				builder += " " + word;
			} else {
				textPages.add(builder.trim());
				builder = "";
				letterCount = 0;
			}
		}
		textPages.add(builder);

		ComponentSprite background = new ComponentSprite(LeftSidebar.leftNormal, 0, 0, LeftSidebar.leftNormal.getWidth(), LeftSidebar.leftNormal.getHeight());
		background.addTag(id);

		ComponentText titleComp = new ComponentText(10, 9, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(titleComp);

		new ButtonMixin(background, () -> {
		});

		background.BUS.hook(GuiComponentEvents.ComponentTickEvent.class, (event) -> {
			if (page.isSelected) {
				background.setVisible(true);
				//background.setEnabled(true);
			} else {
				background.setVisible(false);
				//background.setEnabled(false);
			}

			if (isSelected) {
				background.setSprite(LeftSidebar.leftExtended);
				int x = LeftSidebar.leftExtended.getWidth() - LeftSidebar.leftExtended.getWidth();
				background.setPos(new Vec2d(x + 5, 20 + 20 * id));
				background.setSize(new Vec2d(LeftSidebar.leftExtended.getWidth(), LeftSidebar.leftExtended.getHeight()));
				titleComp.getText().setValue(TextFormatting.ITALIC + title);
			} else {
				background.setSprite(LeftSidebar.leftNormal);
				int x = LeftSidebar.leftExtended.getWidth() - LeftSidebar.leftNormal.getWidth();
				background.setPos(new Vec2d(x + 5, 20 + 20 * id));
				background.setSize(new Vec2d(LeftSidebar.leftNormal.getWidth(), LeftSidebar.leftNormal.getHeight()));
				titleComp.getText().setValue(title);
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (!isSelected) {
					page.pageNB = 0;
					for (SubPage subPage : page.subPages) subPage.isSelected = false;
					isSelected = true;
					page.selectedSubPage = id;
					page.text = text;
				}
			}
		}));

		component = background;

	}

	public SubPage init() {
		int i = 0;
		if (adapter != null)
			for (ExtraSidebar extraSidebar : adapter.extraSidebars) {
				i = extraSidebar.id;
				extraSidebars.add(extraSidebar);
			}
		if (adapter != null && !adapter.extraSidebars.isEmpty()) i++;
		if (object.has("extra") && object.get("extra").isJsonArray()) {
			for (JsonElement extraObject : object.get("extra").getAsJsonArray()) {
				if (extraObject.isJsonObject()) {
					ExtraSidebar extraSidebar = new ExtraSidebar(this, i++, extraObject.getAsJsonObject(), ExtraSidebar.SidebarType.IMAGE).init();
					extraSidebars.add(extraSidebar);
				}
			}
		}

		for (ExtraSidebar extraSidebar : extraSidebars) component.add(extraSidebar.getComponent());

		return this;
	}

	public ComponentSprite getComponent() {
		return component;
	}
}
