package me.minidigger.skyolauncher;

import com.pagosoft.plaf.PgsTheme;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

public class LauncherTheme extends PgsTheme {

    public LauncherTheme() {
        super("Minilauncher Theme",
                new ColorUIResource(242, 241, 238),
                new ColorUIResource(115, 107, 82),
                new ColorUIResource(156, 156, 123),
                new ColorUIResource(35, 33, 29),
                new ColorUIResource(Color.LIGHT_GRAY),
                new ColorUIResource(92, 87, 76),
                Color.WHITE,
                new Color(92, 87, 76));
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
    }

}
