package me.minidigger.launcher.tasks;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimerTask;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.utils.ConnectionUtils;

public class ServicesStatus extends TimerTask {

    private final Collection<String> services;
    private static final List<ServiceStatusListener> listeners = new ArrayList<>();

    public ServicesStatus(final String... services) {
        this(Arrays.asList(services));
    }

    public ServicesStatus(final Collection<String> services) {
        this.services = services;
    }

    @Override
    public void run() {
        final HashMap<String, Boolean> result = new HashMap<>();
        try {
            for (final ServiceStatusListener listener : listeners) {
                listener.onStatusCheckBegin();
            }
            if (MiniLauncher.isOnline) {
                final HashMap<?, ?>[] responses = new Gson().fromJson(ConnectionUtils.httpGet(LauncherConstants.STATUS_CHECK_URL, null), HashMap[].class);
                for (final HashMap<?, ?> response : responses) {
                    for (final Entry<?, ?> entry : response.entrySet()) {
                        final String service = (String) entry.getKey();
                        if (services.contains(service)) {
                            result.put(service, entry.getValue().equals("green"));
                        }
                    }
                }
                notifyListeners(true, result);
            } else {
                notifyListeners(false, result);
            }
        } catch (final Exception ex) {
            notifyListeners(false, result);
            ex.printStackTrace();
        }
    }

    public final void notifyListeners(final boolean success, final HashMap<String, Boolean> result) {
        if (!success) {
            for (final String service : services) {
                result.put(service, false);
            }
        }
        for (final ServiceStatusListener listener : listeners) {
            listener.onStatusCheckFinished(result);
        }
    }

    public static void addListener(final ServiceStatusListener listener) {
        listeners.add(listener);
    }

    public interface ServiceStatusListener {

        void onStatusCheckBegin();

        void onStatusCheckFinished(final HashMap<String, Boolean> servicesStatus);

    }

}
