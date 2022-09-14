/**
 * bravo.org
 * Copyright (c) 2018-2019 ALL Rights Reserved
 */
package org.apache.skyline.model.support;

/**
 * @author lijian
 * @since time: 2022-09-05 16:36
 */
public interface Configurable<C> {

    Class<C> getConfigClass();

    C newConfig();
}
