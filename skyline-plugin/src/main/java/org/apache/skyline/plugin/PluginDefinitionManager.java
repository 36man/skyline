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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.commons.exception.IllegalPluginDefineException;
import org.apache.skyline.commons.exception.SkylineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lijian
 * @since time: 2022-09-14 10:06
 */
public class PluginDefinitionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginDefinitionManager.class);

    private final Map<String, PluginRawDefinition> pluginDefinitions = new ConcurrentHashMap<>();

    public void load(List<URL> resources) {
        if (pluginDefinitions.size() != 0) {
            pluginDefinitions.clear();
        }
        for (URL input : resources) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input.openStream()))) {
                String pluginDefine;
                while ((pluginDefine = reader.readLine()) != null) {
                    try {
                        if (pluginDefine.trim().length() == 0 || pluginDefine.startsWith("#")) {
                            continue;
                        }
                        PluginRawDefinition pluginRawDefine = PluginRawDefinition.build(pluginDefine);
                        pluginDefinitions.put(pluginRawDefine.getName(), pluginRawDefine);
                    } catch (IllegalPluginDefineException e) {
                        LOGGER.error("Failed to format plugin(" + pluginDefine + ") define.", e);
                    }
                }
            } catch (IOException e) {
                throw new SkylineException(e);
            }
        }
    }

    public String getDefineClass(String name) {
        PluginRawDefinition pluginRawDefinition = pluginDefinitions.get(name);
        if (pluginRawDefinition != null) {
            return pluginRawDefinition.getDefineClass();
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class PluginRawDefinition {
        private String name;
        private String defineClass;

        public static PluginRawDefinition build(String define) throws IllegalPluginDefineException {
            if (StringUtils.isEmpty(define)) {
                throw new IllegalPluginDefineException(define);
            }

            String[] pluginDefine = define.split("=");
            if (pluginDefine.length != 2) {
                throw new IllegalPluginDefineException(define);
            }

            String pluginName = pluginDefine[0];
            String defineClass = pluginDefine[1];
            return new PluginRawDefinition(pluginName, defineClass);
        }
    }
}
