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
import org.apache.skyline.engine.event.RefreshApiDefinitionEvent;
import org.apache.skyline.engine.event.RefreshApiDefinitionResultEvent;
import org.apache.skyline.engine.event.RefreshApiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lijian
 * @since time: 2022-09-13 18:19
 */
public class CachingApiDefinitionLocator implements ApiDefinitionLocator, InitializingBean, ApplicationListener<RefreshApiDefinitionEvent>, ApplicationEventPublisherAware {

    private static final Logger LOG = LoggerFactory.getLogger(CachingApiDefinitionLocator.class);

    private final ApiDefinitionLocator delegate;

    private List<ApiDefinition> cache;

    private ApplicationEventPublisher publisher;

    public CachingApiDefinitionLocator(ApiDefinitionLocator delegate) {
        this.delegate = delegate;
    }

    @Override
    public Flux<ApiDefinition> getApiDefinitions() {
        return Flux.fromIterable(cache);
    }

    @Override
    public void onApplicationEvent(RefreshApiDefinitionEvent event) {
        cache = event.getApiDefinitions();

        // notify api to refresh cache
        RefreshApiEvent refreshApiEvent = new RefreshApiEvent(event.getSource());
        publisher.publishEvent(refreshApiEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepareApiDefinition();
    }

    private Flux<ApiDefinition> fetch() {
        return this.delegate.getApiDefinitions().sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    private void prepareApiDefinition() {
        try {
            fetch().collect(Collectors.toList()).subscribe(list -> {
                publisher.publishEvent(new RefreshApiDefinitionResultEvent(this));
                cache = list;
                // populate plugin class
            }, this::handleRefreshError);
        } catch (Throwable e) {
            handleRefreshError(e);
        }
    }

    private void handleRefreshError(Throwable throwable) {
        if (LOG.isErrorEnabled()) {
            LOG.error("Refresh routes error !!!", throwable);
        }
        publisher.publishEvent(new RefreshApiDefinitionResultEvent(this, throwable));
    }
}
