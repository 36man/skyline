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
package org.apache.skyline.plugin.redirect;

import lombok.Data;
import org.apache.skyline.commons.support.HttpStatusHolder;
import org.apache.skyline.commons.utils.WebUtils;
import org.apache.skyline.plugin.api.SkylinePlugin;
import org.apache.skyline.plugin.api.SkylinePluginChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author lijian
 * @since time: 2022-09-23 16:03
 */
public class RedirectToPlugin implements SkylinePlugin<RedirectToPlugin.Config> {

    private static final Logger LOG = LoggerFactory.getLogger(RedirectToPlugin.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, SkylinePluginChain chain) {
        Config config = chain.getConfig();
        HttpStatusHolder httpStatus = HttpStatusHolder.parse(config.getStatus());
        Assert.isTrue(httpStatus.is3xxRedirection(), "status must be a 3xx code, but was " + config.getStatus());
        final URI url = URI.create(config.getUrl());
        if (!exchange.getResponse().isCommitted()) {
            WebUtils.setResponseStatus(exchange, httpStatus);

            final ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().set(HttpHeaders.LOCATION, url.toString());
            return response.setComplete();
        }
        return Mono.empty();
    }

    @Override
    public Class<Config> getConfigClass() {
        return null;
    }

    @Data
    public static class Config {
        String status;

        String url;
    }
}
