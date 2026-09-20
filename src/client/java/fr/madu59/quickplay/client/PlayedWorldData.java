package fr.madu59.quickplay.client;

import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;

public class PlayedWorldData {

    private String name;
    private String id;
    private boolean isClientLevel;
    private boolean isInitialized = false;
    private Path iconPath;

    public PlayedWorldData(Minecraft client){
        if(client.hasSingleplayerServer()){
            isClientLevel = true;
            IntegratedServer server = client.getSingleplayerServer();

            name = server.getWorldData().getLevelName();

            Path path = server.getWorldPath(LevelResource.ROOT);
            iconPath = server.getWorldPath(LevelResource.ICON_FILE);
            id = path.normalize().toFile().getName();
        }
        else{
            isClientLevel = false;
            ServerData serverData = client.getCurrentServer();
            if (serverData == null) return;

            name = serverData.name;
            id = serverData.ip;
        }
        isInitialized = true;
    }

    public String getId(){
        return id;
    }

    public String getServerName(){
        return name;
    }

    public boolean isClientLevel(){
        return isClientLevel;
    }

    public boolean isInitialized(){
        return isInitialized;
    }

    public Path getIconPath(){
        return iconPath;
    }
}
