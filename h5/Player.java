
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public abstract class Player {
	private List<Color[][]> visitLog = new LinkedList<Color[][]>();
	
	public abstract void color(Color color);
	public abstract void move(Color[][] board);
	public abstract Position bestSoFar();	
	
	protected Position[] getLegalMoves(Color[][] board, Color color) {
		List<Position> pos = new LinkedList<Position>();
		int width = board.length;
		for(int i = 0; i < width; i++)
			for(int j = 0; j < width; j++)
				if(isLegal(board, i, j, color))
					pos.add(new Position(i,j));
		return pos.toArray(new Position[pos.size()]);
	}
	
	protected Double getScore(Color[][] b) {
		return (double) (getCount(b, Color.WHITE) - getCount(b, Color.BLACK)); 
	}
	
	protected void visited(Color[][] b) {
		if(visitLog.size() < 100)
			visitLog.add(b);
	}
	
	private int getCount(Color[][] board, Color c) {
		int k = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j] == c)
					k++;
			}
		}
		return k;
	}
	
	protected GameState makeMove(Color[][] board, Position p, Color color) {
		board = copy(board);
		int c = p.getC();
		int r = p.getR();
		int width = board.length;
		Color opposite = color.opposite();
		if(!isLegal(board, r, c, color)) {
			System.err.println("<"+r+","+c+","+color+">");
			System.err.println(toString(board));
			throw new IllegalStateException("You have requested an illegal move");
		} else {
			board[r][c] = color;			
			if(c < width - 2) {
				if(board[r][c+1] == opposite) { // flip right
					for(int i = c+2; i < width; i++) {
						if(board[r][i] == null) break;
						if(board[r][i] == color) {
							int j = c+1;
							while(board[r][j] == opposite) {
								board[r][j] = color;
								j++;
							}
							break;
						}
					}
				}
			}
			if(c > 1) {
				if(board[r][c-1] == opposite) { // flip left
					for(int i = c-2; i >= 0; i--) {
						if(board[r][i] == null) break;
						if(board[r][i] == color) {
							int j = c-1;
							while(board[r][j] == opposite) {
								board[r][j] = color;
								j--;
							}
							break;							
						}

					}
				}
			}
			if(r < width - 2) {
				if(board[r+1][c] == opposite) { // flip down
					for(int i = r+2; i < width; i++) {
						if(board[i][c] == null) break;
						if(board[i][c] == color) {
							int j = r+1;
							while(board[j][c] == opposite) {
								board[j][c] = color;
								j++;
							}
							break;							
						}
					}
				}
			}
			if(r > 1) {
				if(board[r-1][c] == opposite) { // flip up
					for(int i = r-2; i >= 0; i--) {
						if(board[i][c] == null) break;
						if(board[i][c] == color) {
							int j = r-1;
							while(board[j][c] == opposite) {
								board[j][c] = color;
								j--;
							}
							break;							
						}
					}
				}
			}
			if(c < width - 2 && r > 1) { // flip up right			
				if(board[r-1][c+1] == opposite) {
					for(int i = 0; r-1-i >= 0 && c+1+i < width; i++) {
						if(board[r-1-i][c+1+i] == null) break;
						if(board[r-1-i][c+1+i] == color ) {
							int j = r-1;
							int k = c+1;
							while(board[j][k] == opposite) {
								board[j][k] = color;
								j--;
								k++;
							}
							break;
						}
					}
				}
			}
			if(c > 1 && r > 1) { // flip up left			
				if(board[r-1][c-1] == opposite) {
					for(int i = 0; r-1-i >= 0 && c-1-i >= 0; i++) {
						if(board[r-1-i][c-1-i] == null) break;
						if(board[r-1-i][c-1-i] == color ) {
							int j = r-1;
							int k = c-1;
							while(board[j][k] == opposite) {
								board[j][k] = color;
								j--;
								k--;
							}
							break;
						}
					}
				}
			}
			if(c < width - 2 && r < width - 2) { // flip down right			
				if(board[r+1][c+1] == opposite) {
					for(int i = 0; r+1+i < width && c+1+i < width; i++) {
						if(board[r+1+i][c+1+i] == null) break;
						if(board[r+1+i][c+1+i] == color ) {
							int j = r+1;
							int k = c+1;
							while(board[j][k] == opposite) {
								board[j][k] = color;
								j++;
								k++;
							}
							break;
						}
					}
				}
			}
			if(c > 1 && r < width - 2) { // flip down left			
				if(board[r+1][c-1] == opposite) {
					for(int i = 0; r+1+i < width && c-1-i >= 0; i++) {
						if(board[r+1+i][c-1-i] == null) break;
						if(board[r+1+i][c-1-i] == color ) {
							int j = r+1;
							int k = c-1;
							while(board[j][k] == opposite) {
								board[j][k] = color;
								j++;
								k--;
							}
							break;
						}
					}
				}
			}
		}
		return new GameState(board, getLegalMoves(board, opposite).length > 0 ? Move.valueOf(opposite.toString()) 
				: getLegalMoves(board, color).length > 0 ? Move.valueOf(color.toString()) 
				: Move.GAME_OVER);
	}
	
	private String toString(Color[][] board) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				sb.append(board[i][j] == null ? '.' : board[i][j] == Color.WHITE ? 'W' : 'B');
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	private Color[][] copy(Color[][] board) {
		Color[][] res = new Color[board.length][];
		for(int i = 0; i < board.length; i++)
			res[i] = Arrays.copyOf(board[i], board[i].length);
		return res;
	}
	
	private boolean isLegal(Color[][] board, int r, int c, Color color) {
		int width = board.length;
		Color opposite = color == Color.WHITE ? Color.BLACK : Color.WHITE;
		if(board[r][c] != null)
		  return false;		
		if(c < width - 2) { // look right			
			if(board[r][c+1] == opposite) {
				for(int i = c+2; i < width; i++) {
					if(board[r][i] == null) break;
					if(board[r][i] == color)
						return true;					
				}
			}
		}
		if(c > 1) { // look left
			if(board[r][c-1] == opposite) {
				for(int i = c-2; i >= 0; i--) {
					if(board[r][i] == null) break;
					if(board[r][i] == color)
						return true;
				}
			}
		}
		if(r < width - 2) { // look down
			if(board[r+1][c] == opposite) {
				for(int i = r+2; i < width; i++) {
					if(board[i][c] == null) break;
					if(board[i][c] == color)
						return true;					
				}
			}
		}
		if(r > 1) { // look up
			if(board[r-1][c] == opposite) {
				for(int i = r-2; i >= 0; i--) {
					if(board[i][c] == null) break;
					if(board[i][c] == color)
						return true;
				}
			}
		}
		if(c < width - 2 && r > 1) { // look up right			
			if(board[r-1][c+1] == opposite) {
				int i = 1;
				while(c+1+i < width && r-1-i >= 0) {
					if(board[r-1-i][c+1+i] == null) break;
					if(board[r-1-i][c+1+i] == color)
						return true;						
					i++;
				}
			}
		}
		if(c > 1 && r > 1) { // look up left			
			if(board[r-1][c-1] == opposite) {
				int i = 1;
				while(c-1-i >= 0 && r-1-i >= 0) {
					if(board[r-1-i][c-1-i] == null) break;
					if(board[r-1-i][c-1-i] == color)
						return true;						
					i++;
				}
			}
		}
		if(c < width - 2 && r < width - 2) { // look down right			
			if(board[r+1][c+1] == opposite) {
				int i = 1;
				while(c+1+i < width && r+1+i < width) {
					if(board[r+1+i][c+1+i] == null) break;
					if(board[r+1+i][c+1+i] == color)
						return true;						
					i++;
				}
			}
		}
		if(c > 1 && r < width - 2) { // look down left			
			if(board[r+1][c-1] == opposite) {
				int i = 1;
				while(c-1-i >= 0 && r+1+i < width) {
					if(board[r+1+i][c-1-i] == null) break;
					if(board[r+1+i][c-1-i] == color)
						return true;						
					i++;
				}
			}
		}
		return false;		
	}
}
