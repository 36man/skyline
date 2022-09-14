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
import lombok.Setter;
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 17:54
 */
public class AfterRoutePredicateFactory extends AbstractRoutePredicateFactory<AfterRoutePredicateFactory.Config> {

    public static final String DATETIME_KEY = "datetime";

    public AfterRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(DATETIME_KEY);
    }

    @Override
    public Predicate<ServerRequest> apply(Config config) {
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerRequest serverRequest) {
                final ZonedDateTime now = ZonedDateTime.now();
                return now.isAfter(config.getDatetime());
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("After: %s", config.getClass());
            }
        };
    }


    @Validated
    public static class Config {
        @NotNull
        @Getter
        @Setter
        private ZonedDateTime datetime;
    }
}
