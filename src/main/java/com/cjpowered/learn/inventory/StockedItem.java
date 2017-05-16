package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	private final boolean isRestricted;
	
	public StockedItem(int wantOnHand, final boolean isRestricted){
		this.wantOnHand = wantOnHand;
		this.isRestricted = isRestricted;
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		final Order maybeOrder;

		final int onHand = db.onHand(this);
		final int toOrder;
		final boolean onSale = marketInfo.onSale(this);
		
		if (isRestricted){
			if(when.getDayOfMonth() != 1){
				return new Order(this, 0);
			}
		}
		
		if (onSale){
			toOrder = wantOnHand - onHand + 20;
		} else {
			toOrder = wantOnHand - onHand;
		}

		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
