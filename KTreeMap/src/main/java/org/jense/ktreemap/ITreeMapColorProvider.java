package org.jense.ktreemap;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * IColorProvider for a KTreeMap
 */
public interface ITreeMapColorProvider extends IColorProvider {
  /**
   * Return a Composite for the color legend of a KTreeMap
   * @param parent parent Composite
   * @param style style
   * @return a Composite for the color legend of a KTreeMap
   */
  public Composite getLegend(Composite parent, int style);
}
