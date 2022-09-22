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
package org.apache.skyline.plugin.req.header.add;

import lombok.Data;
import org.apache.skyline.commons.utils.WebUtils;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.apache.skyline.plugin.api.SkylinePluginChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lijian
 * @since time: 2022-09-16 16:21
 */
public class AddRequestHeaderPlugin implements SkylinePlugin<AddRequestHeaderPlugin.Config> {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, SkylinePluginChain chain) {
        Config config = chain.getConfig();
        String value = WebUtils.expand(exchange, config.getValue());
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(httpHeaders -> httpHeaders.add(config.getName(), value)).build();

        return chain.handle(exchange.mutate().request(request).build());
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }


    @Data
    public static class Config {
        private String name;
        private String value;
    }
}
