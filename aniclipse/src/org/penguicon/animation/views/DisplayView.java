/**
 * DisplayView.java
 *
 * Created for Penguicon 5.0, 2007.
 */
package org.penguicon.animation.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.Panel;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.penguicon.animation.Activator;
import org.penguicon.animation.Animator;
import org.penguicon.animation.AnimatorListener;

/**
 * <code>DisplayView</code> displays the figures that are to
 * be animated.
 * 
 * @author Ann Marie Steichmann
 *
 */
public class DisplayView extends ViewPart implements AnimatorListener {

	private Animator animator;
	
	private Action playAction;
	private Action pauseAction;
	private Action stopAction;
	
	private Slider frameSlider;
	private Text frameText;
	
	/**
	 * Constructs a new <code>DisplayView</code>
	 */
	public DisplayView() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {

		Composite control = new Composite( parent, SWT.NONE );
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		control.setLayout( layout );
		Panel panel = new Panel();
		animator = new Animator( panel );
		animator.addListener( this );
		Composite sliderComp = new Composite( control, SWT.NONE );
		GridData data = new GridData( GridData.HORIZONTAL_ALIGN_FILL );
		data.grabExcessHorizontalSpace = true;
		sliderComp.setLayoutData( data );
		GridLayout sliderLayout = new GridLayout();
		sliderLayout.numColumns = 2;
		sliderComp.setLayout( sliderLayout );
		frameSlider = new Slider( sliderComp, SWT.HORIZONTAL );
		frameSlider.setIncrement(1);
		frameSlider.setPageIncrement(5);
		frameSlider.setThumb(1);
		frameSlider.setLayoutData( data );
		frameText = new Text( sliderComp, SWT.BORDER );
		frameText.addKeyListener( new KeyAdapter() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			public void keyReleased(KeyEvent e) {
				
				if ( (int)e.character >= 48 && (int)e.character <= 57 ||
					 (int)e.character == 8 || (int)e.character == 16 ) {
					
					String frame = frameText.getText();
					animator.runUntil( Integer.parseInt( frame ) );
				}
			}
				
		});	
		frameSlider.addSelectionListener( new SelectionListener() {

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected( e );
				
			}

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				
				frameText.setText( Integer.toString( frameSlider.getSelection() ) );
				animator.runUntil( frameSlider.getSelection() );
			}
		
		});
		FigureCanvas canvas = new FigureCanvas( control );
		canvas.setBackground( ColorConstants.white );
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL );
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		canvas.setBorder( new LineBorder() );
		canvas.setLayoutData( data  );
		canvas.setContents( panel );
		createActions();
		createToolbar();
	}
	
	private IFile getAnimationFile() {

		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		if ( editor != null ) {
			IFile file = ((IFileEditorInput)editor.getEditorInput()).getFile();		
			return file;
		}
		return null;
	}
	
	private void createActions() {
		
		playAction = new Action("Play") {
			public void run() {
				if ( !animator.isRunning() ) {
					IFile file = getAnimationFile();
					if ( file != null ) {
						int frameCt = animator.loadAnimation( file );
						frameSlider.setMinimum( 0 );
						frameSlider.setMaximum( frameCt );
						frameSlider.setSelection( 0 );
						animator.run();
					}					
				} else {
					animator.resume();
				}
			}
		};
		playAction.setEnabled( true );
		playAction.setImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "enabled/resume_co.gif" ) );
		playAction.setDisabledImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "disabled/resume_co.gif" ) );
		
		pauseAction = new Action("Pause") {
			public void run() {
				animator.suspend();
			}
		};
		pauseAction.setEnabled( false );
		pauseAction.setImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "enabled/suspend_co.gif" ) );
		pauseAction.setDisabledImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "disabled/suspend_co.gif" ) );
		
		stopAction = new Action("Stop") {
			public void run() {
				animator.terminate();
			}
		};
		stopAction.setEnabled( false );
		stopAction.setImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "enabled/terminate_co.gif" ) );
		stopAction.setDisabledImageDescriptor( Activator.getDefault()
				.getImageDescriptor( "disabled/terminate_co.gif" ) );
	}

    private void createToolbar() {
            IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
            mgr.add(playAction);
            mgr.add(pauseAction);
            mgr.add(stopAction);
    }	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.penguicon.animation.AnimatorListener#onResumed()
	 */
	public void onResumed() {
		
		onStarted();
	}

	/* (non-Javadoc)
	 * @see org.penguicon.animation.AnimatorListener#onStarted()
	 */
	public void onStarted() {
		
		playAction.setEnabled( false );
		pauseAction.setEnabled( true );
		stopAction.setEnabled( true );
	}

	/* (non-Javadoc)
	 * @see org.penguicon.animation.AnimatorListener#onSuspended()
	 */
	public void onSuspended() {
		
		playAction.setEnabled( true );
		pauseAction.setEnabled( false );
		stopAction.setEnabled( true );		
	}

	/* (non-Javadoc)
	 * @see org.penguicon.animation.AnimatorListener#onTerminated()
	 */
	public void onTerminated() {
		
		playAction.setEnabled( true );
		pauseAction.setEnabled( false );
		stopAction.setEnabled( false );
	}
	
	/* (non-Javadoc)
	 * @see org.penguicon.animation.AnimatorListener#onFrameUpdated()
	 */
	public void onFrameUpdated( int currentFrame ) {
		
		frameSlider.setSelection( currentFrame );
		frameText.setText( Integer.toString( currentFrame ) );
		frameText.setSelection( frameText.getText().length() );
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		
		animator.removeListener( this );
		super.dispose();
	}

}
