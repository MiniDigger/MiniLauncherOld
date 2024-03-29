package me.minidigger.launcher.tasks;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.frames.UserFrame;
import me.minidigger.launcher.tasks.AuthUser.AuthSession;
import me.minidigger.launcher.tasks.AuthUser.SimpleSession;
import me.minidigger.launcher.utils.ConnectionUtils;
import me.minidigger.launcher.utils.LogUtils;

public class RefreshToken extends Thread {

    private final User[] users;
    private static final List<RefreshTokenListener> listeners = new ArrayList<>();

    public RefreshToken(final User... users) {
        this.users = users;
    }

    @Override
    public void run() {
        for (final RefreshTokenListener listener : listeners) {
            listener.onTokenTaskBegin();
        }
        final HashMap<User, AuthSession> result = new HashMap<>();
        if (MiniLauncher.isOnline) {
            try {
                final Gson gson = new Gson();
                for (final User user : users) {
                    LogUtils.log(Level.INFO, LauncherConstants.REFRESH_TOKEN_PREFIX + "Refreshing access token for " + user.accountName + "...");
                    final String response = ConnectionUtils.httpJsonPost(LauncherConstants.REFRESH_TOKEN_URL, gson.toJson(new SimpleSession(user.accessToken, MiniLauncher.config.clientToken)));
                    final AuthSession session = gson.fromJson(response, AuthSession.class);
                    if (session.accessToken != null && session.clientToken != null) {
                        result.put(user, session);
                    } else {
                        final MojangError error = gson.fromJson(response, MojangError.class);
                        LogUtils.log(Level.SEVERE, LauncherConstants.REFRESH_TOKEN_PREFIX + "Unable to login : " + error.error);
                        LogUtils.log(Level.SEVERE, LauncherConstants.REFRESH_TOKEN_PREFIX + "Message : " + error.errorMessage);
                        if (error.cause != null) {
                            LogUtils.log(Level.SEVERE, LauncherConstants.REFRESH_TOKEN_PREFIX + "Cause : " + error.cause);
                        }
                        new UserFrame(null, user.accountName).setVisible(true);
                    }
                    LogUtils.log(Level.INFO, LauncherConstants.REFRESH_TOKEN_PREFIX + "Done.");
                }
            } catch (final Exception ex) {
                result.clear();
                ex.printStackTrace();
            }
        } else {
            LogUtils.log(Level.WARNING, LauncherConstants.REFRESH_TOKEN_PREFIX + "Cannot refresh your access token because you are offline !");
        }
        for (final RefreshTokenListener listener : listeners) {
            listener.onTokenTaskFinished(result);
        }
    }

    public static void addListener(final RefreshTokenListener listener) {
        listeners.add(listener);
    }

    public interface RefreshTokenListener {

        void onTokenTaskBegin();

        void onTokenTaskFinished(final HashMap<User, AuthSession> result);

    }

    public class MojangError {

        public String error;
        public String errorMessage;
        public String cause;

    }

}
