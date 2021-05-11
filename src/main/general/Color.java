package main.general;

import main.utils.UtilMath;

public class Color {

    private int red;
    private int green;
    private int blue;
    private int alpha;

    public static enum Grayscale {
        LIGHTNESS,
        AVERAGE,
        LUMINOSITY
    }

    public Color(int red, int green, int blue, int alpha) {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Color(int RGB) {
        this.alpha = (RGB >> 24) & 0xFF;
        this.red = (RGB >> 16) & 0xFF;
        this.green = (RGB >> 8) & 0xFF;
        this.blue = RGB & 0xFF;
    }

    /**
     * Return the standard ARGB integer value of this color in the form AAAA AAAA RRRR RRRR BBBB BBBB GGGG GGGG
     *
     * @return 32-Bit ARGB value
     */
    public int getARGB() {
        return (this.alpha << 24) + (this.red << 16) + (this.green << 8) + (this.blue);
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    // Grayscale algorithms originated from: https://www.johndcook.com/blog/2009/08/24/algorithms-convert-color-grayscale/
    public int getGrayscale(Grayscale type) {
        switch (type) {
            case LIGHTNESS:
                return (UtilMath.max(this.red, this.green, this.blue) + UtilMath.min(this.red, this.blue, this.green)) / 2;
            case LUMINOSITY:
                return (int) (0.21 * this.red + 0.72 * this.green + 0.07 * this.blue);
            case AVERAGE:
            default:
                return (this.red + this.green + this.blue) / 3;
        }
    }

}
