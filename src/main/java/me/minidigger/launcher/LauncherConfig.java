package me.minidigger.launcher;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.UUID;

import me.minidigger.launcher.utils.JSONObject;

public class LauncherConfig extends JSONObject {

    public int launcherPointX = 0;
    public int launcherPointY = 0;
    public int consolePointX = 0;
    public int consolePointY = 0;
    public String latestProfile = null;
    public boolean vanillaDataImported = false;
    public String clientToken = UUID.randomUUID().toString();

    public LauncherConfig(final String name) throws JsonSyntaxException, IllegalArgumentException, IllegalAccessException, IOException {
        super(ObjectType.CONFIG, name);
        load();
        save();
    }

}