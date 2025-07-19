package client;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    public int[] myPixels;
    public int myWidth;
    public int myHeight;
    private int offsetX;
    private int offsetY;
    public int drawOffsetX;
    public int drawOffsetY;
    public int anInt1444;
    public int anInt1445;
    public String location = "./Data/Sprites/";

    public Sprite(String relativePath) {
        try {
            BufferedImage img = ImageIO.read(
                new File(location + relativePath + ".png")
            );
            myWidth  = img.getWidth();
            myHeight = img.getHeight();
            myPixels = new int[myWidth * myHeight];
            img.getRGB(0, 0, myWidth, myHeight, myPixels, 0, myWidth);

            // if you rely on anInt1444/1445 for legacy code, set them too
            anInt1444 = myWidth;
            anInt1445 = myHeight;
            offsetX   = 0;
            offsetY   = 0;
        } catch (IOException e) {
            System.err.println("Failed to load sprite: " + relativePath);
            e.printStackTrace();
        }
    }

    
    // 🟥 Draw solid-edged sprite
    public void drawSprite(int x, int y, int edgeColor) {
        int paddedWidth = myWidth + 2;
        int paddedHeight = myHeight + 2;
        int[] buffer = new int[paddedWidth * paddedHeight];

        // Copy pixels with padding
        for (int px = 0; px < myWidth; px++) {
            for (int py = 0; py < myHeight; py++) {
                int val = myPixels[px + py * myWidth];
                if (val != 0) {
                    buffer[(px + 1) + (py + 1) * paddedWidth] = val;
                }
            }
        }

        // Edge detection pass
        for (int px = 0; px < paddedWidth; px++) {
            for (int py = 0; py < paddedHeight; py++) {
                int index = px + py * paddedWidth;
                if (buffer[index] == 0) {
                    boolean hasNeighbor =
                        (px < paddedWidth - 1 && valid(buffer[px + 1 + py * paddedWidth], edgeColor)) ||
                        (px > 0 && valid(buffer[px - 1 + py * paddedWidth], edgeColor)) ||
                        (py < paddedHeight - 1 && valid(buffer[px + (py + 1) * paddedWidth], edgeColor)) ||
                        (py > 0 && valid(buffer[px + (py - 1) * paddedWidth], edgeColor));
                    if (hasNeighbor) buffer[index] = edgeColor;
                }
            }
        }

        renderToScreen(buffer, x - 1 + offsetX, y - 1 + offsetY, paddedWidth, paddedHeight);
    }

    // 🟦 Draw sprite with alpha blending
    public void drawSpriteAlpha(int x, int y) {
        drawSpriteAlpha(x, y, 225); // Call the overloaded method with default alpha value
    }

    public void drawSpriteAlpha(int x, int y, int alpha) {
        renderAlpha(myPixels, x + offsetX, y + offsetY, myWidth, myHeight, alpha);
    }


    // 🟨 Rotate sprite around center and draw
    public void drawRotatedSprite(int centerX, int centerY, double angle, int scale) {
        rotateSprite(myPixels, centerX, centerY, angle, scale, myWidth, myHeight);
    }

    // 🟩 Transform sprite with shear and rotation
    public void drawSkewedSprite(int xOffset, double angle, int centerY) {
        transformSprite(xOffset, angle, centerY);
    }

    // 🟫 Render sprite with mask from Background
    public void drawMasked(Background mask, int x, int y) {
        renderMasked(myPixels, mask.aByteArray1450, x + offsetX, y + offsetY, myWidth, myHeight);
    }

    // 🔧 Internal rendering functions

    private void renderToScreen(int[] buffer, int x, int y, int width, int height) {
        int srcPos = 0;
        int dstPos = x + y * DrawingArea.width;
        int dstStride = DrawingArea.width - width;

        clipAndRender(buffer, DrawingArea.pixels, srcPos, dstPos, width, height, dstStride);
    }

    private void clipAndRender(int[] src, int[] dst, int srcOffset, int dstOffset, int w, int h, int stride) {
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                int pixel = src[srcOffset++];
                if (pixel != 0 && pixel != -1) {
                    dst[dstOffset++] = pixel;
                } else {
                    dstOffset++;
                }
            }
            dstOffset += stride;
        }
    }

    private void renderAlpha(int[] src, int x, int y, int w, int h, int alpha) {
        int dstOffset = x + y * DrawingArea.width;
        int srcOffset = 0;
        int dstStride = DrawingArea.width - w;

        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                int color = src[srcOffset++];
                if (color != 0) {
                    int dstColor = DrawingArea.pixels[dstOffset];
                    DrawingArea.pixels[dstOffset++] = blend(dstColor, color, alpha);
                } else {
                    dstOffset++;
                }
            }
            dstOffset += dstStride;
        }
    }

    private int blend(int bottom, int top, int alpha) {
        int invAlpha = 256 - alpha;
        return (((top & 0xff00ff) * alpha + (bottom & 0xff00ff) * invAlpha & 0xff00ff00)
              + ((top & 0xff00) * alpha + (bottom & 0xff00) * invAlpha & 0xff0000)) >> 8;
    }

    private void rotateSprite(int[] src, int centerX, int centerY, double angle, int scale, int w, int h) {
        int sin = (int)(Math.sin(angle / 326.11) * 65536.0) * scale >> 8;
        int cos = (int)(Math.cos(angle / 326.11) * 65536.0) * scale >> 8;
        int xOffset = -w / 2;
        int yOffset = -h / 2;

        int i3 = (centerX << 16) + (yOffset * sin + xOffset * cos);
        int j3 = (centerY << 16) + (yOffset * cos - xOffset * sin);

        for (int y = 0; y < h; y++) {
            int lineOffset = y * DrawingArea.width;
            for (int x = 0; x < w; x++) {
                int srcX = (i3 >> 16);
                int srcY = (j3 >> 16);
                if (srcX >= 0 && srcX < w && srcY >= 0 && srcY < h) {
                    int color = src[srcX + srcY * w];
                    if (color != 0) {
                        DrawingArea.pixels[lineOffset + x] = color;
                    }
                }
                i3 += cos;
                j3 -= sin;
            }
            i3 += sin;
            j3 += cos;
        }
    }

    private void transformSprite(int offsetX, double angle, int centerY) {
        // Left as-is for your skew transform logic
    }

    private void renderMasked(int[] src, byte[] mask, int x, int y, int w, int h) {
        int dstOffset = x + y * DrawingArea.width;
        int srcOffset = 0;
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                int pixel = src[srcOffset];
                if (pixel != 0 && mask[srcOffset] == 0) {
                    DrawingArea.pixels[dstOffset] = pixel;
                }
                srcOffset++;
                dstOffset++;
            }
            dstOffset += DrawingArea.width - w;
        }
    }
    
  //----------------------------------------------------------------
 // PERFECT 32×32 BLANK SPRITE CONSTRUCTOR FOR getSprite(...)
 public Sprite(int width, int height) {
     // store the dimensions
     this.myWidth  = width;
     this.myHeight = height;
     // copy of original unscaled dimensions (used by some old code)
     this.drawOffsetX = width;
     this.drawOffsetY = height;
     // no offset
     this.offsetX = 0;
     this.offsetY = 0;
     // allocate the pixel buffer
     this.myPixels = new int[width * height];
 }
 //----------------------------------------------------------------

    private boolean valid(int neighbor, int edgeColor) {
        return neighbor > 0 && neighbor != 0xffffff && neighbor != edgeColor;
    }
}