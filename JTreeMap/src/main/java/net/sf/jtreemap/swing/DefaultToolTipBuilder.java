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
 * $Id$
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
package net.sf.jtreemap.swing;

import java.io.Serializable;

import javax.swing.JToolTip;

/**
 * Default class to build the DefaultToolTip displayed by the JTreeMap.<BR>
 * 
 * @see net.sf.jtreemap.swing.DefaultToolTip
 * @author Laurent DUTHEIL
 * 
 */
public class DefaultToolTipBuilder implements IToolTipBuilder, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -1646772942428571187L;

    private static JToolTip instance = null;

    private JTreeMap jTreeMap;

    private String weightPrefix;
    
    private String valuePrefix;
    
    private boolean showWeight;

    /**
     * Constructor.
     * 
     * @param jTreeMap
     *            the linked JTreeMap
     */
    public DefaultToolTipBuilder(final JTreeMap jTreeMap, final String weightPrefix, final String valuePrefix, final boolean showWeight) {
        this.jTreeMap = jTreeMap;
        this.weightPrefix = weightPrefix;
        this.valuePrefix = valuePrefix;
        this.showWeight = showWeight;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.jtreemap.swing.IToolTipBuilder#getToolTip()
     */
    public JToolTip getToolTip() {
        if (instance == null) {
            instance = new DefaultToolTip(this.jTreeMap, this.weightPrefix, 
                    this.valuePrefix, this.showWeight);
        }
        return instance;
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
