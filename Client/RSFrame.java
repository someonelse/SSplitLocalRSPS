import java.awt.*;

/**
 * RSFrame extends the standard AWT Frame to host the RSApplet.
 * It sets the window title, handles custom painting and graphics context adjustment.
 */
final class RSFrame extends Frame {

    private final RSApplet rsApplet;

    /**
     * Constructs the RSFrame window with fixed dimensions and title.
     *
     * @param RSApplet_ The applet to attach and forward painting to.
     * @param width     The width of the client area.
     * @param height    The height of the client area.
     */
    public RSFrame(RSApplet RSApplet_, int width, int height) {
        rsApplet = RSApplet_;
        setTitle("StoneGuide | Version 1.0");
        setResizable(false);
        setVisible(true);
        toFront();

        // Account for frame decorations: borders and title bar
        setSize(width + 8, height + 28);
        setResizable(true);

        // Center window on screen
        setLocationRelativeTo(null);
    }

    /**
     * Overrides getGraphics to translate coordinates for custom insets.
     *
     * @return Translated graphics context
     */
    @Override
    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        g.translate(4, 24); // Adjust for window border and title bar
        return g;
    }

    /**
     * Delegates update behavior to the RSApplet.
     *
     * @param g Graphics context
     */
    @Override
    public void update(Graphics g) {
        rsApplet.update(g);
    }

    /**
     * Delegates paint behavior to the RSApplet.
     *
     * @param g Graphics context
     */
    @Override
    public void paint(Graphics g) {
        rsApplet.paint(g);
    }
}
