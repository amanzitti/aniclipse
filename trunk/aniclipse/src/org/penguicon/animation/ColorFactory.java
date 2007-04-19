/**
 * ColorFactory.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

/**
 * <code>ColorFactory</code> factory for retrieving color constants
 * @author Ann Marie Steichmann
 *
 */
public class ColorFactory {

	/**
	 * @param name The name of the color constant
	 * @return The associated <code>Color</code> (defaults to
	 * black)
	 */
	public static Color getColor( String name ) {
		
		if ( name.equals( "ColorConstants.red" ) ) {
			return ColorConstants.red;
		} else if ( name.equals( "ColorConstants.yellow" ) ) {
			return ColorConstants.yellow;
		} else if ( name.equals( "ColorConstants.blue" ) ) {
			return ColorConstants.blue;
		}
		return ColorConstants.black;
	}
}
