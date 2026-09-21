package fr.madu59.quickplay.client.data;

import java.nio.file.Path;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerData.Type;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.world.level.storage.LevelResource;

public class PlayedWorldData {

    private String name;
    private String id;
    private boolean isClientLevel;
    private boolean isInitialized = false;
    private Path iconPath;
    private transient ServerData serverData;
    private Type type;
    private byte[] iconBytes;

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
            serverData = client.getCurrentServer();
            if (serverData == null) return;

            name = serverData.name;
            id = serverData.ip;
            type = serverData.type();
            iconBytes = serverData.getIconBytes();
        }
        isInitialized = true;
    }

    public PlayedWorldData(String name, String id, boolean isClientLevel, Path iconPath, Type type, byte[] iconBytes){
        this.name = name;
        this.id = id;
        this.isClientLevel = isClientLevel;
        this.iconPath = iconPath;
        this.type = type;
        this.iconBytes = iconBytes;
        this.isInitialized = true;
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

    public ServerData getServerData(){
        if(serverData == null) serverData = new ServerData(name, id, type);
        return serverData;
    }

    public Type getType(){
        return type;
    }

    public byte[] getIconBytes(){
        return iconBytes;
    }

    public void setIconBytes(byte[] iconBytes){
        this.iconBytes = iconBytes;
    }

    public Builder builder(){
        return new Builder();
    }

    public static class Builder{

        private String name = "Unknown";
        private String id = null;
        private boolean isClientLevel = true;
        private Path iconPath = Path.of("unknown");
        private Type type = Type.OTHER;
        private byte[] iconBytes = null;

        public PlayedWorldData build(){
            return new PlayedWorldData(name, id, isClientLevel, iconPath, type, iconBytes);
        }

        public Builder isClientLevel(boolean isClientLevel){
            this.isClientLevel = isClientLevel;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder iconPath(Path iconPath){
            this.iconPath = iconPath;
            return this;
        }

        public Builder iconPath(String iconPath){
            this.iconPath = Path.of(iconPath);
            return this;
        }

        public Builder type(Type type){
            this.type = type;
            return this;
        }

        public Builder iconBytes(byte[] iconBytes){
            this.iconBytes = iconBytes;
            return this;
        }
    }
}
