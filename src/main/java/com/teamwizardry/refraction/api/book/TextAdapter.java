package com.teamwizardry.refraction.api.book;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.refraction.client.jei.JEIRefractionPlugin;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author WireSegal
 *         Created at 5:01 PM on 11/21/16.
 */
public final class TextAdapter {

	private static final Multimap<String, Parser> registry = HashMultimap.create();

	static {
		registerAdapter("translation", object -> {
			if (!object.has("translate") || !object.get("translate").isJsonPrimitive())
				return null;
			String key = object.get("translate").getAsString();
			if (!object.has("format"))
				return new TranslationTextHolder(key);

			JsonElement format = object.get("format");
			if (format.isJsonArray()) {
				List<ITextHolder> l = StreamSupport.stream(format.getAsJsonArray().spliterator(), false)
						.map(TextAdapter::adapt)
						.collect(Collectors.toList());

				return new TranslationTextHolder(key, l.toArray(new ITextHolder[l.size()]));
			}
			return new TranslationTextHolder(key, adapt(format));
		});

		registerAdapter("player", object -> new PlayerTextHolder());

		registerAdapter("stack", object -> {
			ItemStack stack;
			NBTTagCompound nbt = (NBTTagCompound) parseJsonToNBT(object);
			stack = ItemStack.loadItemStackFromNBT(nbt);

			return new StackTextHolder(stack);
		});

		registerAdapter("text", object -> {
			if (!object.has("text") || !object.get("text").isJsonPrimitive())
				return null;
			return new StringTextHolder(object.get("text").getAsString());
		});
	}

	private TextAdapter() {
		// PRIVATE CONSTRUCTOR
	}

