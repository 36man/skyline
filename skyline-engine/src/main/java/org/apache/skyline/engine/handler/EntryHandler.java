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
package org.apache.skyline.engine.handler;

import org.apache.skyline.engine.api.ApiLocator;
import org.apache.skyline.engine.support.ServerRequestUtils;
import org.apache.skyline.model.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * @author lijian
 * @version time: 2022-09-02 11:38
 */
public class EntryHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EntryHandler.class);

    private final ApiLocator apiLocator;

    private final SkylineHandler coreHandler;

    private final SkylineHandler emptyHandler;

    public EntryHandler(ApiLocator apiLocator, SkylineHandler handler, SkylineHandler emptyHandler) {
        this.apiLocator = apiLocator;
        this.coreHandler = handler;
        this.emptyHandler = emptyHandler;
    }

    public Mono<ServerResponse> entryHandle(ServerRequest request) {
        return lookApis(request).flatMap((Function<Api, Mono<SkylineHandler>>) api -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mapping [" + ServerRequestUtils.getExchangeDesc(request) + "] to " + api);
            }

            request.attributes().put(ServerRequestUtils.GATEWAY_API_ATTR, api);
            return Mono.just(coreHandler);
        }).switchIfEmpty(Mono.just(emptyHandler)).flatMap(h -> h.handle(request));
    }

    private Mono<Api> lookApis(ServerRequest request) {
        return apiLocator.getApis().concatMap(
                api -> Mono.just(api).filterWhen(a -> {
                            request.attributes().put(ServerRequestUtils.GATEWAY_PREDICATE_API_IDENTITY, api.getIdentification());
                            return a.getPredicate().apply(request);
                        })
                        .doOnError(e -> LOG.error("Error applying predicate for route: " + api.getIdentification(), e))
                        .onErrorResume(e -> Mono.empty())
        ).next().map(api -> {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Route matched: " + api.getIdentification());
            }
            validateRoute(api, request);
            return api;
        });
    }


    protected void validateRoute(Api api, ServerRequest request) {

    }
}
