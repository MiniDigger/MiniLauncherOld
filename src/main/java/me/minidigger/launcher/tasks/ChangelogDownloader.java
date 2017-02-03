package me.minidigger.launcher.tasks;

import java.util.logging.Level;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.frames.ChangelogFrame;
import me.minidigger.launcher.utils.ConnectionUtils;
import me.minidigger.launcher.utils.LogUtils;

public class ChangelogDownloader extends Thread {

    @Override
    public final void run() {
        final ChangelogFrame frame = ChangelogFrame.getInstance();
        if (!frame.isChangeLogDownloaded()) {
            try {
                LogUtils.log(Level.INFO, LauncherConstants.CHANGELOG_DOWNLOADER_PREFIX + "Downloading changelog...");
                final String changelog = ConnectionUtils.httpGet(LauncherConstants.CHANGELOG_URL, System.lineSeparator());
                LogUtils.log(Level.INFO, LauncherConstants.CHANGELOG_DOWNLOADER_PREFIX + "Done.");
                frame.setChangelog(changelog);
                frame.setChangeLogDownloaded(true);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }
        frame.setVisible(true);
    }

}
