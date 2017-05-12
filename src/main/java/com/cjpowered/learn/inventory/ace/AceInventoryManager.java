package com.cjpowered.learn.inventory.ace;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.marketing.MarketingInfo;

public final class AceInventoryManager implements InventoryManager {

	private final InventoryDatabase database;
	private final MarketingInfo marketInfo;
	
	public AceInventoryManager(InventoryDatabase database, MarketingInfo marketInfo){
		this.database = database;
		this.marketInfo = marketInfo;
	}
	
     @Override
    public List<Order> getOrders(final LocalDate today) {
    	 	final List<Order> orders = new ArrayList<>();
    	 	final List<Item> items = database.stockItems();
    	 	
    	 	for (Item item : items){
//    	 		final Optional<Order> order = item.createOrder(today, database, marketInfo);
    	 		final Order order = item.createOrder(today, database, marketInfo);
    	 		if (order.quantity > 0){
    	 			orders.add(order);
    	 		}
//    	 		order.quantity == 0 ? : orders.add(order);
//    	 		orders.add(order);
//    	 		order.ifPresent(orders.add(order));
    	 	}
    	 	
//    	 	for(Item item : items){
//    	 		final int onHand = database.onHand(item);
//    	 		final boolean onSale = marketInfo.onSale(item);
//    	 		final int toOrder;
//    	 		final boolean inSeason = marketInfo.season(today).equals(item.season());
//    	 		
//    	 		if (onSale){
//    	 			toOrder = item.wantOnHand() - onHand + 20;
//    	 		} else if(inSeason){
//    	 			toOrder = item.wantOnHand() * 2 - onHand;
//    	 		} else {
//    	 			toOrder = item.wantOnHand() - onHand;
//    	 		}
//    	 		if (toOrder > 0){
//        	 		final Order order = new Order(item, toOrder);
//        	 		orders.add(order);
//    	 		}
//    	 	}
    	 	return orders;
    }
}
