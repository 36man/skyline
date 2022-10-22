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
package org.apache.skyline.test.plugin;

import lombok.Data;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.apache.skyline.plugin.api.SkylinePluginChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @author lijian
 * @since time: 2022-10-22 23:27
 */
public class TestPlugin implements SkylinePlugin<TestPlugin.Config> {


    @Override
    public Mono<Void> handle(ServerWebExchange exchange, SkylinePluginChain chain) {
        byte[] bytes = "welcome dynamic plugin!! success!!".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @Override
    public Class<Config> getConfigClass() {
        return null;
    }

    @Data
    public static class Config {
        private String name;
    }
}
