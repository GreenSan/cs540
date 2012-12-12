

public enum Color {
	WHITE, BLACK;
	
	public String downcase() {
		switch(this) {
		case WHITE : return "White";
		case BLACK : return "Black";
		default : throw new IllegalStateException();
		}
	}

	public Color opposite() {
		switch(this) {
		case WHITE : return BLACK;
		case BLACK : return WHITE;
		default : throw new IllegalStateException();
		}
	}
}
