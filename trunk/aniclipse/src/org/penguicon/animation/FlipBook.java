/**
 * FlipBook.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import org.eclipse.draw2d.Label;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * <code>FlipBook</code> figure that changes state.
 * 
 * @author Ann Marie Steichmann
 *
 */
public class FlipBook extends Label {

	/**
	 * "Flip" to the next page (i.e. change state)
	 * @param image The name of the new image that
	 * will be displayed.  It must exist in the icons
	 * directory.
	 */
	public void flip( String image ) {
		
		Image icon = getIcon();
		if ( icon != null ) icon.dispose();
		ImageDescriptor imageDescriptor = 
			Activator.getDefault().getImageDescriptor( image );
		assert( imageDescriptor != null );
		setIcon( imageDescriptor.createImage() );
	}
}
