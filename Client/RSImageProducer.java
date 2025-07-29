// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

import java.awt.*;
import java.awt.image.*;

/**
 * RSImageProducer is responsible for producing images used by the client.
 * It implements both ImageProducer and ImageObserver to allow for drawing
 * and updating the image buffer.
 */
final class RSImageProducer implements ImageProducer, ImageObserver {

    public RSImageProducer(int width, int height, Component component) {
        anInt316 = width;
        anInt317 = height;
        anIntArray315 = new int[width * height];
        aColorModel318 = new DirectColorModel(32, 0xff0000, 0x00ff00, 0x0000ff);
        anImage320 = component.createImage(this);
        
        // Prepare the image multiple times to ensure compatibility
        method239();
        component.prepareImage(anImage320, this);
        method239();
        component.prepareImage(anImage320, this);
        method239();
        component.prepareImage(anImage320, this);

        initDrawingArea();
    }

    /**
     * Initializes the drawing area with current buffer and dimensions.
     */
    public void initDrawingArea() {
        DrawingArea.initDrawingArea(anInt317, anInt316, anIntArray315);
    }

    /**
     * Draws the buffered image onto the specified graphics context.
     * 
     * @param y Vertical offset
     * @param g Graphics context
     * @param x Horizontal offset
     */
    public void drawGraphics(int y, Graphics g, int x) {
        method239();
        g.drawImage(anImage320, x, y, this);
    }

    @Override
    public synchronized void addConsumer(ImageConsumer consumer) {
        anImageConsumer319 = consumer;
        consumer.setDimensions(anInt316, anInt317);
        consumer.setProperties(null);
        consumer.setColorModel(aColorModel318);
        consumer.setHints(ImageConsumer.TOPDOWNLEFTRIGHT | ImageConsumer.COMPLETESCANLINES | ImageConsumer.SINGLEPASS);
    }

    @Override
    public synchronized boolean isConsumer(ImageConsumer consumer) {
        return anImageConsumer319 == consumer;
    }

    @Override
    public synchronized void removeConsumer(ImageConsumer consumer) {
        if (anImageConsumer319 == consumer) {
            anImageConsumer319 = null;
        }
    }

    @Override
    public void startProduction(ImageConsumer consumer) {
        addConsumer(consumer);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer consumer) {
        System.out.println("TDLR");
    }

    /**
     * Notifies the consumer to refresh its pixels.
     */
    private synchronized void method239() {
        if (anImageConsumer319 != null) {
            anImageConsumer319.setPixels(
                0, 0, anInt316, anInt317,
                aColorModel318,
                anIntArray315, 0, anInt316
            );
            anImageConsumer319.imageComplete(ImageConsumer.STATICIMAGEDONE);
        }
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return true;
    }

    // Fields
    public final int[] anIntArray315;
    private final int anInt316;
    private final int anInt317;
    private final ColorModel aColorModel318;
    private ImageConsumer anImageConsumer319;
    private final Image anImage320;
}
