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

import lombok.EqualsAndHashCode;

/**
 * Class who permits to associate a double value to a label
 *
 * @author Laurent DUTHEIL
 */
@EqualsAndHashCode
public abstract class Value implements Comparable, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * get the double value.
     *
     * @return the double value
     */
    public abstract double getValue();

    /**
     * get the formatedValue.
     *
     * @return the label of the value
     */
    public abstract String getLabel();

    /**
     * set the double value.
     *
     * @param value
     *            the new double value
     */
    public abstract void setValue(double value);

    /**
     * set the new label.
     *
     * @param newLabel
     *            the new label
     */
    public abstract void setLabel(String newLabel);

    @Override
    public int compareTo(final Object value) {
        if (value instanceof Value) {
            final Value value2 = (Value) value;
            if (this.getValue() < value2.getValue()) {
                return -1;
            }
            return this.getValue() > value2.getValue() ? 1 : 0;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(getLabel()).append(" - ").append(getValue());
        return b.toString();
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
