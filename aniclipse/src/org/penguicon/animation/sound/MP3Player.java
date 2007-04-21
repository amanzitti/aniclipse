/**
 * MP3Player.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation.sound;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;

/**
 * <code>MP3Player</code> utility for playing mp3s.
 * 
 * @author Ann Marie Steichmann
 * 
 */
public class MP3Player {

	private static Map<String,Player> players = new HashMap<String,Player>();
	
	/**
	 * Play the given mp3
	 * @param mp3 The full path to the mp3
	 */
	public static void play( final String mp3 ) {

		Player player = players.get( mp3 );
		if ( player == null ) {
			try {
				MediaLocator ml = new MediaLocator( "file:" + mp3 );
				player = Manager.createPlayer(ml);
				player.addControllerListener(new ControllerListener() {
					public void controllerUpdate(ControllerEvent event) {
						if (event instanceof EndOfMediaEvent) {
							stop( mp3 );
						}
					}
				});
			} catch (NoPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			players.put( mp3, player );
		}
		player.realize();
		player.start();
	}
	
	/**
	 * Stop playing the given mp3
	 * @param mp3 The full path to the mp3
	 */
	public static void stop( final String mp3 ) {

		Player player = players.get( mp3 );
		if ( player != null ) {
			player.stop();
			player.close();	
			players.remove( mp3 );
		}
	}
}
