package me.minidigger.launcher.frames;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.ProfilesManager.LauncherProfile;
import me.minidigger.launcher.MiniLauncher;
import me.minidigger.launcher.UsersManager;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.frames.UserFrame.UserChangesListener;
import me.minidigger.launcher.tasks.ChangelogDownloader;
import me.minidigger.launcher.tasks.UpdateVersions;
import me.minidigger.launcher.tasks.UpdateVersions.Version;
import me.minidigger.launcher.tasks.UpdateVersions.VersionsListener;
import me.minidigger.launcher.tasks.UpdateVersions.VersionsResult;
import me.minidigger.launcher.utils.Utils;

public class ProfileFrame extends JDialog implements UserChangesListener, VersionsListener {

    private static final long serialVersionUID = 1L;
    private LauncherProfile loadedProfile;
    private static final Color BACKGROUND_COLOR = new Color(66, 65, 61);
    protected final JTextField txtfldProfileName = new JTextField();
    protected final DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>() {

        private static final long serialVersionUID = 1L;

        {
            for (final String user : UsersManager.getUsernames()) {
                addElement(user);
            }
        }

    };
    protected final JTextField txtfldGameDir = new JTextField();
    protected final JTextField txtfldArguments = new JTextField();
    protected final JComboBox<String> cboxVersion = new JComboBox<>();
    protected final JButton btnRefreshList = new JButton("Refresh...");
    protected final JCheckBox chckbxLeaveLauncherVisible = new JCheckBox("Leave launcher visible") {

        private static final long serialVersionUID = 1L;

        {
            setBackground(BACKGROUND_COLOR);
            setForeground(Color.BLACK);
        }

    };
    protected final JCheckBox chckbxLogMinecraft = new JCheckBox("Log Minecraft") {

        private static final long serialVersionUID = 1L;

        {
            setBackground(BACKGROUND_COLOR);
            setForeground(Color.BLACK);
        }

    };
    protected JLabel lblUseravatar = new JLabel("UserAvatar") {

        private static final long serialVersionUID = 1L;

        {
            setFont(getFont().deriveFont(Font.ITALIC));
            setForeground(Color.BLACK);
        }

    };
    protected final JButton btnSave = new JButton("Save") {

        private static final long serialVersionUID = 1L;

        {
            setFont(getFont().deriveFont(Font.BOLD));
        }

    };
    private static final HashMap<String, BufferedImage> cache = new HashMap<>();
    public static final List<ProfileChangesListener> listeners = new ArrayList<>();

    public ProfileFrame(final LauncherFrame parent) {
        this(parent, null);
    }

