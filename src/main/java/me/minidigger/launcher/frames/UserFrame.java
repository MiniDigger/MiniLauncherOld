package me.minidigger.launcher.frames;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import me.minidigger.launcher.LauncherConstants;
import me.minidigger.launcher.UsersManager;
import me.minidigger.launcher.UsersManager.User;
import me.minidigger.launcher.tasks.AuthUser;
import me.minidigger.launcher.tasks.UserUUID;

public class UserFrame extends JDialog {

    private static final long serialVersionUID = 1L;

    public final JButton btnLogIn = new JButton("Log in !") {

        private static final long serialVersionUID = 1L;

        {
            setFont(getFont().deriveFont(Font.BOLD));
        }

    };
    private static final List<UserChangesListener> listeners = new ArrayList<>();

    public UserFrame(final JFrame parent, final String account) {
        final Color background = new Color(66, 65, 61);
        this.setSize(315, 200);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setModal(true);
        this.setIconImage(LauncherConstants.LAUNCHER_ICON);
        this.setLocationRelativeTo(parent);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final Container pane = this.getContentPane();
        pane.setBackground(background);
        final JLabel lblUsername = new JLabel("Username :");
        lblUsername.setForeground(Color.BLACK);
        final JTextField txtfldUsername = new JTextField();
        txtfldUsername.setColumns(10);
        final JLabel lblPassword = new JLabel("Password :");
        lblPassword.setForeground(Color.BLACK);
        final JPasswordField pswrdfldPassword = new JPasswordField();
        pswrdfldPassword.setEchoChar('x');
        final JCheckBox chckbxOfflineMode = new JCheckBox("Offline mode");
        chckbxOfflineMode.addItemListener(event -> {
            final boolean visible = !chckbxOfflineMode.isSelected();
            pswrdfldPassword.setVisible(visible);
            lblPassword.setVisible(visible);
            if (visible) {
                btnLogIn.setText("Log in !");
            } else {
                btnLogIn.setText("Save");
            }
        });
        if (account != null) {
            this.setTitle("User editor");
            txtfldUsername.setText(account);
        } else {
            this.setTitle("Add an user...");
        }
        chckbxOfflineMode.setBackground(background);
        chckbxOfflineMode.setForeground(Color.BLACK);
        btnLogIn.addActionListener(event -> {
            final String username = txtfldUsername.getText();
            if (!UsersManager.hasUser(username)) {
                btnLogIn.setEnabled(false);
                btnLogIn.setText("Please wait...");
                if (pswrdfldPassword.isVisible()) {
                    new AuthUser(username, new String(pswrdfldPassword.getPassword()), UserFrame.this).start();
                } else {
                    new UserUUID(username, UserFrame.this).start();
                }
            } else {
                JOptionPane.showMessageDialog((Component) event.getSource(), "This user already exists !", "Error !", JOptionPane.ERROR_MESSAGE);
            }
        });
        final GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(btnLogIn, GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblUsername).addComponent(lblPassword)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(pswrdfldPassword, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE).addComponent(txtfldUsername, GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE))).addComponent(chckbxOfflineMode)).addContainerGap()));
        groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblUsername).addComponent(txtfldUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblPassword).addComponent(pswrdfldPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(chckbxOfflineMode).addPreferredGap(ComponentPlacement.RELATED, 40, Short.MAX_VALUE).addComponent(btnLogIn).addContainerGap()));
        pane.setLayout(groupLayout);
    }

    public static void addListener(final UserChangesListener listener) {
        listeners.add(listener);
    }

    public interface UserChangesListener {

        void onUserSaved(final User user);

    }

    public final void saveAndNotifyListeners(final User user) {
        user.save();
        UsersManager.setUser(user.username, user);
        for (final UserChangesListener listener : listeners) {
            listener.onUserSaved(user);
        }
        this.dispose();
    }

}
