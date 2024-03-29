package me.minidigger.launcher.tasks;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.frames.UserFrame;
import me.minidigger.launcher.utils.ConnectionUtils;
import me.minidigger.launcher.utils.LogUtils;

public class AuthUser extends Thread {

    private final String username;
    private final String password;
    private final UserFrame parent;

    public AuthUser(final String username, final String password, final UserFrame parent) {
        this.username = username;
        this.password = password;
        this.parent = parent;
    }

    @Override
    public void run() {
        LogUtils.log(Level.INFO, LauncherConstants.AUTH_USER_PREFIX + "Authenticating an user :");
        LogUtils.log(Level.INFO, LauncherConstants.AUTH_USER_PREFIX + "Username : " + username);
        LogUtils.log(Level.INFO, LauncherConstants.AUTH_USER_PREFIX + "Password : " + password.replaceAll(".", "x"));
        try {
            final Gson gson = new Gson();
            final AuthSession session = gson.fromJson(ConnectionUtils.httpJsonPost(LauncherConstants.AUTHENTICATION_URL, gson.toJson(new AuthRequest(username, password, MiniLauncher.config.clientToken))), AuthSession.class);
            LogUtils.log(Level.INFO, LauncherConstants.AUTH_USER_PREFIX + "Done.");
            parent.saveAndNotifyListeners(new User(session.selectedProfile.name, session.selectedProfile.id, username, true, session.accessToken, session.user.properties));
        } catch (final Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Failed to login !", "Error !", JOptionPane.ERROR_MESSAGE);
            parent.btnLogIn.setEnabled(true);
            parent.btnLogIn.setText("Save");
        }
    }

    public class AuthRequest {

        public Agent agent = new Agent();
        public String username;
        public String password;
        public String clientToken;
        public boolean requestUser = true;

        public AuthRequest(final String username, final String password, final String clientToken) {
            this.username = username;
            this.password = password;
            this.clientToken = clientToken;
        }

    }

    public class Agent {

        public String name = "Minecraft";
        public int version = 1;

    }

    public static class SimpleSession {

        public String accessToken;
        public String clientToken;
        public boolean requestUser = true;

        public SimpleSession(final String accessToken, final String clientToken) {
            this.accessToken = accessToken;
            this.clientToken = clientToken;
        }

    }

    public class AuthSession extends SimpleSession {

        public Profile selectedProfile;
        public UserProperties user;

        public AuthSession(final String accessToken, final String clientToken) {
            super(accessToken, clientToken);
        }

    }

    public class Property {

        public String name;
        public String value;

    }

    public class UserProperties {

        public List<Property> properties = new ArrayList<>();

    }

    public static class Profile {

        public String id;
        public String name;

        public Profile(final String id, final String name) {
            this.id = id;
            this.name = name;
        }

    }

}
