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
		int toOrder = 0;
		 int deficit = wantOnHand - onHand;
		final boolean onSale = marketInfo.onSale(this);
		
		if (isRestricted){
			if(when.getDayOfMonth() != 1){
				return new Order(this, 0);
			}
		}
		
		if (onSale){
			deficit = wantOnHand + 20 - onHand;
			while(toOrder < deficit){
				toOrder += bulkAmt;
			}
		} else {
			// work in here for bulk amt
			while(toOrder < deficit){
				toOrder += bulkAmt;
			}
		}

		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
