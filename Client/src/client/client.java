package client;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import java.awt.event.*;      // KeyEvent, MouseEvent, MouseWheelEvent, FocusEvent, etc.
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;    // for ByteBuffer.wrap(...)
import java.util.Arrays;       // for Arrays.toString(...)

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sign.signlink;

@SuppressWarnings("serial")
public class client
    extends Canvas
    implements Runnable,
               KeyListener,
               MouseListener,
               MouseMotionListener,
               MouseWheelListener,
               FocusListener
{
    // ------------------------------------------------------------
    // Static & state fields
    // ------------------------------------------------------------
    public static client clientInstance;
    public static boolean newDamage = false;
    public static boolean loggedIn   = false;

    // Color mapping arrays (example values)
    public static final int[][] anIntArrayArray1003 = {
        {6798, 8741, 25238, 4626, 4550},
        {9104, 10275, 7595, 3610, 7975},
        {41797, 16254, 47511, 42449, 34032},
        {26073, 26591, 27352, 27921, 28952},
        {25486, 26701, 24861, 24863, 25851},
        {32435, 32847, 32848, 32849, 32850},
        {23684, 24191, 24699, 25099, 25510},
        {27812, 28263, 28901, 29384, 29845}
    };
    public static final int[] anIntArray1204 = {
        9104, 10275, 7595, 3610, 7975,
        8526, 918, 38802, 24466, 10145,
        58654, 5027, 1457, 16565, 34991, 25486
    };

    private static Decompressor[] decompressors;

    private JFrame   frame;
    private boolean running       = false;
    private Thread  gameThread;
    private Image   offScreenImage;
    private Graphics offScreenGraphics;

    // Game data fields
    public int    loopCycle;
    public int[]  anIntArray1232;
    public int[]  variousSettings;

    private Stream   stream;
    private RSSocket socketStream;

    private String loginMessage1 = "";
    private String loginMessage2 = "";
    private String myUsername    = "";

    private static final int WIDTH  = 765;
    private static final int HEIGHT = 503;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    public client() {
        clientInstance     = this;
        loopCycle          = 0;
        anIntArray1232     = new int[256];
        variousSettings    = new int[32];
    }

    // ------------------------------------------------------------
    // Entry‐point helpers
    // ------------------------------------------------------------
    public void createGameFrame() {
        frame = new JFrame("StoneGuide");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addFocusListener(this);

        startGame();
    }

    private void startGame() {
        running    = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ------------------------------------------------------------
    // Main loop
    // ------------------------------------------------------------
    @Override
    public void run() {
        initGame();
        while (running) {
            repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // ------------------------------------------------------------
    // Initialization & rendering
    // ------------------------------------------------------------
    private void initGame() {
        offScreenImage    = createImage(WIDTH, HEIGHT);
        if (offScreenImage != null) {
            offScreenGraphics = offScreenImage.getGraphics();
        }
        startUp();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (offScreenGraphics != null) {
            renderGame(offScreenGraphics);
            g.drawImage(offScreenImage, 0, 0, null);
        }
    }

    private void renderGame(Graphics g) {
    if (!loggedIn) {
        drawWelcomeScreen(g);
        return;
    }

    // 1) clear background
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, WIDTH, HEIGHT);

    // 2) update game state (empty for now)
    updateGame();

    // 3) draw the “world” placeholder
    drawWorld(g);

    // 4) draw the “player” placeholder
    drawPlayer(g);

    // 5) draw a simple UI overlay
    drawUI(g);
}
// 2a) Does nothing right now
private void updateGame() {
    // later: process input, animations, network packets…
}

// 2b) Draw a big gray square as your “world”
private void drawWorld(Graphics g) {
    g.setColor(Color.DARK_GRAY);
    g.fillRect(50, 50, 300, 200);
    g.setColor(Color.WHITE);
    g.drawString("Game World Area", 60, 70);
}

// 2c) Draw a green circle as your “player”
private void drawPlayer(Graphics g) {
    g.setColor(Color.GREEN);
    g.fillOval(180, 130, 20, 20);
    g.setColor(Color.WHITE);
    g.drawString("You", 180, 130);
}

// 2d) Draw a white status bar at the top
private void drawUI(Graphics g) {
    g.setColor(Color.WHITE);
    g.drawString("HP: 100/100", 10, 20);
    g.drawRect(WIDTH - 110, 10, 100, 100); // a mini‐map box
}


    private void drawWelcomeScreen(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.drawString("Welcome to StoneGuide!", 330, 240);
        g.drawString(loginMessage1,            330, 260);
        g.drawString(loginMessage2,            330, 280);
    }

    private void resetImageProducers() {
        // placeholder: dispose/recreate image producers as needed
    }

    // ------------------------------------------------------------
    // Loading screen
    // ------------------------------------------------------------
    public void drawLoadingText(int percent, String message) {
        Graphics g = this.getGraphics();
        if (g == null) return;

        // simple fallback
        if (decompressors == null) {
            int w = getWidth(), h = getHeight();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, w, h);
            g.setColor(Color.WHITE);
            String txt = "Loading " + percent + "% - " + message;
            FontMetrics fm = g.getFontMetrics();
            int x = (w - fm.stringWidth(txt)) / 2;
            int y = (h + fm.getAscent()) / 2;
            g.drawString(txt, x, y);
            g.dispose();
            return;
        }

        g.dispose();
    }

    public static Decompressor[] getDecompressors() {
        return decompressors;
    }

    // ------------------------------------------------------------
    // Startup & login
    // ------------------------------------------------------------
    private void startUp() {
        drawLoadingText(20, "Downloading cache...");
        new CacheDownloader(this).downloadCache();

        signlink.errorname = "";
        decompressors = new Decompressor[5];
        if (signlink.cache_dat != null) {
            for (int i = 0; i < 5; i++) {
                decompressors[i] = new Decompressor(
                    signlink.cache_dat,
                    signlink.cache_idx[i],
                    i + 1
                );
            }
        }

        drawLoadingText(100, "Ready to connect…");

        String user = getParameter("username");
        String pass = getParameter("password");
        if (user == null || pass == null) {
            user = JOptionPane.showInputDialog(this, "Enter username:");
            pass = JOptionPane.showInputDialog(this, "Enter password:");
        }

        login(user, pass, false);
    }

    private void login(String username, String password, boolean reconnect) {
        signlink.errorname = username;
        try {
            if (!reconnect) {
                loginMessage1 = "";
                loginMessage2 = "Connecting to server...";
                repaint();
            }

            socketStream = new RSSocket(openSocket(43594));
            long nameAsLong = TextClass.longForName(username);
            int nameHash    = (int)((nameAsLong >> 16) & 31L);

            // initial handshake
            stream.currentOffset = 0;
            stream.writeWordBigEndian(14);
            stream.writeWordBigEndian(nameHash);
            socketStream.queueBytes(2, stream.buffer);

            // read handshake
            byte[] raw = new byte[17];
            socketStream.flushInputStream(raw, 17);
            int handshakeCode = raw[8] & 0xFF;
            long sessionKey   = ByteBuffer.wrap(raw, 9, 8).getLong();

            System.out.println(
                "[Client] handshake: " + Arrays.toString(raw)
                + " code=" + handshakeCode
                + " key=" + sessionKey
            );

            if (handshakeCode != 0) {
                loginMessage1 = "Handshake failed.";
                loginMessage2 = "Server refused login attempt.";
                repaint();
                return;
            }

            // encrypted login block
            stream.currentOffset = 0;
            int sk1 = (int)(Math.random() * 99999999);
            int sk2 = (int)(Math.random() * 99999999);
            stream.writeWordBigEndian(10);
            stream.writeDWord(sk1);
            stream.writeDWord(sk2);
            stream.writeDWord((int)(nameAsLong >> 32));
            stream.writeDWord((int)nameAsLong);
            stream.writeDWord(signlink.uid);
            stream.writeString(username);
            stream.writeString(password);

            System.out.println("[Client] sending login block…");
            socketStream.queueBytes(stream.currentOffset, stream.buffer);

            int responseCode = socketStream.read();
            System.out.println("[Client] login response: " + responseCode);

            switch (responseCode) {
                case 1: case 2: case 15:
                    loginMessage1 = "Login successful!";
                    loginMessage2 = "Welcome, " + username;
                    loggedIn      = true;
                    myUsername    = username;
                    break;
                case 3:
                    loginMessage1 = "";
                    loginMessage2 = "Invalid username or password.";
                    break;
                case 4:
                    loginMessage1 = "Your account has been disabled.";
                    loginMessage2 = "Check your message center.";
                    break;
                case 5:
                    loginMessage1 = "Account already logged in.";
                    loginMessage2 = "Try again in 60 seconds.";
                    break;
                case 6:
                    loginMessage1 = "Client version mismatch.";
                    loginMessage2 = "Please update your client.";
                    break;
                case 7:
                    loginMessage1 = "World is full.";
                    loginMessage2 = "Try again later.";
                    break;
                case 14:
                    loginMessage1 = "Server is being updated.";
                    loginMessage2 = "Please wait…";
                    break;
                case 21:
                    loginMessage1 = "Login delay.";
                    loginMessage2 = "Please retry in a moment.";
                    break;
                default:
                    loginMessage1 = "Login failed.";
                    loginMessage2 = "Unexpected response: " + responseCode;
                    break;
            }

        } catch (IOException e) {
            loginMessage1 = "";
            loginMessage2 = "Error connecting to server.";
            e.printStackTrace();
        }

        repaint();
    }

    public Socket openSocket(int port) throws IOException {
        return new Socket("127.0.0.1", port);
    }

    public String getParameter(String name) {
        if ("username".equalsIgnoreCase(name))
            return "AustinMc";
        if ("password".equalsIgnoreCase(name))
            return "Test123";
        if ("music".equalsIgnoreCase(name))
            return "1";
        return null;
    }

    // ------------------------------------------------------------
    // Event listener stubs
    // ------------------------------------------------------------
    @Override public void keyTyped(KeyEvent e)      {}
    @Override public void keyPressed(KeyEvent e)    {}
    @Override public void keyReleased(KeyEvent e)   {}

    @Override public void mouseClicked(MouseEvent e)  {}
    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    @Override public void mouseDragged(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e)   {}

    @Override public void mouseWheelMoved(MouseWheelEvent e) {}

    @Override public void focusGained(FocusEvent e) {}
    @Override public void focusLost(FocusEvent e)   {}
}
