package me.minidigger.launcher.frames;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.ProfilesManager;
import me.minidigger.launcher.ProfilesManager.LauncherProfile;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.UsersManager;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.frames.ProfileFrame.ProfileChangesListener;
import me.minidigger.launcher.tasks.AuthUser.AuthSession;
import me.minidigger.launcher.tasks.GameTasks;
import me.minidigger.launcher.tasks.GameTasks.GameTasksListener;
import me.minidigger.launcher.tasks.RefreshToken;
import me.minidigger.launcher.tasks.RefreshToken.RefreshTokenListener;
import me.minidigger.launcher.tasks.ServicesStatus;
import me.minidigger.launcher.tasks.ServicesStatus.ServiceStatusListener;
import me.minidigger.launcher.utils.Utils;

public class LauncherFrame extends JFrame implements ProfileChangesListener, ServiceStatusListener, GameTasksListener, RefreshTokenListener {

    private static final long serialVersionUID = 1L;
    private final me.minidigger.launcher.frames.ProfileFrame profileEditor = new ProfileFrame(this);
    private final JComboBox<String> checkboxProfile = new JComboBox<String>() {

        private static final long serialVersionUID = 1L;

        {
            for (final String profile : ProfilesManager.getProfilesName()) {
                addItem(profile);
            }
            if (MiniLauncher.config.latestProfile != null) {
                setSelectedItem(MiniLauncher.config.latestProfile);
            }
        }

    };
    private final HashMap<String, JLabel> status = new HashMap<>();
    private final JButton btnDeleteProfile = new JButton("Delete profile...");
    private final JButton btnEditProfile = new JButton("Edit profile...");
    private final JButton btnPlay = new JButton("Play !") {

        private static final long serialVersionUID = 1L;

        {
            setFont(getFont().deriveFont(Font.BOLD));
        }

    };
    private boolean tokensRefreshed = true;

