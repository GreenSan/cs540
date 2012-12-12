
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Board {
	private int width;
	private Color[][] board;
	private Color toMove = Color.WHITE;
	Stack<Color[][]> history = new Stack<Color[][]>();
	private long lastModification;
	
	public Board(int width) {
		if (width % 2 != 0)
			throw new IllegalStateException();
		this.width = width;
		this.board = new Color[this.width][];
		for (int i = 0; i < width; i++) {
			board[i] = new Color[width];
			for(int j = 0; j < width; j++)
				board[i][j] = null;
		}
		board[width / 2][width / 2] = Color.WHITE;
		board[width / 2 - 1][width / 2 - 1] = Color.WHITE;
		board[width / 2 - 1][width / 2] = Color.BLACK;
		board[width / 2][width / 2 - 1] = Color.BLACK;
	}
	
	public Board(Color[][] board, Color toMove) {
		this.board = board;
		this.toMove = toMove;
		this.width = board.length;
	}
	
	public void undo() {
		if(!history.isEmpty()) {
			this.board = history.pop();		
			toMove = toMove == Color.WHITE ? Color.BLACK : Color.WHITE;
			lastModification = System.currentTimeMillis();
		}
	}
	
	public boolean isLegal(int r, int c, Color color) {
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
	
	public boolean mustPass() {
		return getLegalMoves(toMove).size() == 0 && getLegalMoves(toMove.opposite()).size() > 0;
	}
	
	public void pass() {
		if(mustPass()) {
			this.toMove = this.toMove.opposite();
			lastModification = System.currentTimeMillis();
		}
	}
	
	public boolean move(Position p) {
		int c = p.getC();
		int r = p.getR();
		if(isLegal(r, c, toMove)) {
			push();
			board[r][c] = toMove;
			Color opposite = toMove.opposite();						
			if(c < width - 2) {
				if(board[r][c+1] == opposite) { // flip right
					for(int i = c+2; i < width; i++) {
						if(board[r][i] == null) break;
						if(board[r][i] == toMove) {
							int j = c+1;
							while(board[r][j] == opposite) {
								board[r][j] = toMove;
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
						if(board[r][i] == toMove) {
							int j = c-1;
							while(board[r][j] == opposite) {
								board[r][j] = toMove;
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
						if(board[i][c] == toMove) {
							int j = r+1;
							while(board[j][c] == opposite) {
								board[j][c] = toMove;
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
						if(board[i][c] == toMove) {
							int j = r-1;
							while(board[j][c] == opposite) {
								board[j][c] = toMove;
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
						if(board[r-1-i][c+1+i] == toMove ) {
							int j = r-1;
							int k = c+1;
							while(board[j][k] == opposite) {
								board[j][k] = toMove;
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
						if(board[r-1-i][c-1-i] == toMove ) {
							int j = r-1;
							int k = c-1;
							while(board[j][k] == opposite) {
								board[j][k] = toMove;
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
						if(board[r+1+i][c+1+i] == toMove ) {
							int j = r+1;
							int k = c+1;
							while(board[j][k] == opposite) {
								board[j][k] = toMove;
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
						if(board[r+1+i][c-1-i] == toMove ) {
							int j = r+1;
							int k = c-1;
							while(board[j][k] == opposite) {
								board[j][k] = toMove;
								j++;
								k--;
							}
							break;
						}
					}
				}
			}
			toMove = toMove == Color.WHITE ? Color.BLACK : Color.WHITE;
			lastModification = System.currentTimeMillis();
			return true;
		} else {
			return false;
		}
	}

	private void push() {
		Color[][] b = new Color[this.width][];
		for (int i = 0; i < width; i++) {
			b[i] = new Color[width];
			for(int j = 0; j < width; j++)
				b[i][j] = board[i][j];
		}
		history.add(b);
	}

	public int getWidth() {
		return width;
	}
	
	public Iterator<Square> getSquares() {
		return new Iterator<Square>() {
			int r, c = 0;
			
			@Override
			public boolean hasNext() {
				return r < width;
			}

			@Override
			public Square next() {
				Square sq = new Square(Board.this, r, c);
				if(c < width - 1)
					c++;
				else {
					r++;
					c = 0;
				}
				return sq;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();				
			}			
		};
	}

	public boolean isGameOver() {		
		return getLegalMoves(Color.WHITE).size() == 0 && getLegalMoves(Color.BLACK).size() == 0;
	}
	
	private List<Position> getLegalMoves(Color c) {
		List<Position> pos = new LinkedList<Position>();
		for(int i = 0; i < width; i++)
			for(int j = 0; j < width; j++)
				if(isLegal(i, j, c))
					pos.add(new Position(i,j));
		return pos;
	}

	public Color getColor(int row, int col) {
		return board[row][col];
	}

	public Color getToMove() {
		return toMove;
	}

	public int getWhiteCount() {
		int c = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j] == Color.WHITE)
					c++;
			}
		}
		return c;
	}

	public int getBlackCount() {
		int c = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j] == Color.BLACK)
					c++;
			}
		}
		return c;
	}
	
	public Color[][] getBoard() {
		Color[][] res = new Color[board.length][];
		for(int i = 0; i < board.length; i++)
			res[i] = Arrays.copyOf(board[i], board[i].length);
		return res;
	}

	public long getLastModification() {
		return lastModification;
	}
}
