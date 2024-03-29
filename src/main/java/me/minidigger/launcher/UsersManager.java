package me.minidigger.launcher;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import me.minidigger.launcher.ProfilesManager.LauncherProfile;
import me.minidigger.launcher.tasks.AuthUser.Property;
import me.minidigger.launcher.utils.JSONObject;

public class UsersManager {

    private static final HashMap<String, User> users = new HashMap<>();

    public static void addUser(final User user) {
        setUser(user.username, user);
    }

    public static void removeUserFromList(final String username) {
        removeUserFromList(username, true);
    }

    public static void removeUserFromList(final String username, final boolean fromLauncher) {
        final User user = getUser(username);
        if (user == null) {
            return;
        }
        for (final LauncherProfile profile : ProfilesManager.getProfiles()) {
            if (fromLauncher && profile.user != null && profile.user.equals(user.uuid)) {
                profile.user = null;
                profile.save();
            }
        }
        users.remove(username);
    }

    public static void removeUser(final User user) {
        for (final Entry<String, User> entry : users.entrySet()) {
            if (entry.getValue().equals(user)) {
                removeUserFromList(entry.getKey());
            }
        }
    }

    public static User getUser(final String username) {
        return users.get(username);
    }

    public static User getUserByID(final String id) {
        for (final User user : users.values()) {
            if (user.uuid.equals(id)) {
                return user;
            }
        }
        return null;
    }

    public static User getUserByAccountName(final String accountName) {
        for (final User user : users.values()) {
            if (user.accountName.equals(accountName)) {
                return user;
            }
        }
        return null;
    }

    public static String[] getUsernames() {
        final Set<String> keys = users.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public static User[] getUsers() {
        final Collection<User> values = users.values();
        return values.toArray(new User[values.size()]);
    }

    public static boolean hasUser(final String username) {
        return users.containsKey(username);
    }

    public static boolean hasUser(final User user) {
        return users.get(user.username) != null;
    }

    public static void setUser(final String username, final User user) {
        users.put(username, user);
    }

    public static class User extends JSONObject {

        public String username;
        public String uuid;
        public String accountName; // For migrated accounts.
        public boolean isOnline;
        public String accessToken;
        public List<Property> properties = new ArrayList<>();

        public User(final String uuid) throws JsonSyntaxException, IllegalArgumentException, IllegalAccessException, IOException {
            super(ObjectType.USER, uuid);
            load();
        }

        public User(final String username, final String uuid, final String accountName, final boolean isOnline, final String accessToken, final List<Property> properties) {
            super(ObjectType.USER, uuid);
            this.username = username;
            this.uuid = uuid;
            this.accountName = accountName;
            this.isOnline = isOnline;
            this.accessToken = accessToken;
            if (properties == null) {
                this.properties = new ArrayList<>();
            } else {
                this.properties = properties;
            }
        }
    }
}
