package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	
	public StockedItem(int wantOnHand){
		this.wantOnHand = wantOnHand;
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		final Order maybeOrder;

		final int onHand = db.onHand(this);
		final int toOrder;
		final boolean onSale = marketInfo.onSale(this);
		if (onSale){
			toOrder = wantOnHand - onHand + 20;
		} else {
			toOrder = wantOnHand - onHand;
		}

		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
