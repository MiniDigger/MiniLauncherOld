package me.minidigger.launcher.tasks;

import java.util.logging.Level;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.utils.ConnectionUtils;
import me.minidigger.launcher.utils.LogUtils;

public class ConnectivityChecker extends Thread {

    @Override
    public final void run() {
        LogUtils.log(Level.INFO, LauncherConstants.CONNECTIVITY_CHECKER_PREFIX + "Waiting for the connectivity checker...");
        try {
            MiniLauncher.isOnline = ConnectionUtils.isOnline(LauncherConstants.CONNECTIVITY_CHECKER_URLS);
        } catch (final Exception ex) {
            ex.printStackTrace();
            MiniLauncher.isOnline = false;
        }
        LogUtils.log(Level.INFO, LauncherConstants.CONNECTIVITY_CHECKER_PREFIX + "Done.");
    }

    public final void waitForThread() throws InterruptedException {
        this.join();
    }

}
