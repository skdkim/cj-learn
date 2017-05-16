package test.com.cjpowered.learn.inventory;

import java.time.LocalDate;
import java.util.Optional;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class SeasonalItem implements Item{

	private final int wantOnHand;
	final Season season;
	
	public SeasonalItem(final int wantOnHand, final Season season){
		this.wantOnHand = wantOnHand;
		this.season = season;
	}

	@Override
	public Order createOrder(final LocalDate when, final InventoryDatabase db, final MarketingInfo marketInfo) {
		final Order maybeOrder;
		
		final int onHand = db.onHand(this);
		final int toOrder;
		final boolean inSeason = season.equals(marketInfo.season(when));
		final boolean onSale = marketInfo.onSale(this);
		if (inSeason && !onSale){
			toOrder = wantOnHand * 2 - onHand;
		} else if (inSeason && onSale){
			toOrder = wantOnHand + 20 - onHand;
 		} else {
			toOrder = wantOnHand - onHand;
		}
		maybeOrder = new Order(this, toOrder);
		return maybeOrder;
	}
}
