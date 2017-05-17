package com.cjpowered.learn.marketing;

import java.time.LocalDate;

import com.cjpowered.learn.inventory.Item;

public interface MarketingInfo {
    /**
     * Fetch on-sale status.
     *
     * @param item
     *            item to query
     *
     * @return fetched value
     */
    boolean onSale(Item item);

    /**
     * Fetch the season
     *
     * @param when
     *            date to query
     *
     * @return fetched value
     */
    Season season(LocalDate when);
}
