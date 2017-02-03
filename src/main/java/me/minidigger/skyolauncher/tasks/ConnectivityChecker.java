package me.minidigger.skyolauncher.tasks;

import java.util.logging.Level;

import me.minidigger.skyolauncher.LauncherConstants;
import me.minidigger.skyolauncher.Skyolauncher;
import me.minidigger.skyolauncher.utils.ConnectionUtils;
import me.minidigger.skyolauncher.utils.LogUtils;

public class ConnectivityChecker extends Thread {
	
	@Override
	public final void run() {
		LogUtils.log(Level.INFO, LauncherConstants.CONNECTIVITY_CHECKER_PREFIX + "Waiting for the connectivity checker...");
		try {
			Skyolauncher.isOnline = ConnectionUtils.isOnline(LauncherConstants.CONNECTIVITY_CHECKER_URLS);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			Skyolauncher.isOnline = false;
		}
		LogUtils.log(Level.INFO, LauncherConstants.CONNECTIVITY_CHECKER_PREFIX + "Done.");
	}
	
	public final void waitForThread() throws InterruptedException {
		this.join();
	}

}
