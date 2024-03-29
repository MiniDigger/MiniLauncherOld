package me.minidigger.launcher;

import java.awt.*;

/* 
 * http://wiki.vg/Authentication
 * http://wiki.vg/Game_Files
 */

public class LauncherConstants {

    ////////////////////////////////
    //                            //
    //          LAUNCHER          //
    //                            //
    ////////////////////////////////

	/* Launcher globals options. */

    public static final String LAUNCHER_NAME = "MiniLauncher";
    public static final String LAUNCHER_VERSION = "1.0.0";
    public static final String LAUNCHER_STATUS = "BETA";
    public static final String[] LAUNCHER_AUTHORS = new String[]{"MiniDigger"};
    public static final String LAUNCHER_PREFIX = "[Launcher] ";
    public static final Font LAUNCHER_FONT = loadFontFromRes(Font.TRUETYPE_FONT, "/Ubuntu-L.ttf", 13f);
    public static final Image LAUNCHER_ICON = loadImageFromRes("/Icon.png");
    public static final Image LAUNCHER_IMAGE = loadImageFromRes("/Skyolauncher.png");

	/* Client options. */

    public static final int CLIENT_VERSION = 14;

    ////////////////////////////////
    //                            //
    //           TASKS            //
    //                            //
    ////////////////////////////////

	/* AuthUser options. */

    public static final String AUTH_USER_PREFIX = "[Authentication] ";
    public static final String AUTHENTICATION_URL = "https://authserver.mojang.com/authenticate";

	/* AutoUpdater options. */

    public static final String AUTO_UPDATER_PREFIX = "[Updater] ";
    public static final String LATEST_VERSION_TXT = "http://www.skyost.eu/skyolauncher/latest.txt";
    public static final String LATEST_VERSION_JAR = "http://www.skyost.eu/skyolauncher/download.php";

	/* ConnectivityChecker options. */

    public static final String CONNECTIVITY_CHECKER_PREFIX = "[Connectivity] ";
    public static final String[] CONNECTIVITY_CHECKER_URLS = new String[]{"http://www.google.com", "http://www.facebook.com", "http://minilauncher.minidigger.me/"};

	/* ChangelogDownloader options. */

    public static final String CHANGELOG_DOWNLOADER_PREFIX = "[Changelog] ";
    public static final String CHANGELOG_URL = "http://s3.amazonaws.com/Minecraft.Download/blocknotes.txt";

	/* GameTasks options. */

    public static final String GAME_TASKS_PREFIX = "[Game Tasks] ";
    public static final String LIBS_SUFFIX = "/libraries";
    public static final String LIBS_URL = "https:/" + LIBS_SUFFIX + ".minecraft.net";
    public static final String MINECRAFT_AWS_URL = "https://launchermeta.mojang.com/mc/game";
    public static final String MINECRAFT_RES_URL = "http://resources.download.minecraft.net";
    public static final String VERSIONS_SUFFIX = "/versions";
    public static final String ASSETS_SUFFIX = "/assets";
    public static final String ASSETS_OBJECTS_SUFFIX = "/objects";
    public static final String ASSETS_INDEXES_SUFFIX = "/indexes";
    public static final String ASSETS_VIRTUAL_SUFFIX = "/virtual";
    public static final String ASSETS_LEGACY_SUFFIX = "/legacy";
    public static final String MINECRAFT_SERVER_IP = "localhost"; // Leave null if you do not want to enable this option.
    public static final short MINECRAFT_SERVER_PORT = 25565;

	/* RefreshToken options. */

    public static final String REFRESH_TOKEN_PREFIX = "[Token refresher] ";
    public static final String REFRESH_TOKEN_URL = "https://authserver.mojang.com/refresh";

	/* ServiceStatus options. */

    public static final String STATUS_CHECK_URL = "http://status.mojang.com/check";

	/* StreamReader options. */

    public static final String MINECRAFT_OUTPUT_PREFIX = "[Minecraft output] ";
    public static final String MINECRAFT_ERRORS_PREFIX = "[Minecraft errors] ";

	/* UpdateVersions options. */

    public static final String UPDATE_VERSIONS_PREFIX = "[Versions] ";
    public static final String VERSIONS_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    public static final String[] VERSIONS_TYPES = new String[]{"snapshot", "release"};

	/* UserUUID options. */

    public static final String USER_UUID_PREFIX = "[User UUID] ";
    public static final String UUID_URL = "https://api.mojang.com/profiles/page/1";

    ////////////////////////////////
    //                            //
    //           OTHERS           //
    //                            //
    ////////////////////////////////

	/* Others things... */

    private static Font loadFontFromRes(final int format, final String path, final float size) {
        try {
            return Font.createFont(format, MiniLauncher.class.getResourceAsStream(path)).deriveFont(size);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Image loadImageFromRes(final String path) {
        return Toolkit.getDefaultToolkit().getImage(MiniLauncher.class.getResource(path));
    }

}
