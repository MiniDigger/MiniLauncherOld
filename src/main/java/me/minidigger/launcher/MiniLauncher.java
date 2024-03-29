package me.minidigger.launcher;

import com.pagosoft.plaf.PgsLookAndFeel;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import me.minidigger.launcher.ProfilesManager.LauncherProfile;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.frames.ConsoleFrame;
import me.minidigger.launcher.frames.LauncherFrame;
import me.minidigger.launcher.tasks.AutoUpdater;
import me.minidigger.launcher.tasks.ConnectivityChecker;
import me.minidigger.launcher.tasks.RefreshToken;
import me.minidigger.launcher.utils.JSONObject.ObjectType;
import me.minidigger.launcher.utils.LogUtils;
import me.minidigger.launcher.utils.LogUtils.ErrorOutputStream;
import me.minidigger.launcher.utils.SystemManager;
import me.minidigger.launcher.utils.SystemManager.OS;
import me.minidigger.launcher.utils.Utils;

public class MiniLauncher {

    public static final SystemManager SYSTEM = new SystemManager();

    public static LauncherConfig config;
    public static ConsoleFrame console;
    public static Boolean isOnline;

    public static void main(final String[] args) {
        try {
            final ConnectivityChecker checker = new ConnectivityChecker();
            checker.start();
            checker.waitForThread();
            LogUtils.log(null, null);
            config = new LauncherConfig("launcher");
            final File mcDir = SYSTEM.getMinecraftDirectory();
            mcDir.mkdirs();
            final List<String> argsList = Arrays.asList(args);
            PgsLookAndFeel.setCurrentTheme(new LauncherTheme());
            UIManager.setLookAndFeel(new PgsLookAndFeel());
            Utils.setUIFont(new FontUIResource(LauncherConstants.LAUNCHER_FONT));
            if (SYSTEM.getPlatform().getOS() == OS.LINUX) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
            }
            final File appDir = SYSTEM.getApplicationDirectory();
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            if (argsList.contains("-console")) {
                console = new ConsoleFrame();
                console.setVisible(true);
            }
            System.setErr(new PrintStream(new ErrorOutputStream()));
            LogUtils.log(Level.INFO, LauncherConstants.LAUNCHER_PREFIX + Utils.buildTitle(isOnline));
            LogUtils.log(null, null);
            LogUtils.log(Level.INFO, LauncherConstants.LAUNCHER_PREFIX + "Loading profiles...");
            if (ObjectType.PROFILE.directory.exists()) {
                for (final File profileFile : ObjectType.PROFILE.directory.listFiles()) {
                    final String fileName = profileFile.getName();
                    final LauncherProfile profile = new LauncherProfile(fileName.substring(0, fileName.lastIndexOf(".")));
                    ProfilesManager.addProfile(profile);
                }
            } else {
                ObjectType.PROFILE.directory.mkdirs();
            }
            LogUtils.log(Level.INFO, LauncherConstants.LAUNCHER_PREFIX + "Done.");
            LogUtils.log(Level.INFO, LauncherConstants.LAUNCHER_PREFIX + "Loading users...");
            final List<User> onlineUsers = new ArrayList<>();
            if (ObjectType.USER.directory.exists()) {
                for (final File userFile : ObjectType.USER.directory.listFiles()) {
                    final String fileName = userFile.getName();
                    final User user = new User(fileName.substring(0, fileName.lastIndexOf(".")));
                    UsersManager.addUser(user);
                    if (user.isOnline) {
                        onlineUsers.add(user);
                    }
                }
            } else {
                ObjectType.USER.directory.mkdir();
            }
            LogUtils.log(Level.INFO, LauncherConstants.LAUNCHER_PREFIX + "Done.");
            new LauncherFrame().setVisible(true);
            if (onlineUsers.size() != 0) {
                new RefreshToken(onlineUsers.toArray(new User[onlineUsers.size()])).start();
            }
            new AutoUpdater().start();
        } catch (final Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getClass().getName(), "Error !", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

}
