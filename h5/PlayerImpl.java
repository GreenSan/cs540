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
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		Position[] pos = getLegalMoves(board, color);
		if(pos.length == 0){ //If no moves
			//Should this happen?
			//System.out.println("No moves");
			return;
		}
		bestSoFar = pos[0];
		int depth = 0;
		while(true){
			for (int i = 0; i < pos.length; i++) {
				Position position = pos[i];
				GameState state = makeMove(board, position, color);
				visited(state.getBoard());
				double newScore = MinMax(state.getBoard(), alpha, beta, color.opposite(), depth);
				//System.out.println(newScore);
				if(color == Color.WHITE){
					if (newScore > alpha) {
						alpha = newScore;
						bestSoFar = position;
						//System.out.println("updated");
					}
				} else {
					if (newScore < beta) {
						beta = newScore;
						bestSoFar = position;
						//System.out.println("updated");
					}
				}
			}
			//System.out.println(depth);
			depth++;
		}
	}

	@Override
	public Position bestSoFar() {
		return bestSoFar;
	}
	
	private double MinMax(Color[][] board, double alpha, double beta, Color nextColor, int depth){
		if(depth == 0){
			return getScore(board);
		}
		Position[] l = getLegalMoves(board, nextColor);
		if(l.length == 0){ //If no moves
			if(getLegalMoves(board, nextColor.opposite()).length == 0){
				return getScore(board);
			} else {
				nextColor = nextColor.opposite();
			}
		}
		for (int i = 0; i < l.length; i++) {
			Position position = l[i];
			GameState state = makeMove(board, position, nextColor);
			visited(state.getBoard());
			double newScore = MinMax(state.getBoard(), alpha, beta, nextColor.opposite(), depth - 1);
			if(nextColor == Color.WHITE){
				if (newScore > alpha) {
					alpha = newScore;
				}
				if(alpha > beta){
					return beta;
				}
			} else {
				if (newScore< beta) {
					beta = newScore;
				}
				if(alpha > beta){
					return alpha;
				}
			}
		}
		if(nextColor == Color.WHITE){
			return alpha;
		} else {
			return beta;
		}
	}
}
