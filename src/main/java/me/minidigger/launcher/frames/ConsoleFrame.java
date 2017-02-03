package me.minidigger.launcher.frames;

import java.awt.*;
import javax.swing.*;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.utils.LogUtils;

public class ConsoleFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public ConsoleFrame() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(LauncherConstants.LAUNCHER_ICON);
        this.setTitle("Console");
        this.setType(Type.POPUP);
        this.setPreferredSize(new Dimension(510, 330));
        this.setLocation(MiniLauncher.config.consolePointX, MiniLauncher.config.consolePointY);
        final JTextArea txtLogs = new JTextArea();
        txtLogs.setEditable(false);
        txtLogs.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        txtLogs.setBackground(Color.BLACK);
        txtLogs.setForeground(Color.WHITE);
        txtLogs.setWrapStyleWord(true);
        this.getContentPane().add(new JScrollPane(txtLogs), BorderLayout.CENTER);
        LogUtils.setTextArea(txtLogs);
        this.pack();
    }
}
