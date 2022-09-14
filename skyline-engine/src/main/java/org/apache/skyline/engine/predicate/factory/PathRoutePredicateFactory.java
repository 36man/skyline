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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.PathContainer;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPattern.PathMatchInfo;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.apache.skyline.engine.support.ServerRequestUtils.GATEWAY_PREDICATE_API_IDENTITY;
import static org.apache.skyline.engine.support.ServerRequestUtils.GATEWAY_PREDICATE_MATCHED_PATH_API_IDENTITY;
import static org.apache.skyline.engine.support.ServerRequestUtils.GATEWAY_PREDICATE_MATCHED_PATH_ATTR;
import static org.apache.skyline.engine.support.ServerRequestUtils.putUriTemplateVariables;

/**
 * @author lijian
 * @since time: 2022-09-05 23:10
 */
public class PathRoutePredicateFactory extends AbstractRoutePredicateFactory<PathRoutePredicateFactory.Config> {

    private static final Logger LOG = LoggerFactory.getLogger(PathRoutePredicateFactory.class);

    public static final String MATCH_TRAILING_SLASH_KEY = "matchTrailingSlash";

    public static final String PATTERN_KEY = "patterns";

    private final PathPatternParser pathPatternParser = new PathPatternParser();

    public PathRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(PATTERN_KEY, MATCH_TRAILING_SLASH_KEY);
    }

    @Override
    public ShortcutType shortcutType() {
        return ShortcutType.GATHER_LIST_TAIL_FLAG;
    }

    private static void traceMatch(String prefix, Object desired, Object actual, boolean match) {
        if (LOG.isTraceEnabled()) {
            String message = String.format("%s \"%s\" %s against value \"%s\"", prefix, desired,
                    match ? "matches" : "does not match", actual);
            LOG.trace(message);
        }
    }

    @Override
    public Predicate<ServerRequest> apply(Config config) {
        final ArrayList<PathPattern> pathPatterns = new ArrayList<>();
        synchronized (this.pathPatternParser) {
            pathPatternParser.setMatchOptionalTrailingSeparator(config.isMatchTrailingSlash());
            config.getPatterns().forEach(pattern -> {
                PathPattern pathPattern = this.pathPatternParser.parse(pattern);
                pathPatterns.add(pathPattern);
            });
        }
        return new SkylinePredicate() {
            @Override
            public boolean test(ServerRequest serverRequest) {
                PathContainer path = PathContainer.parsePath(serverRequest.uri().getRawPath());
                Optional<PathPattern> matchPattern = pathPatterns.stream()
                        .filter(pathPattern -> pathPattern.matches(path)).findAny();
                if (matchPattern.isPresent()) {
                    traceMatch("Pattern", config.getPatterns(), path, true);
                    PathMatchInfo pathMatchInfo = matchPattern.get().matchAndExtract(path);
                    if (pathMatchInfo == null) {
                        //todo customize exception
                        throw new RuntimeException("PathPattern matchAndExtract error!");
                    }
                    putUriTemplateVariables(serverRequest, pathMatchInfo.getUriVariables());
                    serverRequest.attributes().put(GATEWAY_PREDICATE_MATCHED_PATH_ATTR, matchPattern.get().getPatternString());
                    String apiIdentity = (String) serverRequest.attributes().get(GATEWAY_PREDICATE_API_IDENTITY);
                    if (apiIdentity != null) {
                        serverRequest.attributes().put(GATEWAY_PREDICATE_MATCHED_PATH_API_IDENTITY, apiIdentity);
                    }
                    return true;
                } else {
                    traceMatch("Pattern", config.getPatterns(), path, false);
                    return false;
                }
            }

            @Override
            public Object getConfig() {
                return config;
            }

            @Override
            public String toString() {
                return String.format("Paths: %s, match trailing slash: %b", config.getPatterns(),
                        config.isMatchTrailingSlash());
            }
        };
    }

    @Validated
    public static class Config {
        @Getter
        private List<String> patterns = new ArrayList<>();

        @Getter
        private boolean matchTrailingSlash = true;

        public Config setPatterns(List<String> patterns) {
            this.patterns = patterns;
            return this;
        }

        public Config setMatchTrailingSlash(boolean matchTrailingSlash) {
            this.matchTrailingSlash = matchTrailingSlash;
            return this;
        }
    }
}
