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
     * Change the required on-hand amount for an item.
     * 
     * @param item item to change
     * 
     * @param newAmount new nominal stock level
     */
    void setRequiredOnHand(Item item, int newAmount);
    
    
    /**
     * Fetch the number on order.
     *
     * @return fetched value
     */
    int onOrder(Item item, Warehouse warehouse);
    
    
    @Deprecated
    int onOrder(Item item);

   /**
    * Change the required on-hand amount for an item.
    *
    * @param item
    *            item to change
    *
    * @param warehouse
    *            warehouse to query. The warehouse instance must have been
    *            returned by the same implementation on which this method is
    *            called.
    *            
    * @param newAmount
    *            new nominal stock level
    */
   void setRequiredOnHand(Item item, Warehouse warehouse, int newAmount);

   /**
    * Fetch number on-hand.
    *
    * @param item
    *            item to query. The item instance must have been returned by
    *            the same implementation on which this method is called.
    *
    *
    * @param warehouse
    *            warehouse to query. The warehouse instance must have been
    *            returned by the same implementation on which this method is
    *            called.
    *
    * @return fetched value
    */
   int onHand(Item item, Warehouse warehouse);
}
