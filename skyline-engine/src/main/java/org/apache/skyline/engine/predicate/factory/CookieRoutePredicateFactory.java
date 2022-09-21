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
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.springframework.http.HttpCookie;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 17:59
 */
public class CookieRoutePredicateFactory extends AbstractRoutePredicateFactory<CookieRoutePredicateFactory.Config> {

    /**
     * Name Key
     */
    public static final String NAME_KEY = "name";

    /**
     * Regexp key
     */
    public static final String REGEXP_KEY = "regexp";

    public CookieRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(NAME_KEY, REGEXP_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                List<HttpCookie> cookies = exchange.getRequest().getCookies().get(config.name);
                if (cookies == null) {
                    return false;
                }
                for (HttpCookie cookie : cookies) {
                    if (cookie.getValue().matches(config.regexp)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Cookie: name=%s regexp=%s", config.name, config.regexp);
            }
        };
    }

    @Validated
    public static class Config {
        @NotEmpty
        @Getter
        private String name;

        @NotEmpty
        @Getter
        private String regexp;

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }
    }
}
