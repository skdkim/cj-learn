package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.SeasonalItem;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.Warehouse;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;
import com.cjpowered.learn.marketing.MarketingInfo;
import com.cjpowered.learn.marketing.Season;

/*
 * We need to keep items in stock to prevent back orders. See the README.md
 * for the requirements.
 *
 */

public class InventoryTest {

    @Test
    public void doNotRefillStockWhenNoOrders() {
    	// given
		final InventoryDatabase db = new DatabaseTemplate() {
			
			@Override
			public List<Item> stockItems(){
				return Collections.emptyList();
			}
		};
    	
        final LocalDate today = LocalDate.now();
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return null;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		
        // when
        final List<Order> actualOrders = im.getOrders(today);

        // then
        assertTrue(actualOrders.isEmpty());
    }
    
    @Test
    public void refillSingleStock(){
    	// given
		int onHand = 10;
		int shouldHave = 16;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(item, actualOrders.get(0).item);
	    assertEquals(shouldHave - onHand, actualOrders.get(0).quantity);
    }
        
    @Test
    public void doNotRefillSingleStockOverEightyPercent(){
    	// given
		int onHand = 10;
		int shouldHave = 11;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void doNotRefillSingleStockSurplus(){
    	// given
		int onHand = 16;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return onHand;
			}
			
			@Override
			public List<Item> stockItems(){
				return Collections.singletonList(item);
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
	    assertTrue(actualOrders.isEmpty());
    }
    
    @Test
    public void doNotRefillStockPerfectCount(){
    	// given
		int onHand = 10;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return onHand;
			}
			
			@Override
			public List<Item> stockItems(){
				return Collections.singletonList(item);
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
	    assertTrue(actualOrders.isEmpty());
    }
    
    @Test
    public void refillMultipleStock(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 16;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return item == itemA ? onHandA : onHandB;
			}
			
			@Override
			public List<Item> stockItems(){
				List<Item> items = new ArrayList<Item>();
				items.add(itemA);
				items.add(itemB);
				return items;
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());
        assertEquals(itemA, actualOrders.get(0).item);
        assertEquals(itemB, actualOrders.get(1).item);
        assertEquals(shouldHaveA - onHandA, actualOrders.get(0).quantity);
        assertEquals(shouldHaveB - onHandB, actualOrders.get(1).quantity);
    }
    
