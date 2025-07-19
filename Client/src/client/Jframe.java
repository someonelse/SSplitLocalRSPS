package client;

import sign.signlink;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Jframe extends client implements ActionListener {

    private static final String SERVER_IP = "127.0.0.1"; // Server hostname or IP
    private JFrame frame;

    public Jframe(String[] args) {
        super();
        try {
            InetAddress ipAddress = InetAddress.getByName(SERVER_IP);
            signlink.startpriv(ipAddress); // Starts socket handling for cache
            initUI(); // Launches UI
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);

            frame = new JFrame("StoneGuide | Client Version 1.0");
            frame.setLayout(new BorderLayout());
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel gamePanel = new JPanel(new BorderLayout());
            gamePanel.add(this);
            gamePanel.setPreferredSize(new Dimension(765, 503));

            JMenu fileMenu = new JMenu("Info");
            String[] mainButtons = { "Files", "Version: 1.0", "-", "Forums", "-", "Exit" };
            for (String name : mainButtons) {
                if (name.equalsIgnoreCase("-")) {
                    fileMenu.addSeparator();
                } else {
                    JMenuItem item = new JMenuItem(name);
                    item.addActionListener(this);
                    fileMenu.add(item);
                }
            }

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(fileMenu);

            frame.getContentPane().add(menuBar, BorderLayout.NORTH);
            frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public URL getCodeBase() {
        try {
            return new URL("http://" + SERVER_IP + "/cache");
        } catch (Exception e) {
            return null; // No fallback needed; not an Applet anymore
        }
    }

    public URL getDocumentBase() {
        return getCodeBase();
    }

    public void loadError(String msg) {
        System.out.println("loadError: " + msg);
    }

    public String getParameter(String key) {
        return ""; // Used only for Applet compatibility in legacy code
    }

    private static void openUpWebSite(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd != null) {
            if (cmd.equalsIgnoreCase("Exit") || cmd.equalsIgnoreCase("Exit Client")) {
                System.exit(0);
            }
            if (cmd.equalsIgnoreCase("Forums")) {
                openUpWebSite("http://www.comingsoon.tk/");
            }
        }
    }
}