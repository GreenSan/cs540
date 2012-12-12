import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class Reversi extends JFrame implements MouseListener {
	private boolean humanMoving;
	private int numSquares;
	private int timePerMove;
	private JLayeredPane layeredPane;
	private JPanel board;
	private Board model;
	private List<JSquare> squares = new LinkedList<JSquare>();
	private JLabel statusLabel;
	private Timer statusBarTimer;
	private long nextMoveAt;
	private Color humanPlayer;

	protected void start() {
		if(!humanMoving) {
			cpuMove();
		}
	}

	public Reversi(Board boardModel, Color humanPlayer, int timePerMove) {
		this.model = boardModel;
		this.timePerMove = timePerMove;
		this.humanPlayer = humanPlayer;
		Dimension boardSize = new Dimension(600, 600);

		numSquares = boardModel.getWidth();
		setLayout(new BorderLayout());

		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane, BorderLayout.CENTER);
		createStatusBar();
		layeredPane.setPreferredSize(boardSize);

		board = new JPanel();
		layeredPane.add(board, JLayeredPane.DEFAULT_LAYER);

		board.setLayout(new GridLayout(numSquares, numSquares));
		board.setPreferredSize(boardSize);
		board.setBounds(0, 0, boardSize.width, boardSize.height);

		Iterator<Square> itr = boardModel.getSquares();
		while (itr.hasNext()) {
			JSquare square = new JSquare(itr.next());
			board.add(square);
			squares.add(square);
			square.addMouseListener(this);			
		}
	
		KeyStroke keystroke = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
	    board.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.undo(); // should check if not empty
				redraw();
				humanMoving = !humanMoving;
				cpuMove();
			}	    	
	    }, keystroke, JComponent.WHEN_FOCUSED);
	    
	    setTitle("Reversi");
	    
	    if(humanPlayer == Color.WHITE) {
	    	humanMoving = true;
	    } else {
	    	humanMoving = false;
	    }
	    createTimer();
	}

	private void createStatusBar() {
		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setPreferredSize(new Dimension(getWidth(), 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel(model.getToMove().downcase() + " to move...");		
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusPanel.add(statusLabel);
	}
	
	public void makeMove(Position position) {
		model.move(position);
		redraw();
		humanMoving = !humanMoving;
		if(!model.isGameOver()) {
			if(model.mustPass()) {
				model.pass();
				humanMoving = !humanMoving;
				if(!humanMoving) {					
					cpuMove();
				}
			} else {				
				if(!humanMoving)
					cpuMove();
			}
		}
	}
	
	private void redraw() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				for(JSquare s : squares)
					  s.repaint();
				if (model.isGameOver()) {
					int w = model.getWhiteCount();
					int b = model.getBlackCount();
					if(w == b)
						statusLabel.setText("White has " + w + " squares. Black has " + b + ". A draw!");
					else if(w > b)
						statusLabel.setText("White has " + w + " squares. Black has " + b + ". White wins!");
					else
						statusLabel.setText("White has " + w + " squares. Black has " + b + ". Black wins!");
				} else if(model.mustPass()) {
					statusLabel.setText(model.getToMove().downcase() + " has no legal moved and had to pass. " + model.getToMove().opposite().downcase() + " to move...");
				} else { 
					statusLabel.setText(model.getToMove().downcase() + " to move...");
				}				
			}
		};
		if(SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(r);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if(humanMoving) {
			JSquare sq = (JSquare) e.getComponent();
			Position p = sq.getPosition();
			if (sq != null && model.isLegal(p.getR(), p.getC(), model.getToMove())) {
				makeMove(p);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
			
	private void cpuMove() {
		// We are on the event-dispatching thread, so we must make haste		
		nextMoveAt = System.currentTimeMillis() + timePerMove * 1000;
		final long lastModification = model.getLastModification();
		Runnable r = new Runnable() {						
			@Override
			public void run() {
				final Player cpu = new PlayerImpl();
				cpu.color(humanPlayer.opposite());
		
				final boolean[] stopThread = new boolean[] {true};
				Runnable s = new Runnable() {
					@Override
					public void run() {
						try {
							cpu.move(model.getBoard());
						} catch(ThreadDeath x) {
							// okay
						} finally {
							stopThread[0] = false;
						}
					}
				};
				Thread th = new Thread(s);
				th.setDaemon(true);
				th.start();

				try {
					Thread.sleep(timePerMove * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
				if(model.getLastModification() == lastModification) {
					Position mv = cpu.bestSoFar();
					if(mv == null) {
						if(stopThread[0])
							th.stop();						
						throw new IllegalStateException("CPU did not decide on a move");
					}
					if(!model.isLegal(mv.getR(), mv.getC(), model.getToMove())) {
						if(stopThread[0])
							th.stop();						
						throw new IllegalStateException("CPU returned an illegal move!");
					}
					makeMove(mv);
				}
				if(stopThread[0])
					th.stop();
			}
		};
		Thread th = new Thread(r);
		th.start();
	}

	private void createTimer() {		
		int delay = 1000;
		  ActionListener taskPerformer = new ActionListener() {
		      public void actionPerformed(ActionEvent evt) {
		    	  if(!humanMoving && !model.isGameOver()) {
			    	  // this I believe executed on the event-dispatching thread, so it is safe		    		  
		    		  int remaining = Math.round((nextMoveAt - System.currentTimeMillis()) / 1000);
		    		  statusLabel.setText(model.getToMove().downcase() + " to move. Next move in " + remaining + " second(s).");
		    	  }
		      }
		  };
		  statusBarTimer = new Timer(delay, taskPerformer);
		  statusBarTimer.start();
	}

	public void play() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		start();
	}
}
