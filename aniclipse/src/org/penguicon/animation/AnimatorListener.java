/**
 * AnimatorListener.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation;

/**
 * <code>AnimatorListener</code> implementers will be notified of
 * events in the <code>Animator</code>.
 * 
 * @author Ann Marie Steichmann
 *
 */
public interface AnimatorListener {

	/**
	 * Notify listener that the <code>Animator</code> has terminated
	 */
	public void onTerminated();
	
	/**
	 * Notify listener that the <code>Animator</code> has started
	 */
	public void onStarted();
	
	/**
	 * Notify listener that the <code>Animator</code> has resumed 
	 */
	public void onResumed();
	
	/**
	 * Notify listener that the <code>Animator</code> has suspended
	 */
	public void onSuspended();
	
	/**
	 * Notify listener that the <code>Animator</code> is processing the
	 * next frame.
	 * @param currentFrame the current frame
	 */
	public void onFrameUpdated( int currentFrame );
}
