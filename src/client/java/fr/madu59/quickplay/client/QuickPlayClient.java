package fr.madu59.quickplay.client;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;

public class QuickPlayClient implements ClientModInitializer {

	private static List<PlayedWorldData> lastPlayed = new ArrayList<>();

	public static int LAST_PLAYED_BUTTONS_COUNT = 5;

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register((_, _, client) -> {
            addPlayed(new PlayedWorldData(client));
        });
	}

	public static void resumeLastPlayed(){
		resumeLastPlayed(0);
	}

	public static void resumeLastPlayed(int i){
		Minecraft client = Minecraft.getInstance();
		if(lastPlayed.size() > i){
			PlayedWorldData data = lastPlayed.get(i);
			if (data.getId() == null || !client.getLevelSource().levelExists(data.getId())) {
				client.setScreenAndShow(new SelectWorldScreen(new TitleScreen()));
				return;
			}

			client.createWorldOpenFlows().openWorld(
				data.getId(),
				() -> client.setScreenAndShow(new TitleScreen())
			);
			return;
		}
		else{
			client.setScreenAndShow(new SelectWorldScreen(new TitleScreen()));
		}
	}

	public static @Nullable PlayedWorldData getLastPlayed(int i){
		if(lastPlayed.size() > i){
			return lastPlayed.get(i);
		}
		return null;
	}

	public static void addPlayed(PlayedWorldData data){
		if(!data.isInitialized()) return;

		lastPlayed.removeIf((k) -> k.getId().equals(data.getId()));

		lastPlayed.addFirst(data);
		saveLastPlayed();
	}

	public static void saveLastPlayed(){

	}

	public static void loadLastPlayed(){

	}
}