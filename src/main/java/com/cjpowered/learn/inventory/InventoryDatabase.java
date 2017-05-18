package com.cjpowered.learn.inventory;

import java.util.List;

public interface InventoryDatabase {
    /**
     * Fetch number on-hand.
     *
     * @param item
     *            item to query
     *
     * @return fetched value
     */
    int onHand(Item item);

    /**
     * Fetch list of all stocked items.
     *
     * @return fetched value
     */
    List<Item> stockItems();
    
    /**
     * Fetch the number on order.
     * 
     * @param item
     *            item to query. The item instance must have been returned by
     *            the same implementation on which this method is called.
     *
     * @return fetched value
     */
    int onOrder(Item item);
}
