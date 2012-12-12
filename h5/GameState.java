

public class GameState {
	private Color[][] board;
	private Move move;
	
	public GameState(Color[][] board, Move move) {
		this.board = board;
		this.move = move;
	}

	public Color[][] getBoard() {
		return board;
	}

	public Move getMove() {
		return move;
	}
}
