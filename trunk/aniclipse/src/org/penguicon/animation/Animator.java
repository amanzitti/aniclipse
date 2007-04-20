/**
 * Animator.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.widgets.Display;

/**
 * <code>Animator</code> controls the states of the figures during
 * animation.
 * 
 * @author Ann Marie Steichmann
 *
 */
public class Animator {
	
	private List<Frame> frames = new ArrayList<Frame>();
	
	private Panel panel;
	private LayoutManager layout;
	private Map<String,IFigure> figures;
	
	private boolean isRunning = false;
	private boolean isSuspended = false;
	private boolean isTerminated = false;
	
	private int fps = 24;
	
	private int currentFrame;
	
	private List<AnimatorListener> listeners;
	
	/**
	 * Constructs a new <code>Animator</code>
	 * @param panel the panel that figures will be added to
	 */
	public Animator( Panel panel ) {
		
		this.panel = panel;
		panel.setLayoutManager( layout = new XYLayout() );
		figures = new HashMap<String,IFigure>();
		listeners = new ArrayList<AnimatorListener>();
	}
	
	/**
	 * Read in the given animation and build the commands from the
	 * given animation file contents.
	 * @param file the animation file
	 * @return the frame count
	 */
	public int loadAnimation( IFile file ) {
		
		try {
			
			frames.clear();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader( 
													file.getContents() ) );
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				
				// skip over comments
				if ( line.startsWith( "#" ) ) continue;
				
				Frame frame = Frame.parseFrame( line );
				assert( frame != null );
				
				if ( !frames.contains( frame ) ) {
					frames.add( frame );
				}

				Command command = Command.parseCommand(panel, layout, figures, line);
				if ( command != null ) {
					frame.addCommand( command );
				}
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return frames.size();
	}
	
	/**
	 * Run the animation up to the given frame (immediately)
	 * @param frame The frame number
	 */
	public void runUntil( int frame ) {

		int frameCt = frames.size();
		if ( frameCt == 0 || frame > frameCt - 1 )  return;
		
		if ( isSuspended() || isTerminated() ) {
		
			currentFrame = 0;
			
			panel.removeAll();
			
			while ( currentFrame <= frame ) {
				
				Display.getDefault().syncExec( new Runnable() {
					
					public void run() {
						
						Frame frame = frames.get( currentFrame );
						fireFrameUpdated( currentFrame++ );
						frame.executeCommands();
						panel.revalidate();
					}
				});
			}
		}
		
	}
	
	/**
	 * Start playing the animation
	 */
	public void run() {
		
		isRunning = true;
		isSuspended = false;
		isTerminated = false;
		
		panel.removeAll();
		
		fireStarted();
		
		Thread thread = new Thread( new Runnable() {
			public void run() {
				while( !isTerminated() ) {
					
					if ( !isSuspended() ) {
						
						long before = System.currentTimeMillis();
						
						Display.getDefault().syncExec( new Runnable() {
							
							public void run() {
								
								Frame frame = frames.get( currentFrame );
								fireFrameUpdated( currentFrame );
								frame.executeCommands();
								panel.revalidate();
							}
						});
						
						long after = System.currentTimeMillis();
						
						if ( currentFrame++ == frames.size() - 1 ) break;
						
						// smooth out frame rate
						int time = 1000 / fps;
						int diff = (int)( after - before );
						if ( time > diff ) {
							time = time - diff;
						} else {
							time = 0;
						}
						
						try {
							Thread.sleep( time );
						} catch (InterruptedException e) {
							// ignore
						}
					}
					
				}
				terminate();
				currentFrame = 0;				
			}
		});
		thread.start();
	}
			
	/**
	 * @return true if the animation was terminated
	 */
	public boolean isTerminated() {
		return isTerminated;
	}
	
	/**
	 * @return true if the animation was started
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * @return true if the animation is currently suspended
	 */
	public boolean isSuspended() {
		return isSuspended;
	}
	
	/**
	 * Resume playing the animation
	 */
	public void resume() {
		
		isSuspended = false;
		fireResumed();
	}
	
	/**
	 * Suspend playing the animation
	 */
	public void suspend() {
	
		isSuspended = true;
		fireSuspended();
	}
	
	/**
	 * Terminate playing the animation
	 */
	public void terminate() {
	
		isRunning = false;
		isSuspended = false;
		isTerminated = true;
		fireTerminated();
	}
	
	/**
	 * @param fps The frames per second
	 */
	public void setFramesPerSecond( int fps ) {
		this.fps = fps;
	}
	
	/**
	 * @param listener The <code>AnimatorListener</code> that will 
	 * be notified of <code>Animator</code> events.
	 */
	public void addListener( AnimatorListener listener ) {
		listeners.add( listener );
	}
	
	/**
	 * @param listener The <code>AnimatorListener</code> that will
	 * no longer be notified of <code>Animator</code> events.
	 */
	public void removeListener( AnimatorListener listener ) {
		listeners.remove( listener );
	}
	
	private void fireStarted() {
		
		for ( AnimatorListener listener : listeners ) {
			listener.onStarted();
		}
	}
	
	private void fireResumed() {
		
		for ( AnimatorListener listener : listeners ) {
			listener.onResumed();
		}
	}
	
	private void fireSuspended() {
		
		for ( AnimatorListener listener : listeners ) {
			listener.onSuspended();
		}
	}
	
	private void fireTerminated() {
		
		for ( AnimatorListener listener : listeners ) {
			listener.onTerminated();
		}
	}
	
	private void fireFrameUpdated( int currentFrame ) {
		
		for ( AnimatorListener listener : listeners ) {
			listener.onFrameUpdated( currentFrame );
		}
	}
}
