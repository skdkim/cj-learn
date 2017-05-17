package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.StockedItem;
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

		
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
	    assertEquals(1, actualOrders.size());
	    assertEquals(shouldHave - onHand + 20, actualOrders.get(0).quantity);
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
		int onHandA = 34;
		int onHandB = 34;
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
    public void refillMixStockSaleAndRegular(){
    	// given
		int onHandA = 30;
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
    public void refillSingleSeasonalStock(){
    	// given
		int onHand = 10;
		int shouldHave = 16;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;

		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
    public void doNotRefillSingleSeasonalStock(){
    	// given
		int onHand = 40;
		int shouldHave = 20;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;

		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		int onHandA = 39;
		int shouldHaveA = 20;
		int onHandB = 21;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		final boolean isRestricted = false;
		int bulkAmt = 1;

		
		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new SeasonalItem(shouldHaveB, season, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		
		final Order expectedOrderA = new Order(itemA, (2 * 20) - 39);
		final Order expectedOrderB = new Order(itemB, (2 * 15) - 21);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndRegular(){
    	// given
		int onHandA = 39;
		int shouldHaveA = 20;
		int onHandB = 10;
		int shouldHaveB = 15;
		final Season seasonA = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;

		
		Item itemA = new SeasonalItem(shouldHaveA, seasonA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		
		final Order expectedOrderA = new Order(itemA, (2 * 20) - 39);
		final Order expectedOrderB = new Order(itemB, 15 - 10);
		HashSet<Order> expected = new HashSet<>();
		expected.add(expectedOrderA);
		expected.add(expectedOrderB);
		assertEquals(expected, new HashSet<>(actualOrders));
    }
    
    @Test
    public void refillMixStockSeasonalAndSale(){
    	// given
		int onHandA = 39;
		int shouldHaveA = 20;
		int onHandB = 10;
		int shouldHaveB = 15;
		final Season season = Season.Summer;
		boolean isRestricted = false;
		int bulkAmt = 1;

		
		Item itemA = new SeasonalItem(shouldHaveA, season, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		
		final Order expectedOrderA = new Order(itemA, (2 * 20) - 39);
		final Order expectedOrderB = new Order(itemB, (15 + 20) - 10);
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

		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		int onHand = 22;
		int shouldHave = 25;
		boolean isRestricted = true;
		int bulkAmt = 1;

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
		int onHandA = 22;
		int onHandB = 10;
		int shouldHaveA = 25;
		int shouldHaveB = 15;
		boolean isRestricted = true;
		int bulkAmt = 1;

		Item itemA = new StockedItem(shouldHaveA, isRestricted, bulkAmt);
		Item itemB = new StockedItem(shouldHaveB, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(itemA, onHandA);
		store.put(itemB, onHandB);
		final InventoryDatabase db = new FakeDatabase(store);
		
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

		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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

		final Season season = Season.Summer;
		
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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

		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
    public void refillBulkItem(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 4;
				
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
	    assertEquals(8, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSaleStock(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 4;
				
		Item item = new StockedItem(shouldHave, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
	    assertEquals(28, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalStock(){
    	// given
		int onHand = 3;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 15;
		Season season = Season.Summer;
				
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
	    assertEquals(30, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
    
    @Test
    public void refillBulkSeasonalAndSaleStock(){
    	// given
		int onHand = 5;
		int shouldHave = 10;
		boolean isRestricted = false;
		int bulkAmt = 8;
		Season season = Season.Summer;
				
		Item item = new SeasonalItem(shouldHave, season, isRestricted, bulkAmt);
		
		final HashMap<Item, Integer> store = new HashMap<>();
		store.put(item, onHand);
		final InventoryDatabase db = new FakeDatabase(store);
		
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
	    assertEquals(32, actualOrders.get(0).quantity);
	    assertEquals(item, actualOrders.get(0).item);
    }
}

