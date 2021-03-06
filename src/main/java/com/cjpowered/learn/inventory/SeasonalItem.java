package com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item{

	private final int wantOnHand;
	final Season season;
	private final boolean isRestricted;
	private final int bulkAmt;
	
	public SeasonalItem(final int wantOnHand, final Season season, final boolean isRestricted, final int bulkAmt){
		this.wantOnHand = wantOnHand;
		this.season = season;
		this.isRestricted = isRestricted;
		this.bulkAmt = bulkAmt;
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		final Order maybeOrder;
		final int onHand = db.onHand(this);
		final boolean inSeason = season.equals(marketInfo.season(when));
		final boolean onSale = marketInfo.onSale(this);
		final int onOrder = db.onOrder(this);
		final int increasedStock = (int)(Math.ceil(wantOnHand * 1.1));
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
		
		if (inSeason && !onSale){
			deficit = wantOnHand * 2 - onHand - onOrder;
			
		} else if (inSeason && onSale){
			deficit = wantOnHand < 20 ? wantOnHand + 20 - onHand - onOrder: wantOnHand * 2 - onHand - onOrder;
 		}
		
		if (onHand + onOrder <= (deficit + onHand + onOrder) * 0.8  ){
			while (toOrder < deficit && toOrder + bulkAmt <= deficit){
				toOrder += bulkAmt;
			}
		}
		
		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
