package com.teamwizardry.refraction.api.book;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.teamwizardry.librarianlib.LibrarianLog;
import com.teamwizardry.librarianlib.common.util.NBTTypes;
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
		return parseJsonToNBT(element, 0);
	}

	private static NBTBase parseJsonToNBT(JsonElement element, int forceType) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive prim = element.getAsJsonPrimitive();
			if (prim.isBoolean())
				return new NBTTagByte((byte) (prim.getAsBoolean() ? 1 : 0));
			else if (prim.isString()) {
				switch (forceType) {
					case 0:
					case 8:
						return new NBTTagString(prim.getAsString());
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
					case 6:
						return parseNumber(new LazilyParsedNumber(prim.getAsString()), forceType);
					default:
						throw new IllegalArgumentException("NBT forced type doesn't conform! Expected: {8}u[0,6] Found: " + forceType);
				}
			} else {
				return parseNumber(prim.getAsNumber(), forceType);
			}
		} else if (element.isJsonNull())
			switch (forceType) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
					return parseNumber(0, forceType);
				case 7:
					return new NBTTagByteArray(new byte[0]);
				case 9:
					return new NBTTagList();
				case 10:
					return new NBTTagCompound();
				case 11:
					return new NBTTagIntArray(new int[0]);
				default:
					return new NBTTagString("");
			}
		else if (element.isJsonArray()) {
			switch (forceType) {
				case 7:
					return parseByteArr(element.getAsJsonArray());
				case 0:
				case 9:
					return parseList(element.getAsJsonArray());
				case 11:
					return parseIntArr(element.getAsJsonArray());
				default:
					throw new IllegalArgumentException("NBT forced type doesn't conform! Expected: {0,7,9,11} Found: " + forceType);
			}
		} else if (element.isJsonObject()) {
			if (forceType != 0 && forceType != 10)
				throw new IllegalArgumentException("NBT forced type doesn't conform! Expected: {0,10} Found: " + forceType);

			NBTTagCompound comp = new NBTTagCompound();
			JsonObject obj = element.getAsJsonObject();
			for (Map.Entry<String, JsonElement> keypair : obj.entrySet())
				comp.setTag(keypair.getKey(), parseJsonToNBT(keypair.getValue()));
			return comp;
		}

		return new NBTTagString("UNKNOWN DATA TYPE");
	}

	private static NBTBase parseNumber(Number num, int forceType) {
		switch (forceType) {
			case 1:
				return new NBTTagByte(num.byteValue());
			case 2:
				return new NBTTagShort(num.shortValue());
			case 3:
				return new NBTTagInt(num.intValue());
			case 4:
				return new NBTTagLong(num.longValue());
			case 5:
				return new NBTTagFloat(num.floatValue());
			case 6:
				return new NBTTagDouble(num.doubleValue());
			case 0:
				break;
			default:
				throw new IllegalArgumentException("NBT forced type doesn't conform! Expected: [0,6] Found: " + forceType);
		}

		if (num instanceof Long)
			return new NBTTagLong(num.longValue());
		else if (num instanceof Short)
			return new NBTTagShort(num.shortValue());
		else if (num instanceof Byte)
			return new NBTTagByte(num.byteValue());
		else if (num instanceof Float)
			return new NBTTagFloat(num.floatValue());
		else if (num instanceof Double)
			return new NBTTagDouble(num.doubleValue());

		return new NBTTagInt(num.intValue());
	}

	private static NBTBase parseList(JsonArray arr) {
		NBTTagList ret = new NBTTagList();
		byte type = 0;
		for (JsonElement el : arr) {
			if (type == 0 && el.isJsonObject() && el.getAsJsonObject().has("type-force") && el.getAsJsonObject().get("type-force").isJsonPrimitive()) {
				String forceName = el.getAsJsonObject().get("type-force").getAsString();
				if (el.getAsJsonObject().entrySet().size() == 1) {
					switch (forceName) {
						case "long":
							type = NBTTypes.LONG;
							break;
						case "int":
							type = NBTTypes.INT;
							break;
						case "int-arr":
							return parseIntArr(arr);
						case "short":
							type = NBTTypes.SHORT;
							break;
						case "byte":
							type = NBTTypes.BYTE;
							break;
						case "byte-arr":
							return parseByteArr(arr);
						case "float":
							type = NBTTypes.FLOAT;
							break;
						case "double":
							type = NBTTypes.DOUBLE;
							break;
					}
				}
				continue;
			}

			NBTBase nbt = parseJsonToNBT(el, type);
			if (type == 0)
				type = nbt.getId();
			else if (type != nbt.getId()) {
				LibrarianLog.INSTANCE.warn("Json uses multiple types in an nbt block!");
				continue;
			}
			ret.appendTag(nbt);
		}
		return ret;
	}

	private static NBTTagIntArray parseIntArr(JsonArray arr) {
		List<Integer> l = new ArrayList<>();
		for (JsonElement el : arr) {
			if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber())
				l.add(el.getAsInt());
		}

		int[] ret = new int[l.size()];
		int i = 0;
		for (Integer integer : l) ret[i++] = integer;

		return new NBTTagIntArray(ret);
	}

	private static NBTTagByteArray parseByteArr(JsonArray arr) {
		List<Byte> l = new ArrayList<>();
		for (JsonElement el : arr) {
			if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isNumber())
				l.add(el.getAsByte());
		}

		byte[] ret = new byte[l.size()];
		int i = 0;
		for (Byte b : l) ret[i++] = b;

		return new NBTTagByteArray(ret);
	}

	public static void registerAdapter(@NotNull String key, @NotNull Parser parser) {
		registry.put(key, parser);
	}

	private static TextStyle getStyleFromObject(JsonObject object) {
		TextFormatting color = object.has("color") && object.get("color").isJsonPrimitive() && object.getAsJsonPrimitive("color").isString() ?
				TextFormatting.getValueByName(object.get("color").getAsString()) : null;

		boolean bold = object.has("bold") &&
				object.get("bold").isJsonPrimitive() &&
				object.getAsJsonPrimitive("bold").getAsBoolean();
		boolean italic = object.has("italic") &&
				object.get("italic").isJsonPrimitive() &&
				object.getAsJsonPrimitive("italic").getAsBoolean();
		boolean underline = object.has("underlined") &&
				object.get("underlined").isJsonPrimitive() &&
				object.getAsJsonPrimitive("underlined").getAsBoolean();
		boolean obfuscated = object.has("obfuscated") &&
				object.get("obfuscated").isJsonPrimitive() &&
				object.getAsJsonPrimitive("obfuscated").getAsBoolean();
		boolean strikethrough = object.has("strikethrough") &&
				object.get("strikethrough").isJsonPrimitive() &&
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
