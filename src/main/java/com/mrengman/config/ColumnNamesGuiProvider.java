package com.mrengman.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.autoconfig.util.Utils;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ColumnNamesGuiProvider implements GuiProvider {

    public static void checkValidity(String newValue) throws CsvValidationException, ConfigData.ValidationException, IOException {

        CSVReader csvReader = new CSVReader(new StringReader(newValue));
        String[] values = csvReader.readNext();
        if(values.length < 3 || values.length > 4 || Arrays.stream(values).anyMatch((s) -> s.strip().isEmpty())) {
            throw new ConfigData.ValidationException("");
        }

    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<AbstractConfigListEntry> get(String i13n, Field field, Object config, Object defaults, GuiRegistryAccess registry) {
        return Collections.singletonList(
                ConfigEntryBuilder.create()
                        .startStrField(Text.translatable(i13n), Utils.getUnsafely(field, config))
                        .setDefaultValue(() -> Utils.getUnsafely(field, defaults))
                        .setErrorSupplier(newValue -> {
                            try {
                                checkValidity(newValue);
                                return Optional.empty();
                            } catch (Exception e) {
                                return Optional.of(Text.translatable("text.shopslookup.option.defaultSpreadsheetColumnNames.error"));
                            }
                        })
                        .setSaveConsumer(newValue -> Utils.setUnsafely(field, config, newValue))
                        .build()
        );
    }
}
