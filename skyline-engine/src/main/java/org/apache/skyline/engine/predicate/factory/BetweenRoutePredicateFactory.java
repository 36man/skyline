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
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-06 10:40
 */
public class BetweenRoutePredicateFactory extends AbstractRoutePredicateFactory<BetweenRoutePredicateFactory.Config> {

    /**
     * DateTime 1 key.
     */
    public static final String DATETIME1_KEY = "datetime1";

    /**
     * DateTime 2 key.
     */
    public static final String DATETIME2_KEY = "datetime2";

    public BetweenRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(DATETIME1_KEY, DATETIME2_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        Assert.isTrue(config.getDatetime1().isBefore(config.getDatetime2()),
                config.getDatetime1() + " must be before " + config.getDatetime2());

        return new SkylinePredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                ZonedDateTime now = ZonedDateTime.now();
                return now.isAfter(config.getDatetime1()) && now.isBefore(config.getDatetime2());
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Between: %s and %s", config.getDatetime1(), config.getDatetime2());
            }
        };
    }

    @Validated
    public static class Config {
        @NotNull
        @Getter
        private ZonedDateTime datetime1;

        @NotNull
        @Getter
        private ZonedDateTime datetime2;

        public Config setDatetime1(ZonedDateTime datetime1) {
            this.datetime1 = datetime1;
            return this;
        }

        public Config setDatetime2(ZonedDateTime datetime2) {
            this.datetime2 = datetime2;
            return this;
        }
    }
}
