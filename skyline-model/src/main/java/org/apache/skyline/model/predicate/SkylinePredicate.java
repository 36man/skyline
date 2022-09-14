/**
 * bravo.org
 * Copyright (c) 2018-2019 ALL Rights Reserved
 */
package org.apache.skyline.model.predicate;

import org.apache.skyline.model.support.HasConfig;
import org.apache.skyline.model.support.Visitor;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.function.Predicate;

/**
 * @author lijian
 * @since time: 2022-09-05 12:02
 */
public interface SkylinePredicate extends Predicate<ServerRequest>, HasConfig {
    @Override
    default Predicate<ServerRequest> and(Predicate<? super ServerRequest> other) {
        return new AndGatewayPredicate(this, wrapIfNeeded(other));
    }

    @Override
    default Predicate<ServerRequest> negate() {
        return new NegateGatewayPredicate(this);
    }

    @Override
    default Predicate<ServerRequest> or(Predicate<? super ServerRequest> other) {
        return new OrGatewayPredicate(this, wrapIfNeeded(other));
    }

    default void accept(Visitor visitor) {
        visitor.visit(this);
    }

    static SkylinePredicate wrapIfNeeded(Predicate<? super ServerRequest> other) {
        SkylinePredicate right;

        if (other instanceof SkylinePredicate) {
            right = (SkylinePredicate) other;
        }
        else {
            right = new SkylinePredicateWrapper(other);
        }
        return right;
    }

    class SkylinePredicateWrapper implements SkylinePredicate {

        private final Predicate<? super ServerRequest> delegate;

        public SkylinePredicateWrapper(Predicate<? super ServerRequest> delegate) {
            Assert.notNull(delegate, "delegate GatewayPredicate must not be null");
            this.delegate = delegate;
        }

        @Override
        public boolean test(ServerRequest exchange) {
            return this.delegate.test(exchange);
        }

        @Override
        public void accept(Visitor visitor) {
            if (delegate instanceof SkylinePredicate) {
                SkylinePredicate gatewayPredicate = (SkylinePredicate) delegate;
                gatewayPredicate.accept(visitor);
            }
        }

        @Override
        public String toString() {
            return this.delegate.getClass().getSimpleName();
        }

    }

    class NegateGatewayPredicate implements SkylinePredicate {

        private final SkylinePredicate predicate;

        public NegateGatewayPredicate(SkylinePredicate predicate) {
            Assert.notNull(predicate, "predicate GatewayPredicate must not be null");
            this.predicate = predicate;
        }

        @Override
        public boolean test(ServerRequest t) {
            return !this.predicate.test(t);
        }

        @Override
        public void accept(Visitor visitor) {
            predicate.accept(visitor);
        }

        @Override
        public String toString() {
            return String.format("!%s", this.predicate);
        }

    }

    class AndGatewayPredicate implements SkylinePredicate {

        private final SkylinePredicate left;

        private final SkylinePredicate right;

        public AndGatewayPredicate(SkylinePredicate left, SkylinePredicate right) {
            Assert.notNull(left, "Left GatewayPredicate must not be null");
            Assert.notNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(ServerRequest t) {
            return (this.left.test(t) && this.right.test(t));
        }

        @Override
        public void accept(Visitor visitor) {
            left.accept(visitor);
            right.accept(visitor);
        }

        @Override
        public String toString() {
            return String.format("(%s && %s)", this.left, this.right);
        }

    }

    class OrGatewayPredicate implements SkylinePredicate {

        private final SkylinePredicate left;

        private final SkylinePredicate right;

        public OrGatewayPredicate(SkylinePredicate left, SkylinePredicate right) {
            Assert.notNull(left, "Left GatewayPredicate must not be null");
            Assert.notNull(right, "Right GatewayPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean test(ServerRequest t) {
            return (this.left.test(t) || this.right.test(t));
        }

        @Override
        public void accept(Visitor visitor) {
            left.accept(visitor);
            right.accept(visitor);
        }

        @Override
        public String toString() {
            return String.format("(%s || %s)", this.left, this.right);
        }

    }
}
