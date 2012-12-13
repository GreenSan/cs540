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
			System.out.println("No moves");
			return;
		}
		bestSoFar = pos[0];
		int depth = 2;
		while(true){
			for (int i = 0; i < pos.length; i++) {
				Position position = pos[i];
				GameState state = makeMove(board, position, color);
				visited(state.getBoard());
				if(color == Color.WHITE){
					double tmpAlpha = MinMax(state.getBoard(), alpha, beta, color.opposite(), depth);
					if (tmpAlpha > alpha) {
						alpha = tmpAlpha;
						bestSoFar = position;
						System.out.println("updated");
					}
				} else {
					double tmpBeta = MinMax(state.getBoard(), alpha, beta, color.opposite(), depth);
					if (tmpBeta < beta) {
						beta = tmpBeta;
						bestSoFar = position;
						System.out.println("updated");
					}
				}
			}
			System.out.println(depth);
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
			if(nextColor == Color.WHITE){
				double tmpAlpha = MinMax(state.getBoard(), alpha, beta, nextColor.opposite(), depth - 1);
				if (tmpAlpha > alpha) {
					alpha = tmpAlpha;
				}
			} else {
				double tmpBeta = MinMax(state.getBoard(), alpha, beta, nextColor.opposite(), depth - 1);
				if (tmpBeta < beta) {
					beta = tmpBeta;
				}
			}
			if(alpha > beta){
				return beta;
			}
		}
		return alpha;
	}
}
