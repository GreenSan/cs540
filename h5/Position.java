

public class Position {
	private final int r;
	private final int c;
	public Position(int r, int c) {
		this.r = r;
		this.c = c;
	}
	public int getC() {
		return c;
	}
	public int getR() {
		return r;
	}
	
	@Override
	public String toString() {
		return "<" + r + "," + c + ">";
	}
}
