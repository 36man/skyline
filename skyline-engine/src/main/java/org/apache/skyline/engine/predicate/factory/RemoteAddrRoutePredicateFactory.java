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

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import lombok.Getter;
import lombok.Setter;
import org.apache.skyline.engine.support.ipresolver.RemoteAddressResolver;
import org.apache.skyline.model.predicate.SkylinePredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.apache.skyline.engine.support.ShortcutConfigurable.ShortcutType.GATHER_LIST;

/**
 * @author lijian
 * @since time: 2022-09-06 09:35
 */
public class RemoteAddrRoutePredicateFactory extends AbstractRoutePredicateFactory<RemoteAddrRoutePredicateFactory.Config> {

    /**
     * Sources Key
     */
    public static final String SOURCES_KEY = "sources";

    private static final Logger LOG = LoggerFactory.getLogger(RemoteAddrRoutePredicateFactory.class);

    public RemoteAddrRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public ShortcutType shortcutType() {
        return GATHER_LIST;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(SOURCES_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        List<IpSubnetFilterRule> sources = convert(config.sources);

        return new SkylinePredicate() {
            @Override
            public boolean test(ServerWebExchange exchange) {
                InetSocketAddress remoteAddress = config.remoteAddressResolver.resolve(exchange);
                if (remoteAddress != null && remoteAddress.getAddress() != null) {
                    String hostAddress = remoteAddress.getAddress().getHostAddress();
                    String host = exchange.getRequest().getURI().getHost();

                    if (LOG.isDebugEnabled() && !hostAddress.equals(host)) {
                        LOG.debug("Remote addresses didn't match " + hostAddress + " != " + host);
                    }

                    for (IpSubnetFilterRule source : sources) {
                        if (source.matches(remoteAddress)) {
                            return true;
                        }
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
                return String.format("RemoteAddr: %s", config.getSources());
            }
        };
    }

    private List<IpSubnetFilterRule> convert(List<String> values) {
        List<IpSubnetFilterRule> sources = new ArrayList<>();
        for (String arg : values) {
            addSource(sources, arg);
        }
        return sources;
    }

    private void addSource(List<IpSubnetFilterRule> sources, String source) {
        if (!source.contains("/")) { // no netmask, add default
            source = source + "/32";
        }

        String[] ipAddressCidrPrefix = source.split("/", 2);
        String ipAddress = ipAddressCidrPrefix[0];
        int cidrPrefix = Integer.parseInt(ipAddressCidrPrefix[1]);

        sources.add(new IpSubnetFilterRule(ipAddress, cidrPrefix, IpFilterRuleType.ACCEPT));
    }

    @Validated
    public static class Config {
        @NotEmpty
        @Getter
        private List<String> sources = new ArrayList<>();

        @NotNull
        @Getter
        @Setter
        private RemoteAddressResolver remoteAddressResolver = new RemoteAddressResolver() {
        };

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
