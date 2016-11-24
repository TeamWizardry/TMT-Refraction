package com.teamwizardry.refraction.client.gui;

import com.teamwizardry.librarianlib.client.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.client.gui.GuiComponent;
import com.teamwizardry.librarianlib.client.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.client.gui.components.ComponentText;
import com.teamwizardry.librarianlib.client.gui.mixin.ButtonMixin;
import com.teamwizardry.librarianlib.client.sprite.Sprite;
import com.teamwizardry.librarianlib.client.sprite.Texture;
import com.teamwizardry.librarianlib.common.util.math.Vec2d;
import com.teamwizardry.refraction.Refraction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;


/**
 * Created by Saad on 10/8/2016.
 */
public class SidebarItem {

	private static final double TRANSITION_TIME_X = 0.25, TRANSITION_TIME_Y = 0.5;
	private final int id;
	public int currentPage = 0;
	public ArrayList<SubPageItem> pages = new ArrayList<>();
	public long prevMillis;
	public float prevX, destX, currentX;
	public float prevY, destY, currentY;
	public boolean isAnimating = false, animatingX = false, animatingY = false;
	private Sprite icon;
	private String info;
	private ResourceLocation sliderLoc = new ResourceLocation(Refraction.MOD_ID, "textures/gui/slider_1.png");
	private Texture sliderTexture = new Texture(sliderLoc);
	private Sprite sliderSprite = sliderTexture.getSprite("slider", 130, 18);

	public SidebarItem(int id, Sprite icon, String info) {
		this.id = id;
		this.icon = icon;
		this.info = info;
	}

	public ArrayList<SubPageItem> getPages() {
		return pages;
	}

	public void setPages(ArrayList<SubPageItem> pages) {
		this.pages = pages;
	}

	public ComponentSprite get() {
		ComponentSprite background = new ComponentSprite(sliderSprite, -120, 20 * id, 110, 18);
		background.addTag(id);

		ComponentSprite sprite = new ComponentSprite(icon, 5, 1, 16, 16);
		background.add(sprite);

		ComponentText infoComp = new ComponentText(sprite.getSize().getXi() + 10, 8, ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		background.add(infoComp);

		pages.forEach(page -> background.add(page.get()));

		new ButtonMixin<>(background, () -> {
		});

		background.BUS.hook(GuiComponent.ComponentTickEvent.class, (event) -> {
			double shift = 5;
			if (GuiBook.selectedSiderbar.getId() == id) {
				destX = (float) (-110 - shift);
				destY = 20 * id;

				infoComp.getText().setValue(TextFormatting.ITALIC + info);
			} else {
				destX = -110f;
				if (GuiBook.selectedSiderbar.getId() > id) destY = 20 * id;
				else destY = 20 * id + GuiBook.selectedSiderbar.pages.size() * 20;

				infoComp.getText().setValue(info);
			}

			if (!isAnimating) {
				background.setSize(new Vec2d(Math.abs(destX), 18));
				background.setPos(new Vec2d(destX, destY));
			} else {

				if (animatingX) {
					double millisTransition = (System.currentTimeMillis() - prevMillis) / 1000.0;
					if (millisTransition < TRANSITION_TIME_X) {
						if (Math.round(destX) > Math.round(prevX))
							currentX = -MathHelper.sin((float) (millisTransition * Math.PI / (TRANSITION_TIME_X * 2))) * (destX - prevX) - prevX;
						else
							currentX = MathHelper.sin((float) (millisTransition * Math.PI / (TRANSITION_TIME_X * 2))) * (destX - prevX) - prevX;
					} else {
						animatingX = false;
						currentX = destX;
					}
				}

				if (animatingY) {
					double millisTransitionY = (System.currentTimeMillis() - prevMillis) / 1000.0;
					if (millisTransitionY < TRANSITION_TIME_Y) {
						if (Math.round(destY) > Math.round(prevY))
							currentY = -MathHelper.sin((float) (millisTransitionY * Math.PI / (TRANSITION_TIME_Y * 2))) * (destY - prevY) - prevY;
						else
							currentY = MathHelper.sin((float) (millisTransitionY * Math.PI / (TRANSITION_TIME_Y * 2))) * (destY - prevY) - prevY;
					} else {
						animatingY = false;
						currentY = destY;
					}
				}

				if (!animatingX && !animatingY) isAnimating = false;

				// background.setSize(new Vec2d(Math.abs(currentX), 18));
				// background.setPos(new Vec2d(currentX, currentY));
				background.setSize(new Vec2d(Math.abs(destX), 18));
				background.setPos(new Vec2d(destX, destY));
			}
		});

		background.BUS.hook(ButtonMixin.ButtonClickEvent.class, (event -> {
			if (event.getButton() == EnumMouseButton.LEFT) {
				if (GuiBook.selectedSiderbar.getId() != id) {

					GuiBook.selectedSiderbar.prevX = GuiBook.selectedSiderbar.currentX;
					GuiBook.selectedSiderbar.destX = 0;
					GuiBook.selectedSiderbar = GuiBook.categories.get(id);
					GuiBook.selectedSiderbar.prevMillis = System.currentTimeMillis();

					GuiBook.categories.forEach(SidebarItem::animate);
				}
			}
		}));

		return background;
	}

	public void animate() {
		isAnimating = true;
		animatingX = true;
		animatingY = true;
		prevMillis = System.currentTimeMillis();
	}

	public void slide(float destX, float destY) {
		prevX = this.destX;
		prevY = this.destY;
		this.destX = destX;
		this.destY = destY;
		isAnimating = true;
		animatingX = true;
		animatingY = true;
		prevMillis = System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public Sprite getIcon() {
		return icon;
	}

	public void setIcon(Sprite icon) {
		this.icon = icon;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
