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
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-14 11:41
 */
public class Builder extends AbstractBuilder<Builder> {

    protected Predicate<ServerWebExchange> predicate;

    @Override
    protected Builder getThis() {
        return this;
    }

    @Override
    public AsyncPredicate<ServerWebExchange> getPredicate() {
        return ModelUtils.toAsyncPredicate(this.predicate);
    }

    public Builder and(Predicate<ServerWebExchange> predicate) {
        Assert.notNull(this.predicate, "can not call and() on null predicate");
        this.predicate = this.predicate.and(predicate);
        return this;
    }

    public Builder or(Predicate<ServerWebExchange> predicate) {
        Assert.notNull(this.predicate, "can not call or() on null predicate");
        this.predicate = this.predicate.or(predicate);
        return this;
    }

    public Builder negate() {
        Assert.notNull(this.predicate, "can not call negate() on null predicate");
        this.predicate = this.predicate.negate();
        return this;
    }
}
