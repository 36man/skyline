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
package org.apache.skyline.engine.predicate.factory;

import lombok.Getter;
import org.apache.skyline.engine.support.ipresolver.XForwardedRemoteAddressResolver;
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-06 10:17
 */
public class XForwardedRemoteAddrRoutePredicateFactory extends AbstractRoutePredicateFactory<XForwardedRemoteAddrRoutePredicateFactory.Config> {

    private static final Logger LOG = LoggerFactory.getLogger(XForwardedRemoteAddrRoutePredicateFactory.class);

    public static final String SOURCES_KEY = "sources";

    public XForwardedRemoteAddrRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("sources");
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Applying XForwardedRemoteAddr route predicate with maxTrustedIndex of "
                    + config.getMaxTrustedIndex() + " for " + config.getSources().size() + " source(s)");
        }

        RemoteAddrRoutePredicateFactory.Config wrappedConfig = new RemoteAddrRoutePredicateFactory.Config();
        wrappedConfig.setSources(config.getSources());
        wrappedConfig.setRemoteAddressResolver(XForwardedRemoteAddressResolver.maxTrustedIndex(config.getMaxTrustedIndex()));
        RemoteAddrRoutePredicateFactory remoteAddrRoutePredicateFactory = new RemoteAddrRoutePredicateFactory();
        Predicate<ServerWebExchange> wrappedPredicate = remoteAddrRoutePredicateFactory.apply(wrappedConfig);
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                boolean isAllowed = wrappedPredicate.test(exchange);

                if (LOG.isDebugEnabled()) {
                    ServerHttpRequest request = exchange.getRequest();
                    LOG.debug("Request for \"" + request.getURI() + "\" from client \""
                            + request.getRemoteAddress().getAddress().getHostAddress() + "\" with \""
                            + XForwardedRemoteAddressResolver.X_FORWARDED_FOR + "\" header value of \""
                            + request.getHeaders().get(XForwardedRemoteAddressResolver.X_FORWARDED_FOR) + "\" is "
                            + (isAllowed ? "ALLOWED" : "NOT ALLOWED"));
                }

                return isAllowed;
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("XForwardedRemoteAddr: %s", config.getSources());
            }
        };
    }

    @Validated
    public static class Config {
        // Trust the last (right-most) value in the "X-Forwarded-For" header by default,
        // which represents the last reverse proxy that was used when calling the gateway.
        @Getter
        private int maxTrustedIndex = 1;

        @Getter
        private List<String> sources = new ArrayList<>();

        public Config setMaxTrustedIndex(int maxTrustedIndex) {
            this.maxTrustedIndex = maxTrustedIndex;
            return this;
        }

        public Config setSources(List<String> sources) {
            this.sources = sources;
            return this;
        }

        public Config setSources(String... sources) {
            this.sources = Arrays.asList(sources);
            return this;
        }
    }
}