    public LauncherFrame() {
        RefreshToken.addListener(this);
        GameTasks.addListener(this);
        ProfileFrame.addListener(this);
        ServicesStatus.addListener(this);

        setTitle(Utils.buildTitle(MiniLauncher.isOnline));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(LauncherConstants.LAUNCHER_ICON);
        setLocation(MiniLauncher.config.launcherPointX, MiniLauncher.config.launcherPointY);
        setPreferredSize(new Dimension(540, 250));// FIXME change back to 400 when we have a logo
        setResizable(false);

        Container pane = this.getContentPane();
        pane.setBackground(new Color(66, 65, 61));
        JLabel lblLogo = new JLabel(new ImageIcon(LauncherConstants.LAUNCHER_IMAGE));
        lblLogo.setVisible(false);// FIXME find a nice logo
        final JProgressBar prgBarDownload = new JProgressBar();
        prgBarDownload.setStringPainted(true);
        prgBarDownload.setVisible(false);
        btnPlay.addActionListener(event -> {
            LauncherProfile profile = ProfilesManager.getProfile((String) checkboxProfile.getSelectedItem());
            if (profile.user == null) {
                JOptionPane.showMessageDialog(null, "Cannot launch the selected profile : user is null.", "Error !", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new GameTasks(profile, prgBarDownload).start();
        });
        if (ProfilesManager.getProfiles().length == 0) {
            updateBtnPlay(false);
            btnDeleteProfile.setEnabled(false);
            btnEditProfile.setEnabled(false);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (ProfilesManager.getProfiles().length != 0) {
                    MiniLauncher.config.latestProfile = checkboxProfile.getSelectedItem().toString();
                }
                Point location;
                if (MiniLauncher.console != null) {
                    location = MiniLauncher.console.getLocation();
                    MiniLauncher.config.consolePointX = location.x;
                    MiniLauncher.config.consolePointY = location.y;
                }
                location = LauncherFrame.this.getLocation();
                MiniLauncher.config.launcherPointX = location.x;
                MiniLauncher.config.launcherPointY = location.y;
                MiniLauncher.config.save();
                final File tempDir = MiniLauncher.SYSTEM.getLauncherTemporaryDirectory();
                if (tempDir.exists()) {
                    tempDir.delete();
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getClass().getName(), "Error !", JOptionPane.ERROR_MESSAGE);
            }
        }));
        final JLabel lblMinecraftWebsite = new JLabel("Minecraft website :");
        lblMinecraftWebsite.setForeground(Color.WHITE);
        final JLabel lblMojangAuthServer = new JLabel("Mojang auth server :");
        lblMojangAuthServer.setForeground(Color.WHITE);
        final JLabel lblMinecraftSkinsServer = new JLabel("Minecraft skins server :");
        lblMinecraftSkinsServer.setForeground(Color.WHITE);
        final JLabel lblMinecraftWebsiteStatus = new JLabel();
        final JLabel lblMojangAuthServerStatus = new JLabel();
        final JLabel lblMinecraftSkinsServerStatus = new JLabel();
        status.put("minecraft.net", lblMinecraftWebsiteStatus);
        status.put("authserver.mojang.com", lblMojangAuthServerStatus);
        status.put("skins.minecraft.net", lblMinecraftSkinsServerStatus);
        new Timer().scheduleAtFixedRate(new ServicesStatus(status.keySet()), 0, 40000);
        final JButton btnAddNewProfile = new JButton("Add new profile...");
        btnAddNewProfile.addActionListener(event -> {
            updateBtnPlay(false);
            profileEditor.loadProfile(null);
            profileEditor.setVisible(true);
        });
        btnDeleteProfile.addActionListener(event -> deleteProfile((String) checkboxProfile.getSelectedItem()));
        btnEditProfile.addActionListener(event -> {
            updateBtnPlay(false);
            profileEditor.loadProfile(ProfilesManager.getProfile((String) checkboxProfile.getSelectedItem()));
            profileEditor.setVisible(true);
        });
        final GroupLayout groupLayout = new GroupLayout(pane);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(checkboxProfile, 0, 514, Short.MAX_VALUE).addContainerGap()).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(btnPlay, GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)).addGroup(groupLayout.createSequentialGroup().addGap(10).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addComponent(btnAddNewProfile, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnEditProfile, GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnDeleteProfile, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)).addComponent(lblLogo, GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblMinecraftWebsite).addComponent(lblMinecraftSkinsServer).addComponent(lblMojangAuthServer)).addPreferredGap(ComponentPlacement.RELATED, 403, Short.MAX_VALUE).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(lblMojangAuthServerStatus).addGroup(groupLayout.createSequentialGroup().addComponent(lblMinecraftSkinsServerStatus).addPreferredGap(ComponentPlacement.RELATED)).addGroup(groupLayout.createSequentialGroup().addComponent(lblMinecraftWebsiteStatus).addPreferredGap(ComponentPlacement.RELATED))))))).addGap(9)).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(prgBarDownload, GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE).addContainerGap()));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(lblLogo).addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblMinecraftWebsite).addComponent(lblMinecraftWebsiteStatus)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblMojangAuthServer).addComponent(lblMojangAuthServerStatus, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblMinecraftSkinsServer).addComponent(lblMinecraftSkinsServerStatus)).addGap(12).addComponent(prgBarDownload, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED, 34, Short.MAX_VALUE).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnAddNewProfile).addComponent(btnDeleteProfile).addComponent(btnEditProfile)).addGap(3).addComponent(checkboxProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnPlay).addContainerGap()));
        pane.setLayout(groupLayout);
        this.pack();
    }

    private void deleteProfile(final String profileName) {
        ProfilesManager.getProfile(profileName).getFile().delete();
        ProfilesManager.removeProfileFromList(profileName);
        checkboxProfile.removeItemAt(checkboxProfile.getSelectedIndex());
        if (ProfilesManager.getProfiles().length == 0) {
            updateBtnPlay(false);
            btnDeleteProfile.setEnabled(false);
            btnEditProfile.setEnabled(false);
        }
    }

    @Override
    public void onStatusCheckBegin() {
        for (final JLabel label : status.values()) {
            label.setText("Please wait...");
            label.setFont(label.getFont().deriveFont(Font.ITALIC));
            label.setForeground(Color.WHITE);
        }
    }

    @Override
    public void onStatusCheckFinished(final HashMap<String, Boolean> servicesStatus) {
        final Font font = LauncherConstants.LAUNCHER_FONT.deriveFont(Font.BOLD);
        for (final Entry<String, Boolean> entry : servicesStatus.entrySet()) {
            final JLabel label = status.get(entry.getKey());
            if (entry.getValue()) {
                label.setText("ONLINE");
                label.setForeground(Color.GREEN);
            } else {
                label.setText("UNREACHABLE");
                label.setForeground(Color.RED);
            }
            label.setFont(font);
        }
    }

    @Override
    public void onGameTasksBegin() {
        updateBtnPlay(false);
    }

    @Override
    public void onGameTasksFinished(final boolean success, final LauncherProfile profile) {
        if (success && !profile.launcherVisible) {
            if (profile.logMinecraft) {
                for (final Frame frame : JFrame.getFrames()) {
                    frame.setVisible(false);
                }
            } else {
                System.exit(0);
            }
        }
        updateBtnPlay(true);
    }

    @Override
    public void onProfileChanged(final LauncherProfile oldProfile, final LauncherProfile newProfile) {
        profileEditor.setVisible(false);
        if (newProfile != null) {
            if (oldProfile != null) {
                deleteProfile(oldProfile.name);
            }
            checkboxProfile.addItem(newProfile.name);
            checkboxProfile.setSelectedItem(newProfile.name);
            ProfilesManager.setProfile(newProfile.name, newProfile);
            newProfile.save();
        }
        if (ProfilesManager.getProfiles().length >= 1) {
            updateBtnPlay(true);
            btnDeleteProfile.setEnabled(true);
            btnEditProfile.setEnabled(true);
        }
    }

    @Override
    public void onTokenTaskBegin() {
        tokensRefreshed = false;
        updateBtnPlay(false);
    }

    @Override
    public void onTokenTaskFinished(final HashMap<User, AuthSession> result) {
        for (final Entry<User, AuthSession> entry : result.entrySet()) {
            final AuthSession session = entry.getValue();
            if (session.selectedProfile != null) {
                final User oldUser = entry.getKey();
                final User newUser = new User(session.selectedProfile.name, session.selectedProfile.id, oldUser.accountName, true, session.accessToken, session.user.properties);
                profileEditor.model.removeElement(oldUser.username);
                oldUser.getFile().delete();
                UsersManager.removeUserFromList(oldUser.username, false);
                newUser.save();
                UsersManager.addUser(newUser);
                profileEditor.model.addElement(newUser.username);
                profileEditor.model.setSelectedItem(newUser.username);
            }
        }
        tokensRefreshed = true;
        updateBtnPlay(true);
    }

    private void updateBtnPlay(final boolean enabled) {
        if (!enabled) {
            btnPlay.setText(ProfilesManager.getProfiles().length != 0 ? "Please wait..." : "Create a new profile first !");
            btnPlay.setEnabled(false);
        } else if (!profileEditor.isVisible() && tokensRefreshed && ProfilesManager.getProfiles().length != 0) {
            btnPlay.setEnabled(true);
            btnPlay.setText("Play !");
        }
    }

}
