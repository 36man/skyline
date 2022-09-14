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
import org.apache.skyline.engine.support.ServerRequestUtils;
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 22:45
 */
public class HostRoutePredicateFactory extends AbstractRoutePredicateFactory<HostRoutePredicateFactory.Config> {

    /**
     * Host Key
     */
    public static final String HOST_KEY = "Host";
    private final PathMatcher pathMatcher = new AntPathMatcher(".");

    public HostRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("patterns");
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST;
    }

    @Override
    public Predicate<ServerRequest> apply(Config config) {
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerRequest serverRequest) {
                String host = serverRequest.headers().asHttpHeaders().getFirst(HOST_KEY);
                if (host == null) {
                    return false;
                }
                String matchPattern = config.getPatterns().stream()
                        .filter(p -> pathMatcher.match(p, host)).findFirst().orElse(null);
                if (matchPattern != null) {
                    Map<String, String> variables = pathMatcher.extractUriTemplateVariables(matchPattern, host);
                    ServerRequestUtils.putUriTemplateVariables(serverRequest, variables);
                    return true;
                }
                return false;
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Hosts: %s", config.getPatterns());
            }
        };
    }

    @Validated
    public static class Config {
        @Getter
        private List<String> patterns = new ArrayList<>();

        public Config getPatterns(List<String> patterns) {
            this.patterns = patterns;
            return this;
        }
    }

}