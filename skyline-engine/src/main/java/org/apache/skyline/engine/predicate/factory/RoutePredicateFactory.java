/**
 * bravo.org
 * Copyright (c) 2018-2019 ALL Rights Reserved
 */
package org.apache.skyline.engine.predicate.factory;

import org.apache.skyline.engine.support.NameUtils;
import org.apache.skyline.engine.support.ServerWebExchangeUtils;
import org.apache.skyline.engine.support.ShortcutConfigurable;
import org.apache.skyline.model.predicate.AsyncPredicate;
import org.apache.skyline.model.support.Configurable;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 17:14
 */
public interface RoutePredicateFactory<C> extends ShortcutConfigurable, Configurable<C> {
    String PATTERN_KEY = "pattern";

    default Predicate<ServerWebExchange> apply(Consumer<C> consumer) {
        C config = newConfig();
        consumer.accept(config);
        beforeApply(config);
        return apply(config);
    }

    default AsyncPredicate<ServerWebExchange> applyAsync(Consumer<C> consumer) {
        C config = newConfig();
        consumer.accept(config);
        beforeApply(config);
        return applyAsync(config);
    }

    default Class<C> getConfigClass() {
        throw new UnsupportedOperationException("getConfigClass() not implemented");
    }

    default C newConfig() {
        throw new UnsupportedOperationException("newConfig() not implemented");
    }

    Predicate<ServerWebExchange> apply(C config);

    default void beforeApply(C config) {
    }

    default AsyncPredicate<ServerWebExchange> applyAsync(C config) {
        return ServerWebExchangeUtils.toAsyncPredicate(apply(config));
    }

    default String name() {
        return NameUtils.normalizeRoutePredicateName(getClass());
    }

}
