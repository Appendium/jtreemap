package net.sf.jtreemap.swttreemap;


import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * The 'viewer' for a treemap control.
 * <P>
 * This viewer uses the JFace conventions.  Note that there is no class that
 * represents a treemap control because a treemap control is nothing more
 * than a Canvas with a Draw2d layout to layout the rectangles each of which
 * are Draw2d figures.
 */
public class TreemapViewer extends ContentViewer {

	/**
	 * This viewer's control.
	 */
	private Canvas canvas;

	private LightweightSystem lws;

	private IColorProvider colorProvider;

	private SplitStrategy strategy = new SplitSquarified();
	
	/**
	 * Creates a treemap viewer on a newly-created canvas control under the
	 * given parent. The canvas control is created using the SWT style bit
	 * <code>BORDER</code>. The viewer has no input, no content provider, a
	 * default label provider, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public TreemapViewer(Composite parent) {
		this(parent, SWT.BORDER);
	}

	/**
	 * Creates a treemap viewer on a newly-created canvas control under the
	 * given parent. The tree control is created using the given SWT style bits.
	 * The viewer has no input, no content provider, a default label provider,
	 * and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            the SWT style bits used to create the tree.
	 */
	public TreemapViewer(Composite parent, int style) {
		this(new Canvas(parent, style));
	}

	/**
	 * Creates a treemap viewer on the given canvas control. The viewer has no input,
	 * no content provider, a default label provider, and no filters.
	 *
	 * @param tree
	 *            the canvas control
	 */
	// TODO How is this useful if we supply the lws?
	public TreemapViewer(Canvas canvas) {
		super();
		this.canvas = canvas;
		lws = new LightweightSystem(canvas);
//		hookControl(canvas);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setContentProvider(org.eclipse.jface.viewers.IContentProvider)
	 */
	public void setContentProvider(ITreeContentProvider provider) {
		super.setContentProvider(provider);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.AbstractTreeViewer#setContentProvider(org.eclipse.jface.viewers.IContentProvider)
	 */
	public void setLabelProvider(ITreemapLabelProvider provider) {
		super.setLabelProvider(provider);
	}

	@Override
	public Control getControl() {
		return canvas;
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc) Method declared on <code>Viewer</code>. Builds the initial
	 * tree.
	 */
	@Override
	protected void inputChanged(Object input, Object oldInput) {
		canvas.setRedraw(false);
		try {
			ITreemapLabelProvider labelProvider = (ITreemapLabelProvider)getLabelProvider();
			Border rootBorder = labelProvider.getBorder(input);
			IFigure expandedFigure = new TreemapFigure(this, input);
			expandedFigure.setBorder(rootBorder);

			IFigure collapsedFigure = labelProvider.getFigure(input);
			
			IFigure rootFigure = new ExpandableFigure(collapsedFigure, expandedFigure);

			lws.setContents(rootFigure);
		} finally {
			canvas.setRedraw(true);
		}
	}

	public void setColorProvider(IColorProvider colorProvider) {
		this.colorProvider = colorProvider;
		
		canvas.redraw();
	}

	public IColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setSplitStrategy(SplitStrategy strategy) {
		this.strategy = strategy;

		/*
		 * The layout strategy can only be changed by re-creating the figures from
		 * the input.  One way to do that is to reset the input.  This works because
		 * this class does not check in the <code>inputChanged</code> method to see
		 * if the input really has changed.
		 */
		if (getInput() != null) {
			setInput(getInput());
		}
	}

	public SplitStrategy getSplitStrategy() {
		return strategy;
	}
}
