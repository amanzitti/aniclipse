/**
 * FigureFactory.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.Triangle;

/**
 * <code>FigureFactory</code> factory for creating figures.
 * @author Ann Marie Steichmann
 *
 */
public class FigureFactory {

	/**
	 * @param type The type of figure to create
	 * @return A figure of the given type (or <code>null</code> if 
	 * the type can't be created)
	 */
	public static IFigure createFigure( String type ) {
	
		// TODO: Could use reflection here perhaps?
		if ( type.equals( "org.eclipse.draw2d.RectangleFigure" ) ) {
			return new RectangleFigure();
		} else if ( type.equals( "org.eclipse.draw2d.Ellipse" ) ) {
			return new Ellipse();
		} else if ( type.equals( "org.eclipse.draw2d.Triangle" ) ) {
			return new Triangle();
		} else if ( type.equals( "org.penguicon.animation.FlipBook" ) ) {
			return new FlipBook();
		}
		return null;
	}
}
