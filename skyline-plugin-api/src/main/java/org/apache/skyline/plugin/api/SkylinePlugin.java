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
package org.apache.skyline.plugin.api;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lijian
 * @since time: 2022-09-09 16:03
 */
public interface SkylinePlugin<T> extends PluginLifeCycle {

    Mono<Void> handle(ServerWebExchange exchange, SkylinePluginChain chain);

    Class<T> getConfigClass();

    default List<CapableSwitch<?>> exportCapableSwitches() {
        return List.of();
    }

    default List<PerpetualResource> exportPerpetualObjs() {
        return List.of();
    }

}
