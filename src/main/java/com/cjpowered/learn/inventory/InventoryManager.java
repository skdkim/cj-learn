package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.List;

public interface InventoryManager {
    /**
     * Compute inventory orders
     *
     * @param today
     *            effective day
     *
     * @return list of items and quantities to order
     */
    List<Order> getOrders(LocalDate today);
}