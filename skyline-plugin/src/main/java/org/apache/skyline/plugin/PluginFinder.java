/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.skyline.plugin;

import org.apache.skyline.plugin.api.SkylinePlugin;

import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-13 15:32
 */
public class PluginFinder {

    private final Map<String, SkylinePlugin> plugins;

    public PluginFinder(Map<String, SkylinePlugin> plugins) {
        if (plugins == null) {
            throw new NullPointerException("plugins is null");
        }
        this.plugins = plugins;
    }

    public SkylinePlugin find(String pluginName) {
        return plugins.get(pluginName);
    }
}
