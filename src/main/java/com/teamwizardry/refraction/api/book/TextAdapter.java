package com.teamwizardry.refraction.api.book;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author WireSegal
 *         Created at 5:01 PM on 11/21/16.
 */
public final class TextAdapter {

	private TextAdapter() {}

	@FunctionalInterface
	public interface Parser {
		@Nullable
		ITextHolder parse(@NotNull JsonObject object);
	}

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
			try {
				NBTTagCompound nbt = JsonToNBT.getTagFromJson(object.toString());
				stack = ItemStack.loadItemStackFromNBT(nbt);
			} catch (NBTException e) {
				return null;
			}

			return new StackTextHolder(stack);
		});
	}

	public static void registerAdapter(@NotNull String key, @NotNull Parser parser) {
		registry.put(key, parser);
	}

	private static final Parser BASE_PARSER = object -> {
		if (!object.has("text") || !object.get("text").isJsonPrimitive())
			return null;
		String text = object.get("text").getAsString();

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

		TextStyle style = new TextStyle()
				.setColor(color)
				.setBold(bold)
				.setItalic(italic)
				.setUnderlined(underline)
				.setObfuscated(obfuscated)
				.setStrikethrough(strikethrough);

		return new StringTextHolder(text).setStyle(style);
	};

	@NotNull
	public static ITextHolder adapt(@NotNull JsonElement object) {
		if (object.isJsonNull())
			return new StringTextHolder("NULL");
		else if (object.isJsonPrimitive())
			return new StringTextHolder(object.getAsString());
		else if (object.isJsonArray()) {
			List<ITextHolder> l = StreamSupport.stream(object.getAsJsonArray().spliterator(), false)
					.map(TextAdapter::adapt)
					.collect(Collectors.toList());

			return new CombinedTextHolder(l.toArray(new ITextHolder[l.size()]));
		} else if (object.isJsonObject()) {
			JsonObject comp = object.getAsJsonObject();
			if (comp.has("type") && comp.get("type").isJsonPrimitive()) {
				String type = comp.get("type").getAsString();
				Collection<Parser> parsers = registry.get(type);
				for (Parser parser : parsers) {
					ITextHolder parsed = parser.parse(comp);
					if (parsed != null) return parsed;
				}
			}

			ITextHolder parsed = BASE_PARSER.parse(comp);
			if (parsed != null) return parsed;
		}

		return new StringTextHolder("UNKNOWN DATA TYPE");
	}
}
