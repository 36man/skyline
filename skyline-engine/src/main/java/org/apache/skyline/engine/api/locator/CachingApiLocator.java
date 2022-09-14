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

import org.apache.skyline.engine.api.ApiLocator;
import org.apache.skyline.engine.event.RefreshApiEvent;
import org.apache.skyline.engine.event.RefreshApiResultEvent;
import org.apache.skyline.model.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author lijian
 * @since time: 2022-09-07 09:24
 */
public class CachingApiLocator implements Ordered, ApiLocator,
        ApplicationListener<RefreshApiEvent>, ApplicationEventPublisherAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(CachingApiLocator.class);

    private static final String CACHE_KEY = "apis";

    private final ApiLocator delegate;

    private final Map<String, List> cache = new ConcurrentHashMap<>();

    private ApplicationEventPublisher publisher;

    public CachingApiLocator(ApiLocator delegate) {
        this.delegate = delegate;
    }

    private Flux<Api> fetch() {
        return this.delegate.getApis().sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    public Flux<Api> refresh() {
        this.cache.clear();
        return getApis();
    }


    @Override
    public Flux<Api> getApis() {
        return Flux.fromIterable(cache.get(CACHE_KEY));
    }

    @Override
    public void onApplicationEvent(RefreshApiEvent event) {
        prepareApi();
    }

    private void prepareApi() {
        try {
            fetch().collect(Collectors.toList()).subscribe(list -> {
                publisher.publishEvent(new RefreshApiResultEvent(this));
                cache.put(CACHE_KEY, list);
            }, this::handleRefreshError);
        } catch (Throwable e) {
            handleRefreshError(e);
        }
    }

    private void handleRefreshError(Throwable throwable) {
        if (LOG.isErrorEnabled()) {
            LOG.error("Refresh routes error !!!", throwable);
        }
        publisher.publishEvent(new RefreshApiResultEvent(this, throwable));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        prepareApi();
    }
}
