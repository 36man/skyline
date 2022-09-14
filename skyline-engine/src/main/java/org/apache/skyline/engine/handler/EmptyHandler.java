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

import org.apache.skyline.engine.support.ServerRequestUtils;
import org.apache.skyline.model.support.HttpMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author lijian
 * @since time: 2022-09-07 16:14
 */
public class EmptyHandler implements SkylineHandler {

    private static final Logger LOG = LoggerFactory.getLogger(EmptyHandler.class);

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        serverRequest.attributes().remove(ServerRequestUtils.GATEWAY_API_ATTR);
        if (LOG.isTraceEnabled()) {
            LOG.trace("No RouteDefinition found for [" + ServerRequestUtils.getExchangeDesc(serverRequest) + "]");
        }
        return ServerResponse.ok().bodyValue(HttpMsg.API_NOT_FOUND);
    }
}
