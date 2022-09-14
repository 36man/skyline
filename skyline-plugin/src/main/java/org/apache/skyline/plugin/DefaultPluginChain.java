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
import org.apache.skyline.plugin.api.SkylinePluginChain;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lijian
 * @since time: 2022-09-09 17:23
 */
public class DefaultPluginChain implements SkylinePluginChain {

    private final int index;

    private final List<SkylinePlugin> plugins;

    public DefaultPluginChain(List<SkylinePlugin> plugins) {
        this.plugins = plugins;
        this.index = 0;
    }

    public DefaultPluginChain(DefaultPluginChain parent, int index) {
        this.plugins = parent.getPlugins();
        this.index = index;
    }

    public List<SkylinePlugin> getPlugins() {
        return plugins;
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.defer(() -> {
            if (this.index < plugins.size()) {
                SkylinePlugin plugin = plugins.get(this.index);
                DefaultPluginChain chain = new DefaultPluginChain(this, this.index + 1);
                return plugin.handle(request, chain);
            }
            else {
                return Mono.empty(); // complete
            }
        });
    }

}