	public static NBTBase parseJsonToNBT(JsonElement element) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive prim = element.getAsJsonPrimitive();
			if (prim.isBoolean())
				return new NBTTagByte((byte) (prim.getAsBoolean() ? 1 : 0));
			else if (prim.isString())
				return new NBTTagString(prim.getAsString());
			else {
				Number num = prim.getAsNumber();
				if (num instanceof Long)
					return new NBTTagLong(num.longValue());
				return new NBTTagInt(num.intValue());
			}
		} else if (element.isJsonNull())
			return new NBTTagString("<NULL>");
		else if (element.isJsonArray()) {
			NBTTagList ret = new NBTTagList();
			JsonArray arr = element.getAsJsonArray();
			byte type = 0;
			for (JsonElement el : arr) {
				NBTBase nbt = parseJsonToNBT(el);
				if (type == 0)
					type = nbt.getId();
				else if (type != nbt.getId()) {
					LibrarianLog.INSTANCE.warn("Json uses multiple types in an nbt block!");
					continue;
				}
				ret.appendTag(nbt);
			}
			return ret;
		} else if (element.isJsonObject()) {
			NBTTagCompound comp = new NBTTagCompound();
			JsonObject obj = element.getAsJsonObject();
			for (Map.Entry<String, JsonElement> keypair : obj.entrySet())
				comp.setTag(keypair.getKey(), parseJsonToNBT(keypair.getValue()));
			return comp;
		}

		return new NBTTagString("UNKNOWN DATA TYPE");
	}

	public static void registerAdapter(@NotNull String key, @NotNull Parser parser) {
		registry.put(key, parser);
	}

	private static TextStyle getStyleFromObject(JsonObject object) {
		TextFormatting color = object.has("color") && object.get("color").isJsonPrimitive() && object.getAsJsonPrimitive("color").isString() ?
				TextFormatting.getValueByName(object.get("color").getAsString()) : null;

		boolean bold = object.has("bold") &&
				object.get("bold").isJsonPrimitive() &&
				object.getAsJsonPrimitive("bold").isBoolean() &&
				object.getAsJsonPrimitive("bold").getAsBoolean(),
				italic = object.has("italic") &&
						object.get("italic").isJsonPrimitive() &&
						object.getAsJsonPrimitive("italic").isBoolean() &&
						object.getAsJsonPrimitive("italic").getAsBoolean(),
				underline = object.has("underlined") &&
						object.get("underlined").isJsonPrimitive() &&
						object.getAsJsonPrimitive("underlined").isBoolean() &&
						object.getAsJsonPrimitive("underlined").getAsBoolean(),
				obfuscated = object.has("obfuscated") &&
						object.get("obfuscated").isJsonPrimitive() &&
						object.getAsJsonPrimitive("obfuscated").isBoolean() &&
						object.getAsJsonPrimitive("obfuscated").getAsBoolean(),
				strikethrough = object.has("strikethrough") &&
						object.get("strikethrough").isJsonPrimitive() &&
						object.getAsJsonPrimitive("strikethrough").isBoolean() &&
						object.getAsJsonPrimitive("strikethrough").getAsBoolean();

		return new TextStyle()
				.setColor(color)
				.setBold(bold)
				.setItalic(italic)
				.setUnderlined(underline)
				.setObfuscated(obfuscated)
				.setStrikethrough(strikethrough);
	}

	@NotNull
	public static ITextHolder adapt(@NotNull JsonElement object) {
		if (object.isJsonNull())
			return new StringTextHolder("\n");
		else if (object.isJsonPrimitive()) {
			if (object.getAsString().isEmpty())
				return new StringTextHolder("\n");
			if (I18n.hasKey(object.getAsString()))
				return new TranslationTextHolder(object.getAsString());
			return new StringTextHolder(object.getAsString());
		} else if (object.isJsonArray()) {
			List<ITextHolder> l = StreamSupport.stream(object.getAsJsonArray().spliterator(), false)
					.map(TextAdapter::adapt)
					.collect(Collectors.toList());

			return new CombinedTextHolder(l.toArray(new ITextHolder[l.size()]));
		} else if (object.isJsonObject()) {
			JsonObject comp = object.getAsJsonObject();
			TextStyle style = getStyleFromObject(comp);

			String type = comp.has("type") && comp.get("type").isJsonPrimitive() ?
					comp.get("type").getAsString() : "text";
			Collection<Parser> parsers = registry.get(type);
			for (Parser parser : parsers) {
				ITextHolder parsed = parser.parse(comp);
				if (parsed != null) return parsed.setStyle(style);
			}
		}

		return new StringTextHolder("UNKNOWN DATA TYPE");
	}

	private static ItemStack getStackFromString(String itemId) {
		ResourceLocation location = new ResourceLocation(itemId);
		ItemStack stack = null;

		if (ForgeRegistries.ITEMS.containsKey(location)) {
			Item item = ForgeRegistries.ITEMS.getValue(location);
			if (item != null) stack = new ItemStack(item);

		} else if (ForgeRegistries.BLOCKS.containsKey(location)) {
			Block block = ForgeRegistries.BLOCKS.getValue(location);
			if (block != null) stack = new ItemStack(block);

		}
		return stack;
	}

	private IRecipeLayoutDrawable getDrawableFromItem(String itemId) {
		ItemStack stack = getStackFromString(itemId);

		if (stack != null) {
			IRecipeRegistry registry = JEIRefractionPlugin.jeiRuntime.getRecipeRegistry();
			IFocus<ItemStack> focus = registry.createFocus(IFocus.Mode.OUTPUT, stack);
			for (IRecipeCategory<?> category : registry.getRecipeCategories(focus)) {
				if (category.getUid().equals("refraction.assembly_table")
						|| category.getUid().equals(VanillaRecipeCategoryUid.CRAFTING)) {
					List<IRecipeLayoutDrawable> layouts = getLayouts(registry, category, focus);
					if (!layouts.isEmpty())
						return layouts.get(0);
				}
			}
		}
		return null;
	}

	private <T extends IRecipeWrapper> List<IRecipeLayoutDrawable> getLayouts(IRecipeRegistry registry, IRecipeCategory<T> category, IFocus<ItemStack> focus) {
		List<IRecipeLayoutDrawable> layouts = new ArrayList<>();
		List<T> wrappers = registry.getRecipeWrappers(category, focus);
		for (T wrapper : wrappers) {
			IRecipeLayoutDrawable layout = registry.createRecipeLayoutDrawable(category, wrapper, focus);
			layouts.add(layout);
		}
		return layouts;
	}

	@FunctionalInterface
	public interface Parser {
		@Nullable
		ITextHolder parse(@NotNull JsonObject object);
	}
}