    public ProfileFrame(final LauncherFrame parent, final LauncherProfile profile) {
        UpdateVersions.addListener(this);
        UserFrame.addListener(this);
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(parent);
        this.setAlwaysOnTop(true);
        this.loadedProfile = profile;
        this.setIconImage(LauncherConstants.LAUNCHER_ICON);
        this.setTitle("Skyolauncher Profile Editor");
        this.setPreferredSize(new Dimension(720, 292));
        this.setType(Type.POPUP);
        final Container pane = this.getContentPane();
        pane.setBackground(BACKGROUND_COLOR);
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent event) {
                for (final ProfileChangesListener listener : listeners) {
                    listener.onProfileChanged(loadedProfile, null);
                }
            }

        });
        final JLabel lblProfileName = new JLabel("Profile name :");
        lblProfileName.setForeground(Color.BLACK);
        txtfldProfileName.setColumns(10);
        final JLabel lblUser = new JLabel("User :");
        lblUser.setForeground(Color.BLACK);
        final JComboBox<String> cboxUser = new JComboBox<>(model);
        cboxUser.addActionListener(event -> loadAvatar((String) cboxUser.getSelectedItem()));
        final JButton btnAddAnUser = new JButton("Add an user...");
        btnAddAnUser.addActionListener(event -> new UserFrame(parent, null).setVisible(true));
        final JButton btnDeleteThisUser = new JButton("Delete this user...");
        btnDeleteThisUser.addActionListener(event -> {
            final String username = (String) cboxUser.getSelectedItem();
            UsersManager.getUser(username).getFile().delete();
            UsersManager.removeUserFromList(username);
            cboxUser.removeItem(username);
        });
        final JLabel lblGameDir = new JLabel("Game dir :");
        lblGameDir.setForeground(Color.BLACK);
        txtfldGameDir.setColumns(10);
        txtfldGameDir.setEnabled(false);
        final JButton button = new JButton("...");
        button.addActionListener(event -> {
            final JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            directoryChooser.showOpenDialog((Component) event.getSource());
            final File selectedFile = directoryChooser.getSelectedFile();
            if (selectedFile != null) {
                txtfldGameDir.setText(selectedFile.getPath());
            }
        });
        final JLabel lblArguments = new JLabel("Arguments :");
        lblArguments.setForeground(Color.BLACK);
        txtfldArguments.setColumns(10);
        final JLabel lblVersion = new JLabel("Version :");
        lblVersion.setForeground(Color.BLACK);
        btnRefreshList.addActionListener(event -> refreshVersions());
        final JButton btnChangelog = new JButton("Changelog...");
        btnChangelog.addActionListener(event -> new ChangelogDownloader().start());
        btnSave.addActionListener(event -> {
            final String profileName = txtfldProfileName.getText();
            final String username = (String) cboxUser.getSelectedItem();
            final String gameDirPath = txtfldGameDir.getText();
            final String arguments = txtfldArguments.getText();
            final String version = (String) cboxVersion.getSelectedItem();
            if (profileName.length() == 0 || username == null || gameDirPath.length() == 0 || version == null) {
                JOptionPane.showMessageDialog(null, "Please fill every field.", "Skyolauncher Profile Editor", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Utils.isValidFileName(profileName)) {
                JOptionPane.showMessageDialog(null, "This name is not valid !", "Skyolauncher Profile Editor", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final File gameDir = new File(gameDirPath);
            if (!gameDir.exists()) {
                gameDir.mkdirs();
            }
            for (final ProfileChangesListener listener : listeners) {
                listener.onProfileChanged(loadedProfile, new LauncherProfile(profileName, UsersManager.getUser(username), gameDir, arguments.length() == 0 ? null : arguments, version, chckbxLeaveLauncherVisible.isSelected(), chckbxLogMinecraft.isSelected()));
            }
        });
        final GroupLayout groupLayout = new GroupLayout(pane);
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addComponent(chckbxLogMinecraft).addContainerGap()).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addGroup(groupLayout.createSequentialGroup().addComponent(lblArguments).addPreferredGap(ComponentPlacement.RELATED).addComponent(txtfldArguments, GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblProfileName).addComponent(lblUser).addComponent(lblGameDir).addComponent(lblVersion)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout.createSequentialGroup().addComponent(txtfldGameDir, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(button, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(txtfldProfileName, GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(cboxVersion, 0, 338, Short.MAX_VALUE).addComponent(cboxUser, 0, 338, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false).addComponent(btnChangelog, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnAddAnUser, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false).addComponent(btnRefreshList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnDeleteThisUser, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)))).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(lblUseravatar)))).addComponent(chckbxLeaveLauncherVisible)).addContainerGap()))));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap(22, Short.MAX_VALUE).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblProfileName).addComponent(txtfldProfileName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblUser).addComponent(cboxUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnDeleteThisUser).addComponent(btnAddAnUser)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblVersion).addComponent(cboxVersion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnChangelog).addComponent(btnRefreshList))).addComponent(lblUseravatar)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblGameDir).addComponent(button).addComponent(txtfldGameDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblArguments).addComponent(txtfldArguments, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(chckbxLeaveLauncherVisible).addPreferredGap(ComponentPlacement.RELATED).addComponent(chckbxLogMinecraft).addGap(9).addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE).addContainerGap()));
        pane.setLayout(groupLayout);
        loadProfile(loadedProfile);
        refreshVersions();
        loadAvatar((String) cboxUser.getSelectedItem());
        this.pack();
    }

    public static void addListener(final ProfileChangesListener listener) {
        listeners.add(listener);
    }

    public final void loadAvatar(final String username) {
        new Thread(() -> {
            lblUseravatar.setIcon(null);
            lblUseravatar.setText("Loading...");
            BufferedImage image = cache.get(username);
            if (image == null) {
                try {
                    image = ImageIO.read(new URL("https://minotar.net/helm/" + username + "/80.png"));
                    new Timer().scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {
                            cache.remove(username);
                        }

                    }, 0, 30000);
                    lblUseravatar.setIcon(new ImageIcon(image));
                } catch (final Exception ex) {
                    lblUseravatar.setVisible(false);
                    ex.printStackTrace();
                }
                lblUseravatar.setText(null);
            }
        }).start();
    }

    public final void refreshVersions() {
        new Thread(() -> {
            if (MiniLauncher.isOnline) {
                new UpdateVersions().start();
            } else {
                new UpdateVersions(new File(txtfldGameDir.getText() + File.separator + "versions")).start();
            }
        }).start();
    }

    public final void loadProfile(final LauncherProfile profile) {
        this.loadedProfile = profile;
        if (profile != null) {
            txtfldProfileName.setText(profile.name);
            if (profile.user != null) {
                model.setSelectedItem(UsersManager.getUserByID(profile.user).username);
            }
            txtfldGameDir.setText(profile.gameDirectory.getPath());
            txtfldArguments.setText(profile.arguments);
            cboxVersion.setSelectedItem(profile.version);
            chckbxLeaveLauncherVisible.setSelected(profile.launcherVisible);
            chckbxLogMinecraft.setSelected(profile.logMinecraft);
        } else {
            txtfldProfileName.setText("New profile");
            txtfldGameDir.setText(MiniLauncher.SYSTEM.getMinecraftDirectory().getPath());
            txtfldArguments.setText("-Xms512m -Xmx1024m");
        }
    }

    @Override
    public void onUserSaved(final User user) {
        if (model.getIndexOf(user.username) == -1) {
            model.addElement(user.username);
        }
        model.setSelectedItem(user.username);
    }

    @Override
    public void onVersionsCheckBegin() {
        btnRefreshList.setEnabled(false);
        btnSave.setEnabled(false);
        btnSave.setText("Please wait...");
    }

    @Override
    public void onVersionsReceived(final VersionsResult result) {
        if (result != null) {
            for (final Version version : result.versions) {
                cboxVersion.addItem(version.id);
            }
            btnSave.setEnabled(true);
        }
        btnRefreshList.setEnabled(true);
        btnSave.setText("Save");
    }

    public interface ProfileChangesListener {

        void onProfileChanged(final LauncherProfile oldProfile, final LauncherProfile newProfile);

    }

}
