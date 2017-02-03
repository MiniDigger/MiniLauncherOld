package me.minidigger.launcher.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import me.minidigger.launcher.MiniLauncher;

public abstract class JSONObject {

    private transient ObjectType type;
    private transient String id;

    public JSONObject(final ObjectType type, final String id) {
        this.type = type;
        this.id = id;
    }

    @Override
    public final String toString() {
        return new Gson().toJson(this);
    }

    public final void load() throws JsonSyntaxException, IOException, IllegalArgumentException, IllegalAccessException {
        final File file = getFile();
        if (file.exists()) {
            final Class<?> clazz = this.getClass();
            final Object subClass = new Gson().fromJson(Utils.getFileContent(file, null), clazz);
            for (final Field field : clazz.getFields()) {
                final Object value = field.get(subClass);
                if (value != null) {
                    field.set(this, value);
                }
            }
        } else {
            save();
        }
    }

    public final void save() {
        try {
            final File file = getFile();
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            Utils.writeToFile(file, this.toString());
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    public final File getFile() {
        return new File(type.directory, id + ".json");
    }

    public enum ObjectType {

        USER(new File(MiniLauncher.SYSTEM.getApplicationDirectory() + File.separator + "users")),
        PROFILE(new File(MiniLauncher.SYSTEM.getApplicationDirectory() + File.separator + "profiles")),
        CONFIG(MiniLauncher.SYSTEM.getApplicationDirectory());

        public final File directory;

        ObjectType(final File directory) {
            this.directory = directory;
        }

    }

}
