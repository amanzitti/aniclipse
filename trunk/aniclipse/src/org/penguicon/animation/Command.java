/**
 * Command.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.penguicon.animation.sound.MP3Player;

/**
 * <code>Command</code> parses a command from the given string and 
 * executes the command on demand.
 * 
 * @author Ann Marie Steichmann
 *
 */
public abstract class Command {

	/**
	 * <code>CreateCommand</code> create a new figure and
	 * add it to the associated panel and figure map.
	 * @author Ann Marie Steichmann
	 *
	 */
	static class CreateCommand extends Command {
		
		protected static final String NAME = "create";
		private Panel panel;
		private Map<String,IFigure> figures;
		private String line;
		
		protected CreateCommand( Panel panel, Map<String,IFigure> figures, 
															String line ) {
			this.panel = panel;
			this.figures = figures;
			this.line = line;
		}
		
		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			String[] split = line.split( " " );
			assert( split.length == 3 );

			IFigure figure = FigureFactory.createFigure( split[1] );
			panel.add( figure );
			figures.put( split[2], figure );
		}
	}
	
	/**
	 * <code>SetBackgroundCommand</code> sets the background of the figure
	 * @author Ann Marie Steichmann
	 *
	 */
	static class SetBackgroundCommand extends Command {
		
		protected static final String NAME = "setBackground";
		private Map<String,IFigure> figures;
		private String line;
		
		protected SetBackgroundCommand( Map<String,IFigure> figures, 
														String line ) {
			this.figures = figures;
			this.line = line;
		}

		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			String[] split = line.split( " " );
			assert( split.length == 3 );
			
			IFigure figure = figures.get( split[1] );
			if ( figure != null ) {
				figure.setBackgroundColor( ColorFactory.getColor( split[2] ) );
			}
		}
	}
	
	/**
	 * <code>SetConstraintCommand</code> sets the constraints on the figure
	 * @author Ann Marie Steichmann
	 *
	 */
	static class SetConstraintCommand extends Command {
		
		protected static final String NAME = "setConstraint";
		private LayoutManager layout;
		private Map<String,IFigure> figures;
		private String line;
		
		protected SetConstraintCommand( LayoutManager layout,
				Map<String,IFigure> figures, String line ) {
			this.layout = layout;
			this.figures = figures;
			this.line = line;
		}
		
		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			String[] split = line.split( " " );
			assert( split.length == 6 );
			
			IFigure figure = figures.get( split[1] );
			
			int x = Integer.parseInt( split[2] );
			int y = Integer.parseInt( split[3] );
			int width = Integer.parseInt( split[4] );
			int height = Integer.parseInt( split[5] );
			
			layout.setConstraint( figure, 
					new Rectangle( x, y, width, height ) );
		}
		
	}
	
	/**
	 * <code>FlipCommand</code> transitions to the next state in the
	 * <code>FlipBook</code>.
	 * @author Ann Marie Steichmann
	 *
	 */
	static class FlipCommand extends Command {
		
		protected static final String NAME = "flip";
		private Map<String,IFigure> figures;
		private String line;
		
		protected FlipCommand( Map<String,IFigure> figures, String line ) {
			this.figures = figures;
			this.line = line;
		}

		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			String[] split = line.split( " " );
			assert( split.length == 3 );
			FlipBook flipBook = (FlipBook)figures.get( split[1] );
			flipBook.flip( split[2] );
		}
		
	}
	
	/**
	 * <code>PlayCommand</code> plays mp3s
	 * @author Ann Marie Steichmann
	 *
	 */
	static class PlayCommand extends Command {
		
		protected static final String NAME = "play";
		private String line;
		
		protected PlayCommand( String line ) {
			this.line = line;
		}

		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			final String[] split = line.split( " " );
			assert( split.length == 3);
			Thread thread = new Thread( new Runnable() {
				public void run() {
					MP3Player.play( split[1] );		
				}
			} );
			thread.start();
			try {
				Thread.sleep(1500);
			} catch ( InterruptedException e ) {
				// ignore
			}
		}
		
	}
	
	/**
	 * <code>StopCommand</code> stops playing mp3s
	 * @author Ann Marie Steichmann
	 *
	 */
	static class StopCommand extends Command {
		
		protected static final String NAME = "stop";
		private String line;
		
		protected StopCommand( String line ) {
			this.line = line;
		}

		/* (non-Javadoc)
		 * @see org.penguicon.animation.Command#execute()
		 */
		public void execute() {
			
			String[] split = line.split( " " );
			assert( split.length == 3);
			MP3Player.stop( split[1] );
		}
		
	}	
		
	/**
	 * Execute the command
	 */
	public abstract void execute();
	
	/**
	 * @param panel The <code>Panel</code> that figures can be added to
	 * @param layout The <code>LayoutManager</code> for arranging figures
	 * @param figures The map of created figures
	 * @param line A line of text from the animation file
	 * @return A new <code>Command</code> instance if it can be built
	 * from the given text (or <code>null</code> if it can't be built)
	 */
	public static Command parseCommand( Panel panel, LayoutManager layout,
			Map<String,IFigure> figures, String line ) {
		
		if ( line.startsWith( CreateCommand.NAME ) ) {
			return new CreateCommand( panel, figures, line );
		} else if ( line.startsWith( SetBackgroundCommand.NAME ) ) {
			return new SetBackgroundCommand( figures, line );
		} else if ( line.startsWith( SetConstraintCommand.NAME ) ) {
			return new SetConstraintCommand( layout, figures, line );
		} else if ( line.startsWith( FlipCommand.NAME ) ) {
			return new FlipCommand( figures, line );
		} else if ( line.startsWith( PlayCommand.NAME ) ) {
			return new PlayCommand( line );
		} else if ( line.startsWith( StopCommand.NAME ) ) {
			return new StopCommand( line );
		}
		return null;
	}
}
