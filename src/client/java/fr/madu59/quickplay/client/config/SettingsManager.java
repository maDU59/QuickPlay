package fr.madu59.quickplay.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.madu59.quickplay.QuickPlay;
import fr.madu59.quickplay.client.config.SettingsManager;
import fr.madu59.quickplay.client.platform.PlatformHelper;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;

public class SettingsManager {

    public static Map<String, Option<?>> ALL_OPTIONS = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = PlatformHelper.getConfigDir().resolve(QuickPlay.MOD_ID + "/config.json");
    private static Map<String, String> loadedSettings = loadSettings();

    private static Runnable EMPTY_ACTION = () -> {};

    public static Option<Boolean> CONTINUE_BUTTON = loadOptionWithDefaults(
        "continue_button", 
        "quickplay.config.continue_button", 
        true
    );

    public static Option<Boolean> QUICKPLAY_BUTTONS = loadOptionWithDefaults(
        "quickplay_buttons", 
        "quickplay.config.quickplay_buttons", 
        true
    );

    public static Option<Integer> QUICKPLAY_BUTTONS_COUNT = loadOptionWithDefaults(
        "quickplay_buttons_count", 
        "quickplay.config.quickplay_buttons_count", 
        5
    );

    public static void saveSettings() {
        Map<String, String> map = toMap(ALL_OPTIONS.values());
        Set<Runnable> actions = new HashSet<Runnable>();

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(map, writer);
            }
            Set<String> allEntries = new HashSet<>();

            allEntries.addAll(map.keySet());
            allEntries.addAll(loadedSettings.keySet());
            for(String key : allEntries){
                if(!Objects.equals(map.get(key), loadedSettings.get(key))){
                    if(SettingsManager.ALL_OPTIONS.containsKey(key)){
                        Runnable action = SettingsManager.ALL_OPTIONS.get(key).getRunnable();
                        if(actions.add(action)) action.run();
                    }
                }
            }
            if(map != null) loadedSettings = map;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, String> toMap(Collection<Option<?>> options) {
        Map<String, String> map = new LinkedHashMap<>();
        for (Option<?> option : options) {
            if (option.getValue() != option.getDefaultValue()) map.put(option.getId(), option.getValue().toString());
        }
        return map;
    }

    private static Map<String, String> loadSettings() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> map = GSON.fromJson(reader, type);
            return map;
        } catch (Exception e) {
            QuickPlay.LOGGER.info("[QuickPlay] Config file not found or invalid, using default");
            return new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOptionValue(String key, T defaultValue) {
        if (loadedSettings == null || !loadedSettings.containsKey(key)) return null;
        else if (defaultValue instanceof Enum<?> e){
            return (T) Enum.valueOf(e.getDeclaringClass(), loadedSettings.get(key));
        }
        else if (defaultValue instanceof Float){
            return (T) Float.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Double){
            return (T) Double.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Integer){
            return (T) Integer.valueOf(loadedSettings.get(key));
        }
        else if (defaultValue instanceof Boolean){
            return (T) (Boolean) Boolean.parseBoolean(loadedSettings.get(key));
        }
        else return null;
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, T defaultValue) {
        return loadOptionWithDefaults(id, name, name, defaultValue, EMPTY_ACTION);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, T defaultValue, Runnable action) {
        return loadOptionWithDefaults(id, name, name, defaultValue, action);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue) {
        return loadOptionWithDefaults(id, name, description, defaultValue, EMPTY_ACTION);
    }

    private static <T> Option<T> loadOptionWithDefaults(String id, String name, String description, T defaultValue, Runnable action) {
        T optionValue= getOptionValue(id, defaultValue);
        if (optionValue == null) optionValue = defaultValue;
        Option<T> option = new Option<T>(
                id,
                name,
                description,
                optionValue,
                defaultValue,
                action
        );
        return option;
    }
}