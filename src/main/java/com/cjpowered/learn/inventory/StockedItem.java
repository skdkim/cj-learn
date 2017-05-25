package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final HashMap<Warehouse, Integer> warehouseWantOnHand;
	private final boolean isRestricted;
	private final int bulkAmt;
	
	public StockedItem(final int wantOnHand, final boolean isRestricted, final int bulkAmt){
		HashMap<Warehouse, Integer> defaultWantOnHand = new HashMap<Warehouse, Integer>();
		defaultWantOnHand.put(Warehouse.home(), wantOnHand);
		this.warehouseWantOnHand = defaultWantOnHand;
		this.isRestricted = isRestricted;
		this.bulkAmt = bulkAmt;
	}
	
	public StockedItem(final HashMap warehouseWantOnHand, final boolean isRestricted, final int bulkAmt){
		this.warehouseWantOnHand = warehouseWantOnHand;
		this.isRestricted = isRestricted;
		this.bulkAmt = bulkAmt;
		
//		int a = 1;
//		Integer x = new Integer(a);
//		
//		int z = x.intValue();
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		int wantOnHand = 0;
		for (Map.Entry<Warehouse, Integer> next : warehouseWantOnHand.entrySet()){
			wantOnHand = next.getValue();
		}
		
//		for(Warehouse warehouse : warehouseWantOnHand.keySet()){
//			Integer onHand = warehouseWantOnHand.get(warehouse);
//		}
		
		final Order maybeOrder;
		final int onHand = db.onHand(this);
		final boolean onSale = marketInfo.onSale(this);
		final int onOrder = db.onOrder(this);
		final int increasedStock = (int) (Math.ceil(wantOnHand * 1.10));
		int deficit = 0;
		int toOrder = 0;
		
		if (onHand == 0){
			db.setRequiredOnHand(this, increasedStock);	
		}
		
		if (isRestricted){
			if(when.getDayOfMonth() != 1){
				return new Order(this, 0);
			}
		}

		if (onSale){
			deficit = wantOnHand + 20 - onHand - onOrder;
		} else {
			deficit = wantOnHand - onHand - onOrder;
		}
		
		if (onHand + onOrder <= (deficit + onHand + onOrder) * 0.8 ){
			while(toOrder < deficit && toOrder + bulkAmt <= deficit){
				toOrder += bulkAmt;
			}			
		}

		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
