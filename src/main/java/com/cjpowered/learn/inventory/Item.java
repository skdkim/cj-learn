package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public interface Item {
	Optional<Order> createOrder(LocalDate when, InventoryDatabase db, MarketingInfo marketInfo);
}
