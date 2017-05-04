package com.cjpowered.learn.inventory;

import java.util.Objects;

/**
 * Order command: how much to buy of what item.
 */
public final class Order {

    /**
     * item to order
     */
    public final Item item;

    /**
     * how many to order
     */
    public final int quantity;

    public Order(final Item item, final int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Order)
            return this.item.equals(((Order) obj).item) && this.quantity == ((Order) obj).quantity;
        return false;

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.item, this.quantity);
    }

}
