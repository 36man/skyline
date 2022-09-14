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

import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.support.ServerRequestUtils;
import org.apache.skyline.model.Api;
import org.apache.skyline.plugin.DefaultPluginChain;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * the core handler of skyline
 * @author lijian
 * @since time: 2022-09-07 15:40
 */
public class CoreHandler implements SkylineHandler {
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        Object objAttr = serverRequest.attributes().get(ServerRequestUtils.GATEWAY_API_ATTR);
        if (objAttr == null) {
            throw new SkylineException("not found api from attribute");
        }
        Api api = (Api) objAttr;
        return new DefaultPluginChain(api.getPlugins()).handle(serverRequest);
    }
}
