package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	private final boolean isRestricted;
	private final int bulkAmt;
	
	public StockedItem(int wantOnHand, final boolean isRestricted, final int bulkAmt){
		this.wantOnHand = wantOnHand;
		this.isRestricted = isRestricted;
		this.bulkAmt = bulkAmt;
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		final Order maybeOrder;
		final int onHand = db.onHand(this);
		final int deficit;
		final boolean onSale = marketInfo.onSale(this);
		int toOrder = 0;
		final int onOrder = db.onOrder(this);
		
		if (isRestricted){
			if(when.getDayOfMonth() != 1){
				return new Order(this, 0);
			}
		}
		
		if (onSale){
			deficit = wantOnHand + 20 - onHand - onOrder;
			while(toOrder < deficit  && toOrder + bulkAmt <= deficit){
				toOrder += bulkAmt;
			}
		} else {
			deficit = wantOnHand - onHand - onOrder;
			while(toOrder < deficit && toOrder + bulkAmt <= deficit){
				toOrder += bulkAmt;
			}
		}

		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
