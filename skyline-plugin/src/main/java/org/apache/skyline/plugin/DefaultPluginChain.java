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

import lombok.Getter;
import lombok.Setter;
import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.commons.utils.CastUtils;
import org.apache.skyline.commons.utils.JsonUtils;
import org.apache.skyline.plugin.api.DefaultCapableSwitchManager;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.apache.skyline.plugin.api.SkylinePluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lijian
 * @since time: 2022-09-09 17:23
 */
public class DefaultPluginChain implements SkylinePluginChain {

    private final int index;

    private final List<SkylinePluginWrapper<?>> pluginWrappers;

    @Setter
    @Getter
    private Object config;

    public DefaultPluginChain(List<SkylinePluginWrapper<?>> pluginWrappers) {
        this.pluginWrappers = pluginWrappers;
        this.index = 0;
    }

    public DefaultPluginChain(DefaultPluginChain parent, int index) {
        this.pluginWrappers = parent.getPluginWrappers();
        this.index = index;
    }

    public List<SkylinePluginWrapper<?>> getPluginWrappers() {
        return pluginWrappers;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        if (this.index < pluginWrappers.size()) {
            SkylinePluginWrapper<?> pluginWrapper = pluginWrappers.get(this.index);
            if (pluginWrapper.getSkylinePlugin() == null) {
                throw new SkylineException("pluginWrapper don't have skyline plugin instance");
            }
            SkylinePlugin<?> plugin = pluginWrapper.getSkylinePlugin();
            DefaultPluginChain chain = new DefaultPluginChain(this, this.index + 1);
            return plugin.handle(exchange, chain);
        } else {
            return Mono.empty();
        }
    }

    @Override
    public <T> T getConfig() {
        SkylinePluginWrapper<?> pluginWrapper = pluginWrappers.get(this.index - 1);
        if (pluginWrapper.getSkylinePlugin() == null) {
            throw new SkylineException("pluginWrapper don't have skyline plugin instance");
        }
        SkylinePlugin<?> plugin = pluginWrapper.getSkylinePlugin();
        return CastUtils.cast(JsonUtils.toObj(pluginWrapper.getJsonConfig(), plugin.getConfigClass()));
    }

    @Override
    public DefaultCapableSwitchManager getCapableSwitchManager() {
        return new DefaultCapableSwitchManager();
    }

}
