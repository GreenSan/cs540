
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class JSquare extends JPanel {
	private static java.awt.Color BGROUND = new java.awt.Color(0,200,0);
	private static java.awt.Color LEGAL = new java.awt.Color(0,200,0);
	
	private Square square;

	public JSquare(Square square) {
		super(new BorderLayout());
		this.square = square;
		setBorder(BorderFactory.createLineBorder(java.awt.Color.black));
		setBackground(BGROUND);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);		
		setBackground(square.isLegal() ? LEGAL : BGROUND);
		if (square.getColor() != null) {			
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(square.getColor() == Color.BLACK ? java.awt.Color.BLACK : java.awt.Color.WHITE);
			g2d.fillOval(0, 0, getWidth(), getHeight());
		}
	}

	public Position getPosition() {
		return new Position(square.getRow(),square.getColumn());
	}

}
