import java.util.Random;

public class PlayerImpl extends Player {
	private Color color;
	private Position bestSoFar;

	@Override
	public void color(Color color) {
		this.color = color;
	}

	@Override
	public void move(Color[][] board) {
		// Delete everything in this method and include your implementation here.
		Position[] l = getLegalMoves(board, color);
		if (l.length > 0) {
			Random r = new Random();
			bestSoFar = l[r.nextInt(l.length)];
		}
	}

	@Override
	public Position bestSoFar() {
		return bestSoFar;
	}
}
