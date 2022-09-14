/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skyline.plugin;

import org.apache.skyline.commons.constant.CommonConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PluginResourcesResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PluginResourcesResolver.class);

    public List<URL> getResources(ClassLoader classLoader) {
        List<URL> cfgUrlPaths = new ArrayList<URL>();
        Enumeration<URL> urls;
        try {
            urls = classLoader.getResources(CommonConstant.PLUGIN_CONF_FILE_NAME);

            while (urls.hasMoreElements()) {
                URL pluginUrl = urls.nextElement();
                cfgUrlPaths.add(pluginUrl);
                LOGGER.info("find plugin define in {}", pluginUrl);
            }

            return cfgUrlPaths;
        } catch (IOException e) {
            LOGGER.error("read resources failure.", e);
        }
        return null;
    }
}
