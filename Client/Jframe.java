import sign.signlink;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class Jframe extends client implements ActionListener {

    private static JMenuItem menuItem;
    private JFrame frame;

    public Jframe(String[] args) {
        super();
        try {
            sign.signlink.startpriv(InetAddress.getByName(server));
            initUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initUI() {
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
                if (name.equals("-")) {
                    fileMenu.addSeparator();
                } else {
                    JMenuItem item = new JMenuItem(name);
                    item.addActionListener(this);
                    fileMenu.add(item);
                }
            }

            JMenuBar menuBar = new JMenuBar();
            menuBar.add(fileMenu);
            frame.setJMenuBar(menuBar);

            frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
            frame.setResizable(false);

            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public URL getCodeBase() {
        try {
            return new URL("http://" + server + "/cache");
        } catch (Exception e) {
            return super.getCodeBase();
        }
    }

    @Override
    public URL getDocumentBase() {
        return getCodeBase();
    }

    public void loadError(String s) {
        System.out.println("loadError: " + s);
    }

    @Override
    public String getParameter(String key) {
        return "";
    }

    private static void openUpWebSite(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ignored) {}
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        try {
            if (cmd != null) {
                if (cmd.equalsIgnoreCase("Exit") || cmd.equalsIgnoreCase("Exit Client")) {
                    System.exit(0);
                } else if (cmd.equalsIgnoreCase("Forums")) {
                    openUpWebSite("http://www.comingsoon.tk/");
                }
            }
        } catch (Exception ignored) {}
    }
}
