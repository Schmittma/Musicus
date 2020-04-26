package general;

public class Color {

	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	
	
	public Color(int red, int green, int blue, int alpha) {
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Color(int RGB) {
		this.alpha = (RGB>>24) & 0xFF;
		this.red = (RGB>>16) & 0xFF;
		this.green = (RGB>>8) & 0xFF;
		this.blue = RGB & 0xFF;
	}

	/**
	 * Return the standard ARGB integer value of this color in the form AAAA AAAA RRRR RRRR BBBB BBBB GGGG GGGG
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

	
	
}
