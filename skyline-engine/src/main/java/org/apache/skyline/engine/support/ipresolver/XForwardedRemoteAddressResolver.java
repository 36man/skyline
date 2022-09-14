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
package org.apache.skyline.engine.support.ipresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author lijian
 * @since time: 2022-09-06 09:58
 */
public class XForwardedRemoteAddressResolver implements RemoteAddressResolver {

    /**
     * Forwarded-For header name.
     */
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final Logger log = LoggerFactory.getLogger(XForwardedRemoteAddressResolver.class);

    private final RemoteAddressResolver defaultRemoteIpResolver = new RemoteAddressResolver() {
    };

    private final int maxTrustedIndex;

    private XForwardedRemoteAddressResolver(int maxTrustedIndex) {
        this.maxTrustedIndex = maxTrustedIndex;
    }

    /**
     * @return a {@link XForwardedRemoteAddressResolver} which always extracts the first
     * IP address found in the X-Forwarded-For header (when present). Equivalent to
     * calling {@link #maxTrustedIndex(int)} with a {@link #maxTrustedIndex} of
     * {@link Integer#MAX_VALUE}. This configuration is vulnerable to spoofing via
     * manually setting the X-Forwarded-For header. If the resulting IP address is used
     * for security purposes, use {@link #maxTrustedIndex(int)} instead.
     */
    public static XForwardedRemoteAddressResolver trustAll() {
        return new XForwardedRemoteAddressResolver(Integer.MAX_VALUE);
    }

    /**
     * <em>trusted</em> IP address found in the X-Forwarded-For header (when present).
     * This configuration exists to prevent a malicious actor from spoofing the value of
     * the X-Forwarded-For header. If you know that your gateway application is only
     * accessible from a trusted load balancer, then you can trust that the load
     * balancer will append a valid client IP address to the X-Forwarded-For header, and
     * should use a value of `1` for the `maxTrustedIndex`.
     *
     *
     * Given the X-Forwarded-For value of [0.0.0.1, 0.0.0.2, 0.0.0.3]:
     *
     * <pre>
     * maxTrustedIndex -> result
     *
     * [MIN_VALUE,0] -> IllegalArgumentException
     * 1 -> 0.0.0.3
     * 2 -> 0.0.0.2
     * 3 -> 0.0.0.1
     * [4, MAX_VALUE] -> 0.0.0.1
     * </pre>
     * @param maxTrustedIndex correlates to the number of trusted proxies expected in
     * front of Spring Cloud Gateway (index starts at 1).
     * @return a {@link XForwardedRemoteAddressResolver} which extracts the last
     */
    public static XForwardedRemoteAddressResolver maxTrustedIndex(int maxTrustedIndex) {
        Assert.isTrue(maxTrustedIndex > 0, "An index greater than 0 is required");
        return new XForwardedRemoteAddressResolver(maxTrustedIndex);
    }

    /**
     * The X-Forwarded-For header contains a comma separated list of IP addresses. This
     * method parses those IP addresses into a list. If no X-Forwarded-For header is
     * found, an empty list is returned. If multiple X-Forwarded-For headers are found, an
     * empty list is returned out of caution.
     * @return The parsed values of the X-Forwarded-Header.
     */
    @Override
    public Optional<InetSocketAddress> resolve(ServerRequest serverRequest) {
        List<String> xForwardedValues = extractXForwardedValues(serverRequest);
        Collections.reverse(xForwardedValues);
        if (!xForwardedValues.isEmpty()) {
            int index = Math.min(xForwardedValues.size(), maxTrustedIndex) - 1;
            return Optional.of(new InetSocketAddress(xForwardedValues.get(index), 0));
        }
        return defaultRemoteIpResolver.resolve(serverRequest);
    }

    private List<String> extractXForwardedValues(ServerRequest serverRequest) {
        List<String> xForwardedValues = serverRequest.headers().asHttpHeaders().get(X_FORWARDED_FOR);
        if (xForwardedValues == null || xForwardedValues.isEmpty()) {
            return Collections.emptyList();
        }
        if (xForwardedValues.size() > 1) {
            log.warn("Multiple X-Forwarded-For headers found, discarding all");
            return Collections.emptyList();
        }
        List<String> values = Arrays.asList(xForwardedValues.get(0).split(", "));
        if (values.size() == 1 && !StringUtils.hasText(values.get(0))) {
            return Collections.emptyList();
        }
        return values;
    }
}