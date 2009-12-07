/*
 * ObjectLab, http://www.objectlab.co.uk/open is supporting JTreeMap.
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: KTreeMapView.java 75 2006-10-24 23:00:51Z benoitx $
 * 
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sf.jtreemap.swtdemo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.LinkedHashMap;

import net.sf.jtreemap.swttreemap.SplitByNumber;
import net.sf.jtreemap.swttreemap.SplitBySlice;
import net.sf.jtreemap.swttreemap.SplitBySortedWeight;
import net.sf.jtreemap.swttreemap.SplitByWeight;
import net.sf.jtreemap.swttreemap.SplitSquarified;
import net.sf.jtreemap.swttreemap.SplitStrategy;
import net.sf.jtreemap.swttreemap.TreemapViewer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class DemoView extends ViewPart {
  private static final String ID_BUNDLE = "net.sf.jtreemap.swtdemo";

  private TreeViewer nodeTreeViewer;
  private DrillDownAdapter drillDownAdapter;
  private Action openXmlAction;
  private Action openTM3Action;
  private Action selectionChangedAction;
  private LinkedHashMap<String, SplitStrategy> strategies = new LinkedHashMap<String, SplitStrategy>();
  private LinkedHashMap<String, HSBTreeMapColorProvider> colorProviders = new LinkedHashMap<String, HSBTreeMapColorProvider>();
  private Combo cmbStrategy;
  private Combo cmbColorProvider;
  private Combo cmbTM3Weight;
  private Combo cmbTM3Value;
  private Legend legend;
  private BuilderTM3 builderTM3;
  private Group grpTM3Params;

private TreemapViewer treemapViewer;

public DemoView() {
createColorProviders();
}

  /*
   * The content provider class is responsible for providing objects to the
   * view. It can wrap existing objects in adapters or simply return objects
   * as-is. These objects may be sensitive to the current input of the view, or
   * ignore it and always show the same content (like Task List, for example).
   */

  /**
   * This is a callback that will allow us to create the nodeTreeViewer and initialize
   * it.
   */
  @Override
  public void createPartControl(Composite parent) {
    SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
    sash.setLayout(new FillLayout());
    sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    createLeftComp(sash);

    treemapViewer = new TreemapViewer(sash);
	
	treemapViewer.setContentProvider(new DemoTreemapContentProvider());
	treemapViewer.setLabelProvider(new DemoTreemapLabelProvider(treemapViewer));
	treemapViewer.setColorProvider(new DemoTreemapColorProvider(colorProviders.get("HSB linear")));
	
	// The following requires the tree map provider to have been set!!!
	
	// NEW, was not here.
    // Set a default color provider now.  This allows the figures to be
    // created without the need to defer coloring.
//    ITreeMapColorProvider cp = new HSBTreeMapColorProvider(kTreeMap,
//        HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Display
//            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
//            .getSystemColor(SWT.COLOR_RED));
//    kTreeMap.setColorProvider(cp);
//    
//    kTreeMap.createFigures();
    


	
	
	createRightComp(sash);

    TreeMapNode root = null;
    try {
      root = getDefaultRoot();
    } catch (ParseException e) {
      MessageDialog.openError(getSite().getShell(), "Parse Error", e.getMessage());
      e.printStackTrace();
    }

    nodeTreeViewer.setInput(root);
    treemapViewer.setInput(root);

    sash.setWeights(new int[] { 15, 70, 15 });

    makeActions();
//    hookContextMenu();
    hookSelectionChangedAction();
    contributeToActionBars();
    
//    kTreeMap.init();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.eclipse.ui.part.WorkbenchPart#dispose()
   */
  @Override
  public void dispose() {
    super.dispose();
    // to dispose the color and the others resources :
    ResourceManager.dispose();
  }

  /**
   * Passing the focus request to the nodeTreeViewer's control.
   */
  @Override
  public void setFocus() {
    nodeTreeViewer.getControl().setFocus();
  }

  void updateColorProvider() {
    String key = this.cmbColorProvider.getText();
    HSBTreeMapColorProvider cp = this.colorProviders.get(key);
    
    treemapViewer.setColorProvider(new DemoTreemapColorProvider(cp));
//    treemapViewer.refresh();
    treemapViewer.getControl().redraw();
    
    legend.setColorProvider(cp);
  }

  void updateStrategy() {
    String key = this.cmbStrategy.getText();
    SplitStrategy strategy = this.strategies.get(key);
    
    treemapViewer.setSplitStrategy(strategy);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void createColorProviders(Composite comp) {
//    createColorProviders();

    final Group grp = new Group(comp, SWT.NONE);
    grp.setText("Color Provider");
    grp.setLayout(new GridLayout());
    grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));

    this.cmbColorProvider = new Combo(grp, SWT.NONE);
    this.cmbColorProvider.removeAll();
    for (String key : this.colorProviders.keySet()) {
      this.cmbColorProvider.add(key);
    }
    this.cmbColorProvider.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));

    this.cmbColorProvider.select(0);

    this.cmbColorProvider.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      // ignore
      }

      public void widgetSelected(SelectionEvent e) {
        updateColorProvider();
      }
    });

    // colorProvider choice :
    legend = new Legend(grp, SWT.NONE);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    gridData.widthHint = 100;
    gridData.heightHint = 100;
	legend.setLayoutData(gridData);

    updateColorProvider();
  }

  private void createColorProviders() {
    this.colorProviders.put("HSB linear", new HSBTreeMapColorProvider(
        HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB log", new HSBTreeMapColorProvider(
        HSBTreeMapColorProvider.ColorDistributionTypes.Log, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB SquareRoot", new HSBTreeMapColorProvider(
        HSBTreeMapColorProvider.ColorDistributionTypes.SquareRoot,
        Display.getDefault().getSystemColor(SWT.COLOR_GREEN), Display
            .getDefault().getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB CubicRoot", new HSBTreeMapColorProvider(
        HSBTreeMapColorProvider.ColorDistributionTypes.CubicRoot,
        Display.getDefault().getSystemColor(SWT.COLOR_GREEN), Display
            .getDefault().getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB exp", new HSBTreeMapColorProvider(
        HSBTreeMapColorProvider.ColorDistributionTypes.Exp, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED)));
  }

  private void createLeftComp(SashForm sash) {
    Composite comp = new Composite(sash, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 2;
    layout.marginWidth = 2;
    comp.setLayout(layout);

    nodeTreeViewer = new TreeViewer(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    drillDownAdapter = new DrillDownAdapter(nodeTreeViewer);
    nodeTreeViewer.setContentProvider(new ViewContentProvider());
    nodeTreeViewer.setLabelProvider(new ViewLabelProvider());
    nodeTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
  }

  private void createRightComp(SashForm sash) {
    Composite comp = new Composite(sash, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginHeight = 2;
    layout.marginWidth = 2;
    comp.setLayout(layout);

    createStrategies(comp);
    createColorProviders(comp);
    createTM3Params(comp);
  }

  private void createTM3Params(Composite comp) {
    grpTM3Params = new Group(comp, SWT.NONE);
    grpTM3Params.setText("TM3 params");
    grpTM3Params.setLayout(new GridLayout());
    grpTM3Params.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));
    grpTM3Params.setVisible(false);

    this.cmbTM3Value = new Combo(grpTM3Params, SWT.NONE);
    this.cmbTM3Value.setToolTipText("Select the value field");
    this.cmbTM3Value.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));
    this.cmbTM3Value.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      // ignore
      }

      public void widgetSelected(SelectionEvent e) {
        Combo cmb = (Combo)e.getSource();
        String field = cmb.getText();
//        TM3TreeMapProvider.setValueField(field);
//        createColorProviders();
//        updateColorProvider(cmbColorProvider.getParent());
//        kTreeMap.redraw();
      }

    });

    this.cmbTM3Weight = new Combo(grpTM3Params, SWT.NONE);
    this.cmbTM3Weight.setToolTipText("Select the weight field");
    this.cmbTM3Weight.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));
    this.cmbTM3Weight.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      // ignore
      }

      public void widgetSelected(SelectionEvent e) {
        Combo cmb = (Combo)e.getSource();
        String field = cmb.getText();
        BuilderTM3.setFieldWeight(field);
        builderTM3.setWeights();
        // TODO
//        kTreeMap.calculatePositions();
//        kTreeMap.redraw();
      }

    });

  }

  private void createStrategies(Composite comp) {
    this.strategies.put("Squarified", new SplitSquarified());
    this.strategies.put("Sorted Weight", new SplitBySortedWeight());
    this.strategies.put("Weight", new SplitByWeight());
    this.strategies.put("Slice", new SplitBySlice());
    this.strategies.put("Equal Weight", new SplitByNumber());

    Group grp = new Group(comp, SWT.NONE);
    grp.setText("Strategy");
    grp.setLayout(new GridLayout());
    grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));

    this.cmbStrategy = new Combo(grp, SWT.NONE);
    this.cmbStrategy.removeAll();
    for (String key : this.strategies.keySet()) {
      this.cmbStrategy.add(key);
    }
    this.cmbStrategy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));

    this.cmbStrategy.select(0);

    this.cmbStrategy.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      // ignore
      }

      public void widgetSelected(SelectionEvent e) {
        updateStrategy();
      }
    });

    // Strategy choice :
    updateStrategy();
  }

  private void fillLocalPullDown(IMenuManager manager) {
    manager.add(openXmlAction);
    manager.add(openTM3Action);
  }

  private void fillLocalToolBar(IToolBarManager manager) {
    manager.add(openXmlAction);
    manager.add(openTM3Action);
    manager.add(new Separator());
    drillDownAdapter.addNavigationActions(manager);
  }

  private TreeMapNode getDefaultRoot() throws ParseException {
    URL url = FileLocator.find(Platform.getBundle(ID_BUNDLE), new Path(
        "/TreeMap.xml"), null);
    try {
      url = FileLocator.toFileURL(url);
    } catch (IOException e) {
      MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
          .getActiveShell(), "Error", e.getMessage());
      e.printStackTrace();
    }
    BuilderXML builder = new BuilderXML(new File(url.getPath()));

    for (HSBTreeMapColorProvider provider : colorProviders.values()) {
    	provider.setRange(builder.getMinValue(), builder.getMaxValue());
    }

    legend.setRange(builder.getMinValue(), builder.getMaxValue());
    
    return builder.getRoot();
  }

  private void hookSelectionChangedAction() {
    nodeTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      public void selectionChanged(SelectionChangedEvent event) {
        selectionChangedAction.run();
      }
    });
  }

  private void setTM3Fields() {
    String[] numberFields = TM3Bean.getNumberFields();
    String[] cmbValues = new String[numberFields.length + 1];
    cmbValues[0] = "";
    for (int i = 1; i < cmbValues.length; i++) {
      cmbValues[i] = numberFields[i - 1];
    }
    this.cmbTM3Weight.removeAll();
    this.cmbTM3Value.removeAll();
    for (int i = 0; i < cmbValues.length; i++) {
      String item = cmbValues[i];
      this.cmbTM3Weight.add(item);
      this.cmbTM3Value.add(item);
    }

  }

  private void makeActions() {
    openXmlAction = new OpenXMLAction();
    openTM3Action = new OpenTM3Action();

    selectionChangedAction = new Action() {
      @Override
      public void run() {
        ISelection selection = nodeTreeViewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        if (obj instanceof TreeMapNode) {
          TreeMapNode dest = ((TreeMapNode)obj).getParent();
          if (dest == null) {
            return;
          }

//          kTreeMap.setContents(dest.getFigure());
        }
      }
    };
  }

  class ViewContentProvider implements IStructuredContentProvider,
      ITreeContentProvider {

    public void dispose() {/*ignore*/}

    public Object[] getChildren(Object parent) {
      if (parent instanceof TreeMapNode) {
        return ((TreeMapNode)parent).getChildren().toArray();
      }
      return new Object[0];
    }

    public Object[] getElements(Object parent) {
      return getChildren(parent);
    }

    public Object getParent(Object child) {
      if (child instanceof TreeMapNode) {
        return ((TreeMapNode)child).getParent();
      }
      return null;
    }

    public boolean hasChildren(Object parent) {
      if (parent instanceof TreeMapNode)
        return !((TreeMapNode)parent).isLeaf();
      return false;
    }

    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
      /*ignore*/
    }
  }

  /**
   * This label provider is used in the tree control in the leftmost pane.
   */
  class ViewLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object obj) {
      return null;
    }

    @Override
    public String getText(Object obj) {
      if (obj instanceof TreeMapNode) {
        TreeMapNode node = (TreeMapNode)obj;
        return node.getLabel();
      }
      return obj.toString();
    }
  }

  private class OpenXMLAction extends Action {
    private final String[] EXTENTIONS = new String[] { "*.xml" };

    /**
     * Constructor
     */
    public OpenXMLAction() {
      super("Open XML File");
      setToolTipText(getText());
      setId(getText());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
      return AbstractUIPlugin.imageDescriptorFromPlugin(ID_BUNDLE,
          "icons/XMLFile.gif");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
      Display display = PlatformUI.getWorkbench().getDisplay();
      final FileDialog dialog = new FileDialog(display.getActiveShell(),
          SWT.OPEN);
      dialog.setFilterExtensions(EXTENTIONS);
      String path = dialog.open();

      if (path != null) {
        BuilderXML builder;
        try {
          builder = new BuilderXML(new File(path));
        } catch (ParseException e) {
          MessageDialog.openError(display.getActiveShell(), "Parse error", e
              .getMessage());
          return;
        }
        
        legend.setRange(builder.getMinValue(), builder.getMaxValue());
        
        TreeMapNode root = builder.getRoot();
        nodeTreeViewer.setInput(root);
        updateColorProvider();
        grpTM3Params.setVisible(false);

      }
    }

  }

  private class OpenTM3Action extends Action {
    private final String[] EXTENTIONS = new String[] { "*.tm3" };

    /**
     * Constructor
     */
    public OpenTM3Action() {
      super("Open TM3 File");
      setToolTipText(getText());
      setId(getText());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
      return AbstractUIPlugin.imageDescriptorFromPlugin(ID_BUNDLE,
          "icons/TM3File.gif");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
      Display display = PlatformUI.getWorkbench().getDisplay();
      final FileDialog dialog = new FileDialog(display.getActiveShell(),
          SWT.OPEN);
      dialog.setFilterExtensions(EXTENTIONS);
      String path = dialog.open();

      if (path != null) {
        try {
          builderTM3 = new BuilderTM3(new File(path));
        } catch (IOException e) {
          MessageDialog.openError(display.getActiveShell(), "Parse error", e
              .getMessage());
          return;
        }
        TreeMapNode root = builderTM3.getRoot();
        nodeTreeViewer.setInput(root);
        treemapViewer.setInput(root);
        
//        for (HSBTreeMapColorProvider provider : colorProviders.values()) {
//        	provider.setRange(builderTM3.getMinValue(), builderTM3.getMaxValue());
//        }
//
//        legend.setRange(builderTM3.getMinValue(), builderTM3.getMaxValue());
//
//        updateColorProvider();
        // add tm3 fields
        setTM3Fields();
        grpTM3Params.setVisible(true);
      }
    }

  }
  private class Legend extends Canvas {
		private HSBTreeMapColorProvider provider;
		private double minValue;
		private double maxValue;

	    /**
	     * Constructor
	     * @param parent parent Composite
	     * @param style style
	     */
	    public Legend(Composite parent, int style) {
	      super(parent, style);

	      addPaintListener(new PaintListener() {
	        public void paintControl(PaintEvent e) {
	          Legend.this.paintControl(e);
	        }
	      });
	    }

	    void setColorProvider(HSBTreeMapColorProvider provider) {
	    	this.provider = provider;
	    	this.redraw();
	    }
	    
	    void setRange(double minValue, double maxValue) {
	    	this.minValue = minValue;
	    	this.maxValue = maxValue;
	    	this.redraw();
	    }
	    
//	    @Override
//	    public Point computeSize(int wHint, int hHint, boolean changed) {
//	      int height = 20;
//	      if (hHint != SWT.DEFAULT)
//	        height = hHint;
//	      return new Point(wHint, height);
//	    }

	    protected void paintControl(PaintEvent e) {
	      GC gc = e.gc;
	      int width = this.getBounds().width;
	      double step = (maxValue - minValue) / width;
	      double value = minValue;
	      for (int i = 0; i < width; i++) {
	    	// TODO
	    	  if (provider != null) {
	        gc.setBackground(provider.getBackground(value));
	    	  }
	        gc.fillRectangle(i, 0, 1, this.getBounds().height);
	        value += step;
	      }
	    }
	  }
}
/*
 *                 ObjectLab is supporing JTreeMap
 * 
 * Based in London, we are world leaders in the design and development 
 * of bespoke applications for the securities financing markets.
 * 
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more about us</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 */
