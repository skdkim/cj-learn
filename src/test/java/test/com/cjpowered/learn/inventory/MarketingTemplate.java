package test.com.cjpowered.learn.inventory;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

public class MarketingTemplate implements MarketingInfo {

    @Override
    public boolean onSale(final Item item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Season season(final LocalDate when) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
