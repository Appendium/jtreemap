package net.sf.jtreemap.swttreemap;


import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * This figure has two child figures, one or other of which is rendered to
 * fill this figure.  One is displayed when this figure
 * exceeds a certain size, and the other is displayed when this figure is
 * smaller than a certain size.
 * 
 * @author Nigel Westbury
 */
public class ExpandableFigure extends Figure {

	public ExpandableFigure(IFigure collapsedFigure, IFigure expandedFigure) {
		setLayoutManager(new FirstThatFitsLayout());
		setOpaque(true);

		add(expandedFigure, new Dimension(100, 100));
		add(collapsedFigure);
	}
}
