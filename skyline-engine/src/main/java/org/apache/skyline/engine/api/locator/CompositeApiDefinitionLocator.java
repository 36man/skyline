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

import org.apache.skyline.engine.api.ApiDefinition;
import reactor.core.publisher.Flux;

/**
 * @author lijian
 * @since time: 2022-09-07 17:19
 */
public class CompositeApiDefinitionLocator implements ApiDefinitionLocator {

    private final Flux<ApiDefinitionLocator> delegates;

    public CompositeApiDefinitionLocator(Flux<ApiDefinitionLocator> delegates) {
        this.delegates = delegates;
    }

    @Override
    public Flux<ApiDefinition> getApiDefinitions() {
        return this.delegates.flatMapSequential(ApiDefinitionLocator::getApiDefinitions);
    }
}
