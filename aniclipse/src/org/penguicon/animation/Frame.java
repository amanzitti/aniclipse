/**
 * Frame.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>Frame</code> contains the commands for each frame.
 * @author Ann Marie Steichmann
 *
 */
public class Frame {
	
	private static String HEADER = "Frame: ";
	
	private String id;
	
	private List<Command> commands = new ArrayList<Command>();
	
	private static Frame _instance;
	
	private Frame( String id ) {
		this.id = id;
	}
	
	/**
	 * @return the frame identifier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param command The <code>Command</code> to add
	 */
	public void addCommand( Command command ) {
		commands.add( command );
	}
	
	/**
	 * Execute all the commands in this frame
	 */
	public void executeCommands() {
		
		for ( Command command : commands ) {
			command.execute();
		}
	}

	/**
	 * @param line A line of text from the animation file
	 * @return A new <code>Frame</code> instance if it
	 * can be built from the given text or the last frame
	 * built (or <code>null</code> if the frame can't be
	 * built and has never been built before)
	 */
	public static Frame parseFrame( String line ) {
		
		if ( line.startsWith( HEADER ) ) {
			_instance = new Frame( line.substring( HEADER.length() ) );
		}
		return _instance;
	}
}
