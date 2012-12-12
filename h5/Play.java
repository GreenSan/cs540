import java.awt.EventQueue;

public class Play {
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				int timePerMove = 5;
				Color humanPlayer = Color.WHITE;
				if(args.length > 0)
					timePerMove = Integer.parseInt(args[0]);
				if(args.length > 1)
					humanPlayer = Color.valueOf(args[1].toUpperCase());
				Reversi f = new Reversi(new Board(8), humanPlayer, timePerMove);
				f.play();
			}
		});
	}

}
