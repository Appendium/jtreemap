package net.sf.jtreemap.swttreemap;

import org.eclipse.draw2d.IFigure;

public class TreeMapNode2 {

	private IFigure figure;
	private Double weight;

	public TreeMapNode2(IFigure figure, double weight) {
		this.figure = figure;
		this.weight = weight;
	}

	public IFigure getFigure() {
		return figure;
	}

	public double getWeight() {
		return weight;
	}
}
