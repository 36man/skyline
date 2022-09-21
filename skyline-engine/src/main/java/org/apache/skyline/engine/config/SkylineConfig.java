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
package org.apache.skyline.engine.config;

import org.apache.skyline.engine.api.ApiLocator;
import org.apache.skyline.engine.api.locator.ApiDefinitionLocator;
import org.apache.skyline.engine.api.locator.CachingApiLocator;
import org.apache.skyline.engine.api.locator.CompositeApiDefinitionLocator;
import org.apache.skyline.engine.api.locator.CompositeApiLocator;
import org.apache.skyline.engine.api.locator.DefaultApiLocator;
import org.apache.skyline.engine.api.locator.TestApiDefinitionLocator;
import org.apache.skyline.engine.controller.EntryController;
import org.apache.skyline.engine.predicate.factory.AfterRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.BeforeRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.BetweenRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.HeaderRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.MethodRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.PathRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.PredicateFactoryManager;
import org.apache.skyline.engine.predicate.factory.QueryRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.RemoteAddrRoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.RoutePredicateFactory;
import org.apache.skyline.engine.predicate.factory.XForwardedRemoteAddrRoutePredicateFactory;
import org.apache.skyline.engine.support.ConfigurationService;
import org.apache.skyline.plugin.PluginDefinitionManager;
import org.apache.skyline.plugin.PluginManager;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author lijian
 * @since time: 2022-09-07 17:08
 */
@Configuration
@EnableConfigurationProperties
public class SkylineConfig {

    @Bean
    public EntryController entryController(@Qualifier("cachingApiLocator") ApiLocator cachingApiLocator) {
        return new EntryController(cachingApiLocator);
    }

    @Bean
    public ApiLocator defaultApiLocator(@Qualifier("compositeApiDefinitionLocator") ApiDefinitionLocator compositeApiDefinitionLocator,
                                        PredicateFactoryManager predicateFactoryManager,
                                        ConfigurationService configurationService,
                                        SkylineProperties skylineProperties,
                                        PluginManager pluginManager) {
        return new DefaultApiLocator(compositeApiDefinitionLocator, predicateFactoryManager, configurationService, pluginManager, skylineProperties);
    }

    @Bean
    public ApiLocator compositeApiLocator(@Qualifier("defaultApiLocator") ApiLocator defaultApiLocator) {
        return new CompositeApiLocator(Flux.just(defaultApiLocator));
    }

    @Bean
    public ApiLocator cachingApiLocator(@Qualifier("compositeApiLocator") ApiLocator compositeApiLocator) {
        return new CachingApiLocator(compositeApiLocator);
    }

    @Bean
    public ApiDefinitionLocator compositeApiDefinitionLocator(@Qualifier("testApiDefinitionLocator") ApiDefinitionLocator testApiDefinitionLocator) {
        return new CompositeApiDefinitionLocator(Flux.just(testApiDefinitionLocator));
    }

    @Bean
    public ApiDefinitionLocator testApiDefinitionLocator(SkylineProperties skylineProperties, PluginDefinitionManager pluginDefinitionManager) {
        return new TestApiDefinitionLocator(skylineProperties, pluginDefinitionManager);
    }

    @Bean
    public PredicateFactoryManager predicateFactoryManager(List<RoutePredicateFactory<?>> predicateFactories) {
        return new PredicateFactoryManager(predicateFactories);
    }

    @Bean
    public ConfigurationService gatewayConfigurationService(BeanFactory beanFactory,
                                                            @Qualifier("webFluxConversionService") ObjectProvider<ConversionService> conversionService,
                                                            ObjectProvider<Validator> validator) {
        return new ConfigurationService(beanFactory, conversionService, validator);
    }

    @Bean
    public RoutePredicateFactory<?> afterRoutePredicateFactory() {
        return new AfterRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> beforeRoutePredicateFactory() {
        return new BeforeRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> betweenRoutePredicateFactory() {
        return new BetweenRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> headerRoutePredicateFactory() {
        return new HeaderRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> methodRoutePredicateFactory() {
        return new MethodRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> pathRoutePredicateFactory() {
        return new PathRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> queryRoutePredicateFactory() {
        return new QueryRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> remoteAddrRoutePredicateFactory() {
        return new RemoteAddrRoutePredicateFactory();
    }

    @Bean
    public RoutePredicateFactory<?> xForwardedRemoteAddrRoutePredicateFactory() {
        return new XForwardedRemoteAddrRoutePredicateFactory();
    }

    @Bean
    public SkylineProperties skylineProperties() {
        return new SkylineProperties();
    }

    @Bean
    public PluginManager pluginManager() {
        return new PluginManager();
    }

    @Bean
    public PluginDefinitionManager pluginDefinitionManager() {
        return new PluginDefinitionManager();
    }
}
