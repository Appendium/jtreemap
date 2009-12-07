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

import net.sf.jtreemap.swttreemap.ITreeMapColorProvider;
import net.sf.jtreemap.swttreemap.ITreeMapProvider;
import net.sf.jtreemap.swttreemap.KTreeMap;
import net.sf.jtreemap.swttreemap.SplitByNumber;
import net.sf.jtreemap.swttreemap.SplitBySlice;
import net.sf.jtreemap.swttreemap.SplitBySortedWeight;
import net.sf.jtreemap.swttreemap.SplitByWeight;
import net.sf.jtreemap.swttreemap.SplitSquarified;
import net.sf.jtreemap.swttreemap.SplitStrategy;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
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

public class KTreeMapView extends ViewPart {
//  private static final String ID_BUNDLE = "net.sf.jtreemap.swttreemap";
  private static final String ID_BUNDLE = "net.sf.jmoney.treemap";

  private TreeViewer viewer;
  private DrillDownAdapter drillDownAdapter;
  private Action openXmlAction;
  private Action openTM3Action;
  private Action selectionChangedAction;
  private KTreeMap kTreeMap;
  private LinkedHashMap<String, SplitStrategy> strategies = new LinkedHashMap<String, SplitStrategy>();
  private LinkedHashMap<String, ITreeMapColorProvider> colorProviders = new LinkedHashMap<String, ITreeMapColorProvider>();
  private Combo cmbStrategy;
  private Combo cmbColorProvider;
  private Combo cmbTM3Weight;
  private Combo cmbTM3Value;
  private Composite legend;
  private XMLTreeMapProvider xmlProvider;
  private TM3TreeMapProvider tm3Provider;
  private BuilderTM3 builderTM3;
  private Group grpTM3Params;

  /*
   * The content provider class is responsible for providing objects to the
   * view. It can wrap existing objects in adapters or simply return objects
   * as-is. These objects may be sensitive to the current input of the view, or
   * ignore it and always show the same content (like Task List, for example).
   */

  /**
   * This is a callback that will allow us to create the viewer and initialize
   * it.
   */
  @Override
  public void createPartControl(Composite parent) {
    SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
    sash.setLayout(new FillLayout());
    sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    TreeMapNode root = null;
    try {
      root = getDefaultRoot();
    } catch (ParseException e) {
      MessageDialog.openError(getSite().getShell(), "Parse Error", e.getMessage());
      e.printStackTrace();
    }

    createLeftComp(sash);
    kTreeMap = new KTreeMap(sash, SWT.NONE, root);
	kTreeMap.setTreeMapProvider(xmlProvider);

	// The following requires the tree map provider to have been set!!!
	
	// NEW, was not here.
    // Set a default color provider now.  This allows the figures to be
    // created without the need to defer coloring.
    ITreeMapColorProvider cp = new HSBTreeMapColorProvider(kTreeMap,
        HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED));
    kTreeMap.setColorProvider(cp);
    
    kTreeMap.createFigures();
    


	
	
	createRightComp(sash);

    viewer.setInput(root);

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
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus() {
    viewer.getControl().setFocus();
  }

  void updateColorProvider(Composite comp) {
    String key = this.cmbColorProvider.getText();
    ITreeMapColorProvider cp = this.colorProviders.get(key);
    this.kTreeMap.setColorProvider(cp);
    if (legend != null) {
      legend.dispose();
    }
    legend = cp.getLegend(comp, SWT.NONE);
    legend.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
        | GridData.VERTICAL_ALIGN_BEGINNING));
    comp.layout();
  }

  void updateStrategy() {
    String key = this.cmbStrategy.getText();
    SplitStrategy strat = this.strategies.get(key);
    this.kTreeMap.setStrategy(strat);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void createColorProviders(Composite comp) {
    createColorProviders();

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
        updateColorProvider(grp);
      }
    });

    // colorProvider choice :
    updateColorProvider(grp);
  }

  private void createColorProviders() {
    this.colorProviders.put("HSB linear", new HSBTreeMapColorProvider(kTreeMap,
        HSBTreeMapColorProvider.ColorDistributionTypes.Linear, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB log", new HSBTreeMapColorProvider(kTreeMap,
        HSBTreeMapColorProvider.ColorDistributionTypes.Log, Display
            .getDefault().getSystemColor(SWT.COLOR_GREEN), Display.getDefault()
            .getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB SquareRoot", new HSBTreeMapColorProvider(
        kTreeMap, HSBTreeMapColorProvider.ColorDistributionTypes.SquareRoot,
        Display.getDefault().getSystemColor(SWT.COLOR_GREEN), Display
            .getDefault().getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB CubicRoot", new HSBTreeMapColorProvider(
        kTreeMap, HSBTreeMapColorProvider.ColorDistributionTypes.CubicRoot,
        Display.getDefault().getSystemColor(SWT.COLOR_GREEN), Display
            .getDefault().getSystemColor(SWT.COLOR_RED)));
    this.colorProviders.put("HSB exp", new HSBTreeMapColorProvider(kTreeMap,
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

    viewer = new TreeViewer(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    drillDownAdapter = new DrillDownAdapter(viewer);
    viewer.setContentProvider(new ViewContentProvider());
    viewer.setLabelProvider(new ViewLabelProvider());
    viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
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
        TM3TreeMapProvider.setValueField(field);
        createColorProviders();
        updateColorProvider(cmbColorProvider.getParent());
        kTreeMap.redraw();
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

    xmlProvider = new XMLTreeMapProvider();
    tm3Provider = new TM3TreeMapProvider();

    return builder.getRoot();
  }

  private void hookSelectionChangedAction() {
    viewer.addSelectionChangedListener(new ISelectionChangedListener() {
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
        ISelection selection = viewer.getSelection();
        Object obj = ((IStructuredSelection)selection).getFirstElement();
        if (obj instanceof TreeMapNode) {
          TreeMapNode dest = ((TreeMapNode)obj).getParent();
          if (dest == null) {
            return;
          }

          kTreeMap.setContents(dest.getFigure());
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

  class ViewLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object obj) {
      return null;
    }

    @Override
    public String getText(Object obj) {
      if (obj instanceof TreeMapNode) {
        TreeMapNode node = (TreeMapNode)obj;
        ITreeMapProvider provider = KTreeMapView.this.kTreeMap
            .getTreeMapProvider();
        return provider.getLabel(node);
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
        TreeMapNode root = builder.getRoot();
        kTreeMap.setTreeMapProvider(xmlProvider);
        kTreeMap.setRoot(root);
        viewer.setInput(root);
        createColorProviders();
        updateColorProvider(cmbColorProvider.getParent());
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
        kTreeMap.setTreeMapProvider(tm3Provider);
        kTreeMap.setRoot(root);
        viewer.setInput(root);
        createColorProviders();
        updateColorProvider(cmbColorProvider.getParent());
        // add tm3 fields
        setTM3Fields();
        grpTM3Params.setVisible(true);
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
