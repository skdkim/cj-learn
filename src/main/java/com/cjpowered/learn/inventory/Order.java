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

      /**
       * warehouse to which units should be shipped
       */
      public final Warehouse warehouse;
  
     public Order(final Item item, final int quantity, final Warehouse warehouse) {
     this.item = item;
        this.quantity = quantity;
	    this.warehouse = warehouse;
	 }
   
       @Deprecated
       public Order(final Item item, final int quantity) {
           this(item, quantity, Warehouse.home());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        else if (obj instanceof Order)
            return this.item.equals(((Order) obj).item) && this.quantity == ((Order) obj).quantity
                    && this.warehouse == ((Order) obj).warehouse;        return false;
    }

    @Override
    public int hashCode() {
       return Objects.hash(this.item, this.quantity, this.warehouse);
    }
}
