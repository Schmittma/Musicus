package general;

public class Tone {

	private Pitch pitch;
	private int octave;
	
	//Stores the position in the current System/Staff not the coordinate
	//This is used to determine the sequence at which the Tones have to be played
	private int position;
	
	//These two variables determine the length of the tone (e.g. quarter, eighth)
	//In the simple case we have a denominator of 1 and a numerator of the basic lengths.
	//In the complex case the denominator can be > 1, thus we need a variable to hold it.
	private int lengthDenominator;
	private int lengthNumerator;
	
	public Tone(Pitch pitch, int octave, int position, int lengthDenominator, int lengthNumerator) {
		super();
		this.pitch = pitch;
		this.octave = octave;
		this.position = position;
		this.lengthDenominator = lengthDenominator;
		this.lengthNumerator = lengthNumerator;
	}
	
	
	public Pitch getPitch() {
		return pitch;
	}
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}
	public int getOctave() {
		return octave;
	}
	public void setOctave(int octave) {
		this.octave = octave;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getLengthDenominator() {
		return lengthDenominator;
	}
	public void setLengthDenominator(int lengthDenominator) {
		this.lengthDenominator = lengthDenominator;
	}
	public int getLengthNumerator() {
		return lengthNumerator;
	}
	public void setLengthNumerator(int lengthNumerator) {
		this.lengthNumerator = lengthNumerator;
	}
	
	
}
