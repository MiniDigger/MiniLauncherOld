package me.minidigger.skyolauncher.frames;

import java.awt.*;
import javax.swing.*;

import me.minidigger.skyolauncher.LauncherConstants;
import me.minidigger.skyolauncher.Skyolauncher;
import me.minidigger.skyolauncher.utils.LogUtils;

public class ConsoleFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public ConsoleFrame() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setIconImage(LauncherConstants.LAUNCHER_ICON);
        this.setTitle("Console");
        this.setType(Type.POPUP);
        this.setPreferredSize(new Dimension(510, 330));
        this.setLocation(Skyolauncher.config.consolePointX, Skyolauncher.config.consolePointY);
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
