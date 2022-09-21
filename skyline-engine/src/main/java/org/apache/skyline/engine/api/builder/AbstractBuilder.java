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
package org.apache.skyline.engine.api.builder;

import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.config.SkylineProperties;
import org.apache.skyline.engine.loader.SkylineClassLoader;
import org.apache.skyline.model.Api;
import org.apache.skyline.model.ApiCluster;
import org.apache.skyline.model.ApiGroup;
import org.apache.skyline.model.predicate.AsyncPredicate;
import org.apache.skyline.model.support.Buildable;
import org.apache.skyline.plugin.PluginBootstrap;
import org.apache.skyline.plugin.PluginDefinition;
import org.apache.skyline.plugin.PluginManager;
import org.apache.skyline.plugin.SkylinePluginWrapper;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lijian
 * @since time: 2022-09-14 11:38
 */
public abstract class AbstractBuilder<B extends AbstractBuilder<B>> implements Buildable<Api> {

    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractBuilder.class);
    private int id;

    private String identity;

    private String rawPath;

    private int order;

    private int version;

    private String name;

    private String memo;

    private ApiGroup apiGroup;

    private ApiCluster apiCluster;

    private Date createTime;

    private Date updateTime;

    protected Map<String, Object> metadata = new HashMap<>();

    private List<PluginDefinition> pluginDefinitions = new ArrayList<>();

    protected AbstractBuilder() {
    }

    protected abstract B getThis();

    public B id(int id) {
        this.id = id;
        return getThis();
    }

    public B order(int order) {
        this.order = order;
        return getThis();
    }

    public B rawPath(String rawPath) {
        this.rawPath = rawPath;
        return getThis();
    }

    public B version(int version) {
        this.version = version;
        return getThis();
    }

    public B name(String name) {
        this.name = name;
        return getThis();
    }

    public B memo(String memo) {
        this.memo = memo;
        return getThis();
    }

    public B apiGroup(ApiGroup apiGroup) {
        this.apiGroup = apiGroup;
        return getThis();
    }

    public B apiCluster(ApiCluster apiCluster) {
        this.apiCluster = apiCluster;
        return getThis();
    }

    public B createTime(Date createTime) {
        this.createTime = createTime;
        return getThis();
    }

    public B updateTime(Date updateTime) {
        this.updateTime = updateTime;
        return getThis();
    }


    public B replaceMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return getThis();
    }

    public B pluginDefinitions(List<PluginDefinition> pluginDefinitions) {
        this.pluginDefinitions = pluginDefinitions;
        return getThis();
    }

    public B metadata(Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
        return getThis();
    }

    public B metadata(String key, Object value) {
        this.metadata.put(key, value);
        return getThis();
    }

    public abstract AsyncPredicate<ServerWebExchange> getPredicate();

    public Api build(Object... params) {
        if (!(params != null && params.length == 2 && params[0]
                instanceof PluginManager && params[1] instanceof SkylineProperties)) {
            throw new IllegalArgumentException("api build params' number must be one and type must be pluginManager");
        }
        PluginManager pluginManager = (PluginManager) params[0];
        SkylineProperties skylineProperties = (SkylineProperties) params[1];
        if (this.apiCluster == null) {
            throw new SkylineException("api cluster is null");
        }
        if (this.apiGroup == null) {
            throw new SkylineException("api group is null");
        }
        Api api = new Api();
        api.setId(this.id);
        api.setName(this.name);
        api.setMemo(this.memo);
        api.setApiCluster(this.apiCluster);
        api.setApiGroup(this.apiGroup);
        api.setMetadata(this.metadata);
        if (this.createTime == null) {
            api.setCreateTime(new Date());
        }
        if (this.updateTime == null) {
            api.setUpdateTime(new Date());
        }
        api.setIdentification(this.id + "_" + this.apiCluster.getId() + "_" + this.apiGroup.getId());
        api.setPredicate(getPredicate());
        api.setPluginWrappers(convertPlugins(this.pluginDefinitions, pluginManager, skylineProperties));
        return api;
    }

    private List<SkylinePluginWrapper<?>> convertPlugins(List<PluginDefinition> pluginDefinitions,
                                                      PluginManager pluginManager, SkylineProperties skylineProperties) {
        return pluginDefinitions.stream().map(
                pluginDefinition -> pluginManager.getPlugins().computeIfAbsent(pluginDefinition.getDefineClass(),
                        s -> {
                            try {
                                LOGGER.debug("loading plugin class {}.", pluginDefinition.getDefineClass());
                                SkylinePlugin<?> plugin = (SkylinePlugin<?>) Class.forName(
                                        pluginDefinition.getDefineClass(), true,
                                        new SkylineClassLoader(PluginBootstrap.class.getClassLoader(), skylineProperties.getPluginPath())).getConstructor().newInstance();
                                return new SkylinePluginWrapper<>(plugin, pluginDefinition.getConfig());
                            } catch (Throwable t) {
                                LOGGER.error("load plugin [" + pluginDefinition.getDefineClass() + "] failure.", t);
                                throw new SkylineException(t);
                            }
                })
        ).collect(Collectors.toList());
    }
}
