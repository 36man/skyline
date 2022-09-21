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
import org.apache.commons.lang3.StringUtils;
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-06 09:15
 */
public class QueryRoutePredicateFactory extends AbstractRoutePredicateFactory<QueryRoutePredicateFactory.Config> {
    /**
     * Param key.
     */
    public static final String PARAM_KEY = "param";

    /**
     * Regexp key.
     */
    public static final String REGEXP_KEY = "regexp";

    public QueryRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(PARAM_KEY, REGEXP_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {

                if (!StringUtils.isNotBlank(config.regexp)) {
                    return exchange.getRequest().getQueryParams().containsKey(config.param);
                }
                List<String> values = exchange.getRequest().getQueryParams().get(config.param);
                if (values == null) {
                    return false;
                }
                return values.stream().anyMatch(v -> StringUtils.isNotBlank(v) && v.matches(config.regexp));
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Query: param=%s regexp=%s", config.getParam(), config.getRegexp());
            }
        };
    }

    @Validated
    public static class Config {
        @NotEmpty
        @Getter
        private String param;

        @Getter
        private String regexp;

        public Config setParam(String param) {
            this.param = param;
            return this;
        }

        public Config setRegexp(String regexp) {
            this.regexp = regexp;
            return this;
        }
    }

}
