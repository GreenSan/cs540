
public class Square {
	private int row;
	private int col;
	private Board board;
	
	public Square(Board board, int row, int col) {
		this.board = board;
		this.row = row;
		this.col = col;		
	}
	
	public Color getColor() {
		return board.getColor(row,col);
	}

	public int getColumn() {
		return col;
	}

	public int getRow() {
		return row;
	}

	public boolean isLegal() {
		return board.isLegal(row, col, board.getToMove());
	}	
}
