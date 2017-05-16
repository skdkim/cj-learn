package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
		
		Item item = new StockedItem(shouldHave);
		
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
    public void doNotRefillSingleStockOnSurplus(){
    	// given
		int onHand = 16;
		int shouldHave = 10;
		
		Item item = new StockedItem(shouldHave);
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
    public void doNotRefillStockOnPerfectInventoryCount(){
    	// given
		int onHand = 10;
		int shouldHave = 10;
		
		Item item = new StockedItem(shouldHave);
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
		
		Item itemA = new StockedItem(shouldHaveA);
		Item itemB = new StockedItem(shouldHaveB);
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
    public void refillMultipleStockOneValidForRefill(){
    	// given
		int onHandA = 10;
		int onHandB = 12;
		int shouldHaveA = 8;
		int shouldHaveB = 20;
		
		Item itemA = new StockedItem(shouldHaveA);
		Item itemB = new StockedItem(shouldHaveB);
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
		
		Item itemA = new StockedItem(shouldHaveA);
		Item itemB = new StockedItem(shouldHaveB);
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
		
		Item item = new StockedItem(shouldHave);
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
    public void doNotRefillSaleStockWithPerfectInventory(){
    	// given
		int onHand = 35;
		int shouldHave = 15;
		
		Item item = new StockedItem(shouldHave);
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
		
		Item itemA = new StockedItem(shouldHaveA);
		Item itemB = new StockedItem(shouldHaveB);
		
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
    public void refillSaleAndRegularStock(){
    	// given
		int onHandA = 30;
		int onHandB = 14;
		int shouldHaveA = 15;
		int shouldHaveB = 18;
		
		Item itemA = new StockedItem(shouldHaveA);
		Item itemB = new StockedItem(shouldHaveB);
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
		
		Item item = new SeasonalItem(shouldHave, season);
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
		
		Item item = new SeasonalItem(shouldHave, season);
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
}