    @Test
    public void doNotRefillMultipleStockOverEightyPercent(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 12;
		int shouldHaveB = 14;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
				
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMultipleStockOneOnly(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 8;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return item == itemA ? onHandA : onHandB;
			}
			
			@Override
			public List<Item> stockItems(){
				List<Item> items = new ArrayList<Item>();
				items.add(itemA);
				items.add(itemB);
				return items;
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(1, actualOrders.size());
        assertEquals(itemB, actualOrders.get(0).item);
        assertEquals(shouldHaveB - onHandB, actualOrders.get(0).quantity);
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void doNotRefillInvalidStockOnMultipleOrder(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 8;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return item == itemA ? onHandA : onHandB;
			}
			
			@Override
			public List<Item> stockItems(){
				List<Item> items = new ArrayList<Item>();
				items.add(itemA);
				items.add(itemB);
				return items;
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
    	Item nonExistantItem = actualOrders.get(1).item;
    }
    
    @Test
    public void refillSaleStock(){
    	// given
		int onHand = 10;
		int shouldHave = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave - onHand + 20, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillSaleStockOverEightyPercent(){
    	// given
		int onHand = 33;
		int shouldHave = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void doNotRefillSaleStockPerfectCount(){
    	// given
		int onHand = 35;
		int shouldHave = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return onHand;
			}
			
			@Override
			public List<Item> stockItems(){
				return Collections.singletonList(item);
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMultipleSaleStock(){
    	// given
		int onHandA = 24;
		int onHandB = 24;
		int shouldHaveA = 15;
		int shouldHaveB = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return item == itemA ? onHandA : onHandB;
			}
			
			@Override
			public List<Item> stockItems(){
				List<Item> items = new ArrayList<Item>();
				items.add(itemA);
				items.add(itemB);
				return items;
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());
        assertEquals(itemA, actualOrders.get(0).item);
        assertEquals(itemB, actualOrders.get(1).item);
        assertEquals(shouldHaveA - onHandA + 20, actualOrders.get(0).quantity);
        assertEquals(shouldHaveB - onHandB + 20, actualOrders.get(1).quantity);
    }
    
    @Test
    public void doNotRefillMultipleSaleStockOverEightyPercent(){
    	// given
		int onHandA = 34;
		int onHandB = 34;
		int shouldHaveA = 15;
		int shouldHaveB = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMixStockSaleAndRegular(){
    	// given
		int onHandA = 25;
		int onHandB = 14;
		int shouldHaveA = 15;
		int shouldHaveB = 18;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final InventoryDatabase db = new DatabaseTemplate() {
			@Override
			public int onHand(Item item){
				return item == itemA ? onHandA : onHandB;
			}
			
			@Override
			public List<Item> stockItems(){
				List<Item> items = new ArrayList<Item>();
				items.add(itemA);
				items.add(itemB);
				return items;
			}
		};
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return item == itemA ? true : false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());
        assertEquals(itemA, actualOrders.get(0).item);
        assertEquals(itemB, actualOrders.get(1).item);
        assertEquals(shouldHaveA - onHandA + 20, actualOrders.get(0).quantity);
        assertEquals(shouldHaveB - onHandB, actualOrders.get(1).quantity);
    }
    
    @Test
    public void doNotRefillMixStockSaleAndRegularOverEightyPercent(){
    	// given
		int onHandA = 25;
		int onHandB = 14;
		int shouldHaveA = 15;
		int shouldHaveB = 18;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
			
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, 0);
		currOrders.put(itemB, 0);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return item == itemA ? true : false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());

		final Order expectedOrderA = new Order(itemA, (20 + shouldHaveA) - onHandA);
		final Order expectedOrderB = new Order(itemB, (shouldHaveB) - onHandB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillSingleSeasonalStock(){
    	// given
		int onHand = 10;
		int shouldHave = 16;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(item, actualOrders.get(0).item);
	    assertEquals((shouldHave * 2) - onHand, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillSingleSeasonalStockOverEightyPercent(){
    	// given
		int onHand = 31;
		int shouldHave = 16;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }

    @Test
    public void doNotRefillSingleSeasonalStock(){
    	// given
		int onHand = 40;
		int shouldHave = 20;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMultipleSeasonalStock(){
    	// given
		int onHandA = 30;
		int shouldHaveA = 20;
		int onHandB = 12;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;

		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new SeasonalItem(shouldHaveB, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA);
		final Order expectedOrderB = new Order(itemB, (2 * shouldHaveB) - onHandB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndRegular(){
    	// given
		int onHandA = 20;
		int shouldHaveA = 20;
		int onHandB = 10;
		int shouldHaveB = 15;
		final Season seasonA = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;

		Item itemA = new SeasonalItem(shouldHaveA, seasonA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA);
		final Order expectedOrderB = new Order(itemB, shouldHaveB - onHandB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndSale(){
    	// given
		int onHandA = 20;
		int shouldHaveA = 20;
		int onHandB = 10;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;

		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return item == itemB ? true : false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA);
		final Order expectedOrderB = new Order(itemB, (20 + shouldHaveB) - onHandB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillSeasonalOnSaleStockWithSaleRefillHigher(){
    	// given
		int onHand = 5;
		int shouldHave = 6;
		final boolean isRestricted = false;
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());	    
	    assertEquals((shouldHave + 20) - onHand, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillSeasonalOnSaleStockWithSeasonRefillHigher(){
    	// given
		int onHand = 22;
		int shouldHave = 25;
		final boolean isRestricted = false;
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());	    
	    assertEquals((shouldHave * 2) - onHand, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void doNotRefillDateRestrictedRegularStock(){
    	// given
		int onHand = 22;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 2);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillDateRestrictedRegularStock(){
    	// given
		int onHand = 19;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave - onHand, actualOrders.get(0).quantity);
    }
    
    @Test
    public void refillMultipleDateRestrictedRegularStock(){
    	// given
		int onHandA = 19;
		int onHandB = 10;
		int shouldHaveA = 25;
		int shouldHaveB = 15;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 0;

		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());
	    
		final Order expectedOrderA = new Order(itemA, shouldHaveA - onHandA);
		final Order expectedOrderB = new Order(itemB, shouldHaveB - onHandB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillDateRestrictedSaleStock(){
    	// given
		int onHand = 22;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave + 20 - onHand, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillDateRestrictedSeasonalStock(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 0;

		final Season season = Season.Summer;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 2);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillDateRestrictedSeasonalStock(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = true;
		
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave * 2 - onHand, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillMultipleBulkStockNoOverflow(){
    	// given
		int onHandA = 3;
		int shouldHaveA = 10;
		int onHandB = 5;
		int shouldHaveB = 10;
		boolean isRestricted = false;
		int bulkAmtA = 4;
		int bulkAmtB = 3;
		int onOrder = 0;

		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmtA);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmtB);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrder);
		currOrders.put(itemB, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());
	    
		final Order expectedOrderA = new Order(itemA, 4);
		final Order expectedOrderB = new Order(itemB, 3);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
// ******************************    THE GREAT DIVIDE!!!! (TESTING FOR ONORDER)    ******************************
    @Test
    public void refillSingleStockWithConcurrentOrder(){
    	// given
		int onHand = 8;
		int shouldHave = 16;
		int onOrder = 3;
		boolean isRestricted = false;
		int bulkAmt = 1;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(item, actualOrders.get(0).item);
	    assertEquals(shouldHave - onHand - onOrder, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillSingleStockSurplusWithConcurrentOrder(){
    	// given
		int onHand = 16;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
	    assertTrue(actualOrders.isEmpty());
    }
    
    @Test
    public void doNotRefillStockPerfectCountWithConcurrentOrder(){
    	// given
		int onHand = 10;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
	    assertTrue(actualOrders.isEmpty());
    }
    
    @Test
    public void refillMultipleStockWithConcurrentOrder(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 16;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());
		final Order expectedOrderA = new Order(itemA, (shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, (shouldHaveB) - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillOnlyOneOfMultipleStockWithConcurrentOrder(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 8;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		
		// how do you know which item is on order???
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(1, actualOrders.size());
        assertEquals(itemB, actualOrders.get(0).item);
        assertEquals(shouldHaveB - onHandB - onOrderB, actualOrders.get(0).quantity);
    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void doNotRefillInvalidStockOnMultipleOrderWithConcurrentOrder(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 8;
		int shouldHaveB = 20;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 1;
		int onOrderB = 2;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		
		// how do you know which item is on order???
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
    	Item nonExistantItem = actualOrders.get(1).item;
    }
    
    @Test
    public void refillSaleStockWithConcurrentOrder(){
    	// given
		int onHand = 10;
		int shouldHave = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave - onHand + 20 - onOrder, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillSaleStockPerfectCountWithConcurrentOrder(){
    	// given
		int onHand = 35;
		int shouldHave = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMultipleSaleStockWithConcurrentOrder(){
    	// given
		int onHandA = 20;
		int onHandB = 20;
		int shouldHaveA = 15;
		int shouldHaveB = 15;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());
        
		final Order expectedOrderA = new Order(itemA, (20 +  shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, (20 +  shouldHaveB) - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSaleAndRegularWithConcurrentOrder(){
    	// given
		int onHandA = 20;
		int onHandB = 4;
		int shouldHaveA = 15;
		int shouldHaveB = 18;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;
		
		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){
			@Override
			public boolean onSale(Item item) {
				return item == itemA ? true : false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
        assertEquals(2, actualOrders.size());

		final Order expectedOrderA = new Order(itemA, (20 + shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, (shouldHaveB) - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillSingleSeasonalStockWithConcurrentOrder(){
    	// given
		int onHand = 10;
		int shouldHave = 16;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(item, actualOrders.get(0).item);
	    assertEquals((shouldHave * 2) - onHand - onOrder, actualOrders.get(0).quantity);
    }
    

    @Test
    public void doNotRefillSingleSeasonalStockWithConcurrentOrder(){
    	// given
		int onHand = 40;
		int shouldHave = 20;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 2;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillMultipleSeasonalStockWithConcurrentOrder(){
    	// given
		int onHandA = 17;
		int shouldHaveA = 20;
		int onHandB = 11;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;

		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new SeasonalItem(shouldHaveB, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, (2 * shouldHaveB) - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndRegularWithConcurrentOrder(){
    	// given
		int onHandA = 17;
		int shouldHaveA = 20;
		int onHandB = 8;
		int shouldHaveB = 15;
		final Season seasonA = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;

		Item itemA = new SeasonalItem(shouldHaveA, seasonA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, shouldHaveB - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndSaleWithConcurrentOrder(){
    	// given
		int onHandA = 17;
		int shouldHaveA = 20;
		int onHandB = 8;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;

		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return item == itemB ? true : false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());	    
		
		final Order expectedOrderA = new Order(itemA, (2 * shouldHaveA) - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, (20 + shouldHaveB) - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillSeasonalOnSaleStockWithSaleRefillHigherWithConcurrentOrder(){
    	// given
		int onHand = 5;
		int shouldHave = 6;
		final boolean isRestricted = false;
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());	    
	    assertEquals((shouldHave + 20) - onHand - onOrder, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillSeasonalOnSaleStockWithSeasonRefillHigherWithConcurrentOrder(){
    	// given
		int onHand = 12;
		int shouldHave = 25;
		final boolean isRestricted = false;
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());	    
	    assertEquals((shouldHave * 2) - onHand - onOrder, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void doNotRefillDateRestrictedRegularStockWithConcurrentOrder(){
    	// given
		int onHand = 22;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 2);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillDateRestrictedRegularStockWithConcurrentOrder(){
    	// given
		int onHand = 18;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave - onHand - onOrder, actualOrders.get(0).quantity);
    }
    
    @Test
    public void refillMultipleDateRestrictedRegularStockWithConcurrentOrder(){
    	// given
		int onHandA = 12;
		int onHandB = 5;
		int shouldHaveA = 25;
		int shouldHaveB = 15;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrderA = 2;
		int onOrderB = 3;

		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());
	    
		final Order expectedOrderA = new Order(itemA, shouldHaveA - onHandA - onOrderA);
		final Order expectedOrderB = new Order(itemB, shouldHaveB - onHandB - onOrderB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillDateRestrictedSaleStockWithConcurrentOrder(){
    	// given
		int onHand = 22;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave + 20 - onHand - onOrder, actualOrders.get(0).quantity);
    }
    
    @Test
    public void doNotRefillDateRestrictedSeasonalStockWithConcurrentOrder(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = true;
		int bulkAmt = 1;
		int onOrder = 2;

		final Season season = Season.Summer;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 2);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void refillDateRestrictedSeasonalStockWithConcurrentOrder(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = true;
		
		final Season season = Season.Summer;
		int bulkAmt = 1;
		int onOrder = 2;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave * 2 - onHand - onOrder, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkItemWithConcurrentOrderNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 4;
		int onOrder = 2;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(4, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillMultipleBulkStockWithConcurrentOrderNoOverflow(){
    	// given
		int onHandA = 3;
		int shouldHaveA = 10;
		int onHandB = 5;
		int shouldHaveB = 10;
		boolean isRestricted = false;
		int bulkAmtA = 4;
		int bulkAmtB = 3;
		int onOrderA = 2;
		int onOrderB = 3;

		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmtA);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmtB);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(itemA, onOrderA);
		currOrders.put(itemB, onOrderB);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(4, actualOrders.get(0).quantity);
	    assertEquals(itemA, actualOrders.get(0).item);
	    
//		final Order expectedOrderA = new Order(itemA, 8);
//		final Order expectedOrderB = new Order(itemB, 3);
//		HashSet<Order> expected = new HashSet<>();
//		expected.add(expectedOrderA);
//		expected.add(expectedOrderB);
//		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillBulkSaleStockWithConcurrentOrderNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 4;
		int onOrder = 2;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(24, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalStockWithConcurrentOrder(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 15;
		Season season = Season.Summer;
		int onOrder = 2;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(15, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalAndSaleStockWithConcurrentOrder(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 8;
		Season season = Season.Summer;
		int onOrder = 2;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(16, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    
    
    @Test
    public void refillBulkStockNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 3;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(6, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSaleStockNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 16;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(16, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalStockNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 16;
		int onOrder = 0;
		Season season = Season.Summer;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(16, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalSaleStockNoOverflow(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 16;
		int onOrder = 0;
		Season season = Season.Summer;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(16, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void increaseStockLevelOnStockOutage(){
    	// given
		int onHand = 0;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		
		InventoryDatabase db = mock(FakeDatabase.class, withSettings()
				.useConstructor(store, currOrders).defaultAnswer(CALLS_REAL_METHODS));
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	im.getOrders(today);
		    	
    	// then
    	Mockito.verify(db).setRequiredOnHand(item, 11);
    }
    
    @Test
    public void increaseSeasonalStockLevelOnStockOutage(){
    	// given
		int onHand = 0;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		Season season = Season.Summer;

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		
		InventoryDatabase db = mock(FakeDatabase.class, withSettings()
				.useConstructor(store, currOrders).defaultAnswer(CALLS_REAL_METHODS));
		
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	im.getOrders(today);
		    	
    	// then
    	Mockito.verify(db).setRequiredOnHand(item, 11);
    }
    
    @Test
    public void increaseSaleStockLevelOnStockOutage(){
    	// given
		int onHand = 0;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		
		InventoryDatabase db = mock(FakeDatabase.class, withSettings()
				.useConstructor(store, currOrders).defaultAnswer(CALLS_REAL_METHODS));
	
		final MarketingInfo mrktInfo = new MarketingTemplate(){
			@Override
			public boolean onSale(Item item) {
				return true;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Summer;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.of(2017, 1, 1);
	
    	// when
    	im.getOrders(today);
		    	
    	// then
    	Mockito.verify(db).setRequiredOnHand(item, 11);
    }
    
    @Test
    public void refillSingleStockFromSingleNonDefaultWarehouse(){
    	// given
		int onHand = 10;
		int shouldHave = 16;
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		Warehouse warehouse = Warehouse.Ashford;
		
		final HashMap<Warehouse, Integer> warehouseReqs = new HashMap<>();
		warehouseReqs.put(warehouse, shouldHave);
		
		Item item = new StockedItem(warehouseReqs, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(1, actualOrders.size());
	    assertEquals(item, actualOrders.get(0).item);
	    assertEquals(shouldHave - onHand, actualOrders.get(0).quantity);
    }
    
    private InventoryDatabase fakeDatabaseForDefaultWarehouse(final Map<Item, Integer> dataStore, final Map<Item, Integer> onOrder){
    	Map<Warehouse, Map<Item, Integer>> allData = new HashMap<>();
    	allData.put(Warehouse.home(), dataStore);
    	
    	return new FakeDatabase(allData, onOrder);
    }

    
    @Test
    public void refillSingleStockFromMultipleNonDefaultWarehouse(){
    	// given
		int onHandA = 10;
		int onHandB = 9;
		int shouldHaveA = 16;
		int shouldHaveB = 12; 
		boolean isRestricted = false;
		int bulkAmt = 1;
		int onOrder = 0;
		Warehouse warehouseA = Warehouse.Ashford;
		Warehouse warehouseB = Warehouse.Peculiar;
		
		final HashMap<Warehouse, Integer> warehouseReqs = new HashMap<>();
		warehouseReqs.put(warehouseA, shouldHaveA);
		warehouseReqs.put(warehouseB, shouldHaveB);
		
		Item item = new StockedItem(warehouseReqs, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHandA);
		store.put(item, onHandB);

		final HashMap<Item, Integer> currOrders = new HashMap<>();
		currOrders.put(item, onOrder);
		final InventoryDatabase db = fakeDatabaseForDefaultWarehouse(store, currOrders);
		
		final MarketingInfo mrktInfo = new MarketingInfo(){

			@Override
			public boolean onSale(Item item) {
				return false;
			}

			@Override
			public Season season(LocalDate when) {
				return Season.Spring;
			}
		};
		
		final InventoryManager im = new AceInventoryManager(db, mrktInfo);
		final LocalDate today = LocalDate.now();
	
    	// when
    	final List<Order> actualOrders = im.getOrders(today);
		
    	// then
	    assertEquals(2, actualOrders.size());
	    
		final Order expectedOrderA = new Order(item, 6, warehouseA);
		final Order expectedOrderB = new Order(item, 3, warehouseB);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
}



