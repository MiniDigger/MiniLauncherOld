package me.minidigger.launcher.frames;

import java.awt.*;
import javax.swing.*;

import me.minidigger.launcher.LauncherConstants;

public class ChangelogFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    private static ChangelogFrame instance;
    private final JTextArea txtChangeLog = new JTextArea();
    private boolean changeLogDownloaded = false;

    public ChangelogFrame() {
        this.setIconImage(LauncherConstants.LAUNCHER_ICON);
        this.setSize(406, 346);
        this.setTitle("Changelog");
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.getContentPane().add(new JScrollPane(txtChangeLog), BorderLayout.CENTER);
    }

    public static ChangelogFrame getInstance() {
        if (instance == null) {
            instance = new ChangelogFrame();
        }
        return instance;
    }

    public void setChangelog(final String changelog) {
        txtChangeLog.setText(changelog);
    }

    public void setChangeLogDownloaded(boolean changeLogDownloaded) {
        this.changeLogDownloaded = changeLogDownloaded;
    }

    public boolean isChangeLogDownloaded() {
        return changeLogDownloaded;
    }

}