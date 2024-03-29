package me.minidigger.launcher;

import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.utils.JSONObject;

public class ProfilesManager {

    private static final HashMap<String, LauncherProfile> profiles = new HashMap<>();

    public static void addProfile(final LauncherProfile profile) {
        setProfile(profile.name, profile);
    }

    public static void removeProfileFromList(final String profileName) {
        profiles.remove(profileName);
    }

    public static void removeProfileFromList(final LauncherProfile profile) {
        for (final Entry<String, LauncherProfile> entry : profiles.entrySet()) {
            if (entry.getValue().equals(profile)) {
                profiles.remove(entry.getKey());
            }
        }
    }

    public static String getProfileName(final LauncherProfile profile) {
        for (final Entry<String, LauncherProfile> entry : profiles.entrySet()) {
            if (entry.getValue().equals(profile)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static LauncherProfile getProfile(final String profileName) {
        return profiles.get(profileName);
    }

    public static String[] getProfilesName() {
        final Set<String> keys = profiles.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public static LauncherProfile[] getProfiles() {
        final Collection<LauncherProfile> values = profiles.values();
        return values.toArray(new LauncherProfile[values.size()]);
    }

    public static boolean hasProfile(final String profileName) {
        return profiles.containsKey(profileName);
    }

    public static boolean hasProfile(final LauncherProfile profile) {
        return profiles.get(profile.name) != null;
    }

    public static void setProfile(final String profileName, final LauncherProfile profile) {
        profiles.put(profileName, profile);
    }

    public static class LauncherProfile extends JSONObject {

        public String name;
        public String user;
        public File gameDirectory;
        public String arguments;
        public String version;
        public boolean launcherVisible;
        public boolean logMinecraft;

        public LauncherProfile(final String name) throws JsonSyntaxException, IllegalArgumentException, IllegalAccessException, IOException {
            super(ObjectType.PROFILE, name);
            load();
        }

        public LauncherProfile(final String name, final User user, final File gameDirectory, final String arguments, final String version, final boolean launcherVisible, final boolean logMinecraft) {
            super(ObjectType.PROFILE, name);
            this.name = name;
            this.user = user.uuid;
            this.gameDirectory = gameDirectory;
            this.arguments = arguments;
            this.version = version;
            this.launcherVisible = launcherVisible;
            this.logMinecraft = logMinecraft;
        }

    }

}
