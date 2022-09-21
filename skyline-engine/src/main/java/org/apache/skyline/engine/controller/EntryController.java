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
package org.apache.skyline.engine.controller;

import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.api.ApiLocator;
import org.apache.skyline.engine.handler.SkylineHandler;
import org.apache.skyline.engine.support.ServerWebExchangeUtils;
import org.apache.skyline.model.Api;
import org.apache.skyline.model.support.HttpMsg;
import org.apache.skyline.plugin.DefaultPluginChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.apache.skyline.commons.constant.CommonConstant.GATEWAY_API_ATTR;
import static org.apache.skyline.commons.constant.CommonConstant.GATEWAY_PREDICATE_API_IDENTITY;

/**
 * @author lijian
 * @since time: 2022-09-21 17:30
 */
@Controller
public class EntryController {

    private static final Logger LOG = LoggerFactory.getLogger(EntryController.class);

    private final ApiLocator apiLocator;

    private final SkylineHandler coreHandler;

    private final SkylineHandler emptyHandler;

    public EntryController(ApiLocator apiLocator) {
        this.apiLocator = apiLocator;
        this.coreHandler = new coreHandler();
        this.emptyHandler = new EmptyHandler();
    }

    @RequestMapping("/**")
    public Mono<Void> entry(ServerWebExchange exchange) {
        return lookApis(exchange).flatMap((Function<Api, Mono<SkylineHandler>>) api -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mapping [" + ServerWebExchangeUtils.getExchangeDesc(exchange) + "] to " + api);
            }

            exchange.getAttributes().put(GATEWAY_API_ATTR, api);
            return Mono.just(coreHandler);
        }).switchIfEmpty(Mono.just(emptyHandler)).flatMap(h -> h.handle(exchange));
    }

    private static class EmptyHandler implements SkylineHandler {

        @Override
        public Mono<Void> handle(ServerWebExchange exchange) {
            exchange.getAttributes().remove(GATEWAY_API_ATTR);
            if (LOG.isTraceEnabled()) {
                LOG.trace("No RouteDefinition found for [" + ServerWebExchangeUtils.getExchangeDesc(exchange) + "]");
            }
            byte[] bytes = HttpMsg.API_NOT_FOUND.getMsg().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }

    private static class coreHandler implements SkylineHandler {

        @Override
        public Mono<Void> handle(ServerWebExchange serverWebExchange) {
            Object objAttr = serverWebExchange.getAttributes().get(GATEWAY_API_ATTR);
            if (objAttr == null) {
                throw new SkylineException("not found api from attribute");
            }
            Api api = (Api) objAttr;
            return new DefaultPluginChain(api.getPluginWrappers()).handle(serverWebExchange);
        }
    }

    private Mono<Api> lookApis(ServerWebExchange serverWebExchange) {
        return apiLocator.getApis().concatMap(
                api -> Mono.just(api).filterWhen(a -> {
                            serverWebExchange.getAttributes().put(GATEWAY_PREDICATE_API_IDENTITY, api.getIdentification());
                            return a.getPredicate().apply(serverWebExchange);
                        })
                        .doOnError(e -> LOG.error("Error applying predicate for route: " + api.getIdentification(), e))
                        .onErrorResume(e -> Mono.empty())
        ).next().map(api -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Route matched: " + api.getIdentification());
            }
            validateRoute(api, serverWebExchange);
            return api;
        });
    }


    protected void validateRoute(Api api, ServerWebExchange serverWebExchange) {

    }
}
