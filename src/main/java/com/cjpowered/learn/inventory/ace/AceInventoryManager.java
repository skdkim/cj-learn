package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Order;

public final class AceInventoryManager implements InventoryManager {

     @Override
    public List<Order> getOrders(final LocalDate today) {
        return Collections.emptyList();
    }

}
