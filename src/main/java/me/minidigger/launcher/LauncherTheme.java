package me.minidigger.launcher;

import com.pagosoft.plaf.PgsTheme;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class LauncherTheme extends PgsTheme {

    public LauncherTheme() {
        super("Minilauncher Theme",
                new ColorUIResource(242, 241, 238), // selected
                new ColorUIResource(115, 107, 82), // button active
                new ColorUIResource(156, 156, 123),// button focus
                new ColorUIResource(35, 33, 29),// elem borders
                new ColorUIResource(192, 192, 192),// "grayed" out
                new ColorUIResource(92, 87, 76),// button
                new ColorUIResource(255, 255, 255),// font
                new ColorUIResource(92, 87, 76));// text input bg
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
    }

}
