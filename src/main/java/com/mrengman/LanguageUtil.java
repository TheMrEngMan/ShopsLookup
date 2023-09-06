package com.mrengman;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Language;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

// Based on net.minecraft.util.Language
public class LanguageUtil {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d.]*[df]");
    public static Language englishLanguageInstance = create();

    private static Language create() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        load(biConsumer);
        final Map<String, String> map = builder.build();
        return new Language() {
            public String get(String key, String fallback) {
                return map.getOrDefault(key, fallback);
            }
            public boolean hasTranslation(String key) {
                return map.containsKey(key);
            }
            public boolean isRightToLeft() {
                return false;
            }
            public OrderedText reorder(StringVisitable text) {
                return (visitor) -> text.visit((style, string) -> TextVisitFactory.visitFormatted(string, style, visitor) ? Optional.empty() : StringVisitable.TERMINATE_VISIT, Style.EMPTY).isPresent();
            }
        };
    }

    private static void load(BiConsumer<String, String> entryConsumer) {
        try {
            InputStream inputStream = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");

            try {
                load(inputStream, entryConsumer);
            } catch (Throwable e) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable e2) {
                        e2.addSuppressed(e2);
                    }
                }

                throw e;
            }

            if (inputStream != null) {
                inputStream.close();
            }
        } catch (Exception e) {
            LOGGER.error("Couldn't read strings from {}", "en_us", e);
        }

    }

    public static void load(InputStream inputStream, BiConsumer<String, String> entryConsumer) {
        JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonObject.entrySet()) {
            String string = TOKEN_PATTERN.matcher(JsonHelper.asString(stringJsonElementEntry.getValue(), stringJsonElementEntry.getKey())).replaceAll("%$1s");
            entryConsumer.accept(stringJsonElementEntry.getKey(), string);
        }
    }

}
