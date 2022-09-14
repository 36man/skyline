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
package org.apache.skyline.engine.api.builder;

import org.apache.skyline.model.predicate.AsyncPredicate;
import org.apache.skyline.model.support.ModelUtils;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-14 11:40
 */
public class AsyncBuilder extends AbstractBuilder<AsyncBuilder> {

    protected AsyncPredicate<ServerRequest> predicate;

    @Override
    protected AsyncBuilder getThis() {
        return this;
    }

    @Override
    public AsyncPredicate<ServerRequest> getPredicate() {
        return this.predicate;
    }

    public AsyncBuilder predicate(Predicate<ServerRequest> predicate) {
        return asyncPredicate(ModelUtils.toAsyncPredicate(predicate));
    }

    public AsyncBuilder asyncPredicate(AsyncPredicate<ServerRequest> predicate) {
        this.predicate = predicate;
        return this;
    }

    public AsyncBuilder and(AsyncPredicate<ServerRequest> predicate) {
        Assert.notNull(this.predicate, "can not call and() on null predicate");
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    public AsyncBuilder or(AsyncPredicate<ServerRequest> predicate) {
        Assert.notNull(this.predicate, "can not call or() on null predicate");
        this.predicate = this.predicate.or(predicate);
        return this;
    }

    public AsyncBuilder negate() {
        Assert.notNull(this.predicate, "can not call negate() on null predicate");
        this.predicate = this.predicate.negate();
        return this;
    }
}
