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

import org.apache.skyline.model.predicate.AsyncPredicate;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 17:26
 */
public final class ServerWebExchangeUtils {

    public static AsyncPredicate<ServerWebExchange> toAsyncPredicate(Predicate<? super ServerWebExchange> predicate) {
        Assert.notNull(predicate, "predicate must not be null");
        return AsyncPredicate.from(predicate);
    }

    public static String getExchangeDesc(ServerWebExchange serverWebExchange) {
        StringBuilder out = new StringBuilder();
        out.append("Request: ");
        HttpMethod method = serverWebExchange.getRequest().getMethod();
        if (method != null) {
            out.append(method.name());
        }
        out.append(" ");
        out.append(serverWebExchange.getRequest().getURI());
        return out.toString();
    }

}
