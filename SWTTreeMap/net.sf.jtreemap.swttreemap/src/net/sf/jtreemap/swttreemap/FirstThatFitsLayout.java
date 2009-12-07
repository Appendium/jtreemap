package net.sf.jtreemap.swttreemap;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Lays out the child figures so that one child figure fills the parent figure
 * and none of the other figures are visible.  The child figure to be shown is
 * the first figure in the order of child figures that has a minimum dimension that
 * allows it to be shown.  The minimum dimension of each child figure is a
 * Dimension object and is set as the constraint.  For a figure to fit, neither dimension
 * may be larger than the dimension of the parent.
 * <P> 
 * If no constraint is set then the figure is assumed to fit.  Normally the last child
 * and no other will have a null constraint.
 */
public class FirstThatFitsLayout extends AbstractLayout 
{
	private Map<IFigure, Dimension> constraints = new HashMap<IFigure, Dimension>();

	/**
	 * Calculates the preferred size of the given Figure.
	 * 
	 * This is the minimum size of the largest (i.e. the first) figure.
	 * 
	 * @param parent the figure whose preferred size is being calculated
	 * @param wHint the width hint
	 * @param hHint the height hint
	 * @return the preferred size
	 * @since 2.0
	 */
	protected Dimension calculatePreferredSize(IFigure parent, int wHint, int hHint) {
		IFigure firstChild = (IFigure)parent.getChildren().iterator().next();
		Dimension result = new Dimension(constraints.get(firstChild));
		if (wHint > result.width) {
			result.width = wHint;
		}
		if (hHint > result.height) {
			result.height = hHint;
		}
		return result;
	}

	/**
	 * @see org.eclipse.draw2d.LayoutManager#getConstraint(org.eclipse.draw2d.IFigure)
	 */
	public Object getConstraint(IFigure child) {
		return constraints.get(child);
	}

	/**
	 * Lays out the given figure's children based on their {@link Locator} constraint.
	 * @param parent the figure whose children should be laid out
	 */
	public void layout(IFigure parent) {
		Rectangle clientArea = parent.getClientArea();
		IFigure bestChild = null;
		for (Object childObject : parent.getChildren()) {
			IFigure child = (IFigure)childObject;
			Dimension minSize = constraints.get(child);
			if (minSize == null
					|| (minSize.width <= clientArea.width
					&& minSize.height <= clientArea.height)) {
				bestChild = child;
				break;
			}
		}
		
		for (Object childObject : parent.getChildren()) {
			IFigure child = (IFigure)childObject;
			if (child == bestChild) {
				child.setBounds(clientArea);
				child.setVisible(true);
			} else {
				child.setVisible(false);
			}
		}
	}

	/**
	 * Removes the locator for the given figure.
	 * @param child the child being removed
	 */
	public void remove(IFigure child) {
		constraints.remove(child);
	}

	/**
	 * Sets the constraint for the given figure.
	 * @param figure the figure whose constraint is being set
	 * @param constraint the new constraint
	 */
	@Override
	public void setConstraint(IFigure figure, Object constraint) {
		super.setConstraint(figure, constraint);
		if (constraint != null) {
			constraints.put(figure, (Dimension)constraint);
		} else {
			constraints.remove(figure);
		}
	}
}
