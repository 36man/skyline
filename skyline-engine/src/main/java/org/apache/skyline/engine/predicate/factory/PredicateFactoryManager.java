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
import org.apache.skyline.commons.exception.SkylineException;
import org.apache.skyline.engine.enums.PredicateFactoryEnum;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lijian
 * @since time: 2022-09-06 17:32
 */
public class PredicateFactoryManager implements InitializingBean {

    @Getter
    private final Map<PredicateFactoryEnum, RoutePredicateFactory<?>> managedPredicateFactories = new HashMap<>();

    private final List<RoutePredicateFactory<?>> factories;

    public PredicateFactoryManager(List<RoutePredicateFactory<?>> factories) {
        this.factories = factories;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (RoutePredicateFactory<?> factory : factories) {
            PredicateFactoryEnum predicateFactoryEnum = PredicateFactoryEnum.getPredicateFactoryEnum(factory.name());
            if (predicateFactoryEnum == null) {
                throw new SkylineException("Predicate[" + factory.name() + "] not found");
            }
            managedPredicateFactories.put(predicateFactoryEnum, factory);
        }
    }
}
