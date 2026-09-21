package fr.madu59.quickplay.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import fr.madu59.quickplay.QuickPlay;
import fr.madu59.quickplay.client.platform.PlatformHelper;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PlayedWorldDataStorage {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeHierarchyAdapter(Path.class, new PathAdapter())
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .create();

    private static final Path SAVE_PATH = PlatformHelper.getConfigDir().resolve(QuickPlay.MOD_ID + "/save.json");

    private static final Type LIST_TYPE = new TypeToken<List<PlayedWorldData>>() {}.getType();

    public static void save(List<PlayedWorldData> data) {
        if(data.isEmpty()) return;
        try (Writer writer = Files.newBufferedWriter(SAVE_PATH)) {
            GSON.toJson(data, LIST_TYPE, writer);
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    public static List<PlayedWorldData> load() {
        if (!Files.isRegularFile(SAVE_PATH)) return List.of();
        try (Reader reader = Files.newBufferedReader(SAVE_PATH)) {
            List<PlayedWorldData> result = GSON.fromJson(reader, LIST_TYPE);
            return result != null ? result : List.of();
        }
        catch(IOException e){
            return List.of();
        }
    }

    private static class PathAdapter implements JsonSerializer<Path>, JsonDeserializer<Path> {
        @Override
        public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Path.of(json.getAsString());
        }
    }
}