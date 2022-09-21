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
package org.apache.skyline.engine.api.locator;

import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.api.ApiDefinition;
import org.apache.skyline.engine.api.ApiLocator;
import org.apache.skyline.engine.config.SkylineProperties;
import org.apache.skyline.engine.enums.PredicateFactoryEnum;
import org.apache.skyline.engine.event.PredicateArgsEvent;
import org.apache.skyline.engine.predicate.PredicateDefinition;
import org.apache.skyline.engine.predicate.factory.PredicateFactoryManager;
import org.apache.skyline.engine.predicate.factory.RoutePredicateFactory;
import org.apache.skyline.engine.support.ApiBuilderUtils;
import org.apache.skyline.engine.support.ConfigurationService;
import org.apache.skyline.model.Api;
import org.apache.skyline.model.predicate.AsyncPredicate;
import org.apache.skyline.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * apiLocator that loads apis from a ApiDefinitionLocator.
 * @author lijian
 * @since time: 2022-09-07 10:02
 */
public class DefaultApiLocator implements ApiLocator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultApiLocator.class);

    private final ApiDefinitionLocator apiDefinitionLocator;

    private final ConfigurationService configurationService;

    private final PredicateFactoryManager predicateFactoryManager;

    private final PluginManager pluginManager;

    private final SkylineProperties skylineProperties;

    public DefaultApiLocator(ApiDefinitionLocator apiDefinitionLocator, PredicateFactoryManager predicateFactoryManager,
                             ConfigurationService configurationService, PluginManager pluginManager, SkylineProperties skylineProperties) {
        this.apiDefinitionLocator = apiDefinitionLocator;
        this.configurationService = configurationService;
        this.predicateFactoryManager = predicateFactoryManager;
        this.pluginManager = pluginManager;
        this.skylineProperties = skylineProperties;
    }

    @Override
    public Flux<Api> getApis() {
        return this.apiDefinitionLocator.getApiDefinitions().map(this::convertToApi)
                .onErrorContinue((error, obj) -> {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("apiDefinition id " + ((ApiDefinition) obj).getId()
                                + " will be ignored. Definition has invalid configs, " + error.getMessage());
                    }
                }).map(api -> {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("api found: " + api.getIdentification());
                    }
                    return api;
                });
    }

    private Api convertToApi(ApiDefinition apiDefinition) {
        return ApiBuilderUtils.async(apiDefinition).asyncPredicate(combinePredicates(apiDefinition)).build(pluginManager, skylineProperties);
    }

    private AsyncPredicate<ServerWebExchange> combinePredicates(ApiDefinition apiDefinition) {
        List<PredicateDefinition> predicates = apiDefinition.getPredicates();
        if (predicates == null || predicates.isEmpty()) {
            // this is a very rare case, but possible, just match all
            return AsyncPredicate.from(exchange -> true);
        }
        AsyncPredicate<ServerWebExchange> predicate = lookupPredicate(apiDefinition, predicates.get(0));

        for (PredicateDefinition andPredicate : predicates.subList(1, predicates.size())) {
            AsyncPredicate<ServerWebExchange> found = lookupPredicate(apiDefinition, andPredicate);
            predicate = predicate.and(found);
        }

        return predicate;
    }

    private AsyncPredicate<ServerWebExchange> lookupPredicate(ApiDefinition apiDefinition, PredicateDefinition predicateDefinition) {
        PredicateFactoryEnum predicateFactoryEnum = PredicateFactoryEnum.getPredicateFactoryEnum(predicateDefinition.getName());
        if (predicateFactoryEnum == null) {
            throw new SkylineException("not found predicate Factory [" + predicateDefinition.getName() + "] in enums");
        }
        RoutePredicateFactory factory = this.predicateFactoryManager.getManagedPredicateFactories().get(predicateFactoryEnum);
        if (factory == null) {
            // not happen except predicateFactoryManager config is wrong
            throw new SkylineException("Unable to find RoutePredicateFactory with name " + predicateDefinition.getName());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("RouteDefinition " + apiDefinition.getId() + " applying " + predicateDefinition.getArgs() + " to "
                    + predicateDefinition.getName());
        }

        // @formatter:off
        Object config = this.configurationService.with(factory)
                .name(predicateDefinition.getName())
                .properties(predicateDefinition.getArgs())
                .eventFunction((o, properties) ->
                    new PredicateArgsEvent(DefaultApiLocator.this, String.valueOf(apiDefinition.getId()), (Map<String, Object>)properties)
                )
                .bind();
        // @formatter:on

        return factory.applyAsync(config);
    }

}
