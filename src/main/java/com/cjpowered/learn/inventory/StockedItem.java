package com.cjpowered.learn.inventory;

import com.cjpowered.learn.marketing.Season;

public class StockedItem implements Item {
	
	private final int wantOnHand;
	private final Season season;
	
	public StockedItem(int wantOnHand, final Season season){
		this.wantOnHand = wantOnHand;
		this.season = season;
	}
	
	@Override
	public int wantOnHand(){
		return wantOnHand;
	}

	@Override
	public Season season() {
		return season;
	}
}
