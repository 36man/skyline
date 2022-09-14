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
package org.apache.skyline.engine.support;

import org.apache.skyline.commons.utils.CastUtils;
import org.apache.skyline.model.predicate.AsyncPredicate;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 17:26
 */
public final class ServerRequestUtils {

    /**
     * URI template variables attribute name.
     */
    public static final String URI_TEMPLATE_VARIABLES_ATTRIBUTE = qualify("uriTemplateVariables");

    /**
     * Gateway predicate matched path attribute name.
     */
    public static final String GATEWAY_PREDICATE_MATCHED_PATH_ATTR = qualify("gatewayPredicateMatchedPathAttr");

    /**
     * Gateway predicate matched path route id attribute name.
     */
    public static final String GATEWAY_PREDICATE_MATCHED_PATH_API_IDENTITY = qualify(
            "gatewayPredicateMatchedPathApiIdentity");

    /**
     * Gateway predicate api identity.
     */
    public static final String GATEWAY_PREDICATE_API_IDENTITY = qualify("gatewayPredicateApiIdentity");

    public static final String GATEWAY_API_ATTR = qualify("gatewayApi");

    private static String qualify(String attr) {
        return ServerRequestUtils.class.getName() + "." + attr;
    }

    public static AsyncPredicate<ServerRequest> toAsyncPredicate(Predicate<? super ServerRequest> predicate) {
        Assert.notNull(predicate, "predicate must not be null");
        return AsyncPredicate.from(predicate);
    }

    public static void putUriTemplateVariables(ServerRequest serverRequest, Map<String, String> uriVariables) {
        if (serverRequest.attributes().containsKey(URI_TEMPLATE_VARIABLES_ATTRIBUTE)) {
            Object v = serverRequest.attributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            Map<String, Object> existingVariables = CastUtils.cast(serverRequest.attributes().get(URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            Map<String, Object> newVariables = new HashMap<>();
            if (existingVariables != null) {
                newVariables.putAll(existingVariables);
            }
            newVariables.putAll(uriVariables);
            serverRequest.attributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, newVariables);
        } else {
            serverRequest.attributes().put(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriVariables);
        }
    }

    public static String getExchangeDesc(ServerRequest request) {
        StringBuilder out = new StringBuilder();
        out.append("Request: ");
        out.append(request.methodName());
        out.append(" ");
        out.append(request.uri());
        return out.toString();
    }

}
