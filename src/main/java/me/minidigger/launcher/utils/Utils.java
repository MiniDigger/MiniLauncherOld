package me.minidigger.launcher.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.utils.SystemManager.OS;

public class Utils {

    public static String buildTitle(final boolean isOnline) {
        return (isOnline ? "" : "[OFFLINE] ") + LauncherConstants.LAUNCHER_NAME + " v" + LauncherConstants.LAUNCHER_VERSION + " " + LauncherConstants.LAUNCHER_STATUS + " - By " + StringUtils.join(LauncherConstants.LAUNCHER_AUTHORS, ' ');
    }

    public static String getFileChecksum(final File file, final MessageDigest digest) throws IOException {
        final FileInputStream input = new FileInputStream(file);
        final byte[] dataBytes = new byte[1024];
        int nread = 0;
        while ((nread = input.read(dataBytes)) != -1) {
            digest.update(dataBytes, 0, nread);
        }
        final byte[] mdbytes = digest.digest();
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            builder.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        input.close();
        return builder.toString();
    }

    public static String getFileContent(final File file, final String lineSeparator) throws IOException {
        final BufferedReader reader = new BufferedReader(new FileReader(file));
        final StringBuilder builder = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                if (lineSeparator != null) {
                    builder.append(lineSeparator);
                }
                line = reader.readLine();
            }
        } finally {
            reader.close();
        }
        return builder.toString();
    }

    public static boolean unzipJar(final File destination, final File jarFile, final List<String> exclude) {
        try {
            if (!isZipValid(jarFile)) {
                return false;
            }
            final JarFile jar = new JarFile(jarFile);
            for (final Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); ) {
                final JarEntry entry = enums.nextElement();
                final String fileName = entry.getName();
                final File file = new File(destination, fileName);
                if (!listContains(exclude, fileName) && !file.exists()) {
                    if (fileName.endsWith("/")) {
                        file.mkdirs();
                    } else {
                        final InputStream input = jar.getInputStream(entry);
                        final FileOutputStream output = new FileOutputStream(file);
                        while (input.available() > 0) {
                            output.write(input.read());
                        }
                        output.close();
                        input.close();
                    }
                }
            }
            jar.close();
            return true;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isZipValid(final File file) {
        try {
            final ZipFile zip = new ZipFile(file);
            final Enumeration<? extends ZipEntry> enumeration = zip.entries();
            while (enumeration.hasMoreElements()) {
                enumeration.nextElement();
            }
            zip.close();
            return true;
        } catch (final Exception ex) {
        }
        return false;
    }

    public static boolean listContains(final List<String> list, String sentence) {
        sentence = sentence.toLowerCase();
        for (final String string : list) {
            if (sentence.contains(string.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareVersions(final String versionTo, final String versionWith) {
        return normalisedVersion(versionTo, ".", 4).compareTo(normalisedVersion(versionWith, ".", 4)) > 0;
    }

    private static String normalisedVersion(final String version, final String separator, final int maxWidth) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final String normalised : Pattern.compile(separator, Pattern.LITERAL).split(version)) {
            stringBuilder.append(String.format("%" + maxWidth + 's', normalised));
        }
        return stringBuilder.toString();
    }

    public static void writeToFile(final File file, final String content) throws IOException {
        final FileWriter fileWriter = new FileWriter(file, true);
        final PrintWriter printWriter = new PrintWriter(fileWriter, true);
        printWriter.println(content);
        printWriter.close();
        fileWriter.close();
    }

    public static void setUIFont(final FontUIResource font) {
        final Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    public static String getJavaDir() {
        final String path = System.getProperty("java.home") + File.separator + "bin" + File.separator;
        if (MiniLauncher.SYSTEM.getPlatform().getOS() == OS.WINDOWS && new File(path + "javaw.exe").isFile()) {
            return path + "javaw.exe";
        }
        return path + "java";
    }

    public static boolean isValidFileName(final String name) {
        final File tempDir = MiniLauncher.SYSTEM.getLauncherTemporaryDirectory();
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        final File file = new File(tempDir, name);
        try {
            if (file.createNewFile()) {
                file.delete();
                return true;
            }
        } catch (final Exception ex) {
        }
        return false;
    }

}
