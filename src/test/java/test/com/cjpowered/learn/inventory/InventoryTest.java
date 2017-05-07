package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Order;
import com.cjpowered.learn.inventory.StockedItem;
import com.cjpowered.learn.inventory.ace.AceInventoryManager;

/*
 * We need to keep items in stock to prevent back orders. See the README.md
 * for the requirements.
 *
 */

public class InventoryTest {

    @Test
    public void whenNoStockItemsDoNotOrder() {
    	// given
		final InventoryDatabase db = new DatabaseTemplate() {
			
			@Override
			public List<Item> stockItems(){
				// TODO Auto-generate method stub
				return Collections.emptyList();
//				return super.stockItems();
			}
		};
    	
        final LocalDate today = LocalDate.now();
        final InventoryManager im = new AceInventoryManager(db);

        // when
        final List<Order> actual = im.getOrders(today);

        // then
        assertTrue(actual.isEmpty());

    }
    
    @Test
    public void orderEnoughStock(){
	    	// given
    		int onHand = 10;
    		int shouldHave = 16;
    		
    		Item item = new StockedItem(shouldHave);
    		final InventoryDatabase db = new DatabaseTemplate() {
    			@Override
    			public int onHand(Item item){
    				// TODO Auto-generate method stub
    				return onHand;
    			}
    			
    			@Override
    			public List<Item> stockItems(){
    				// TODO Auto-generate method stub
    				return Collections.singletonList(item);
    			}
    		};
    		
    		final InventoryManager im = new AceInventoryManager(db);
    		final LocalDate today = LocalDate.now();
    	
	    	// when
	    	final List<Order> actual = im.getOrders(today);
    		
	    	// then
	    assertEquals(1, actual.size());
	    assertEquals(item, actual.get(0).item);
	    assertEquals(shouldHave - onHand, actual.get(0).quantity);
    }
    
    @Test
    public void orderStockWithSurplus(){
	    	// given
    		int onHand = 16;
    		int shouldHave = 10;
    		
    		Item item = new StockedItem(shouldHave);
    		final InventoryDatabase db = new DatabaseTemplate() {
    			@Override
    			public int onHand(Item item){
    				// TODO Auto-generate method stub
    				return onHand;
    			}
    			
    			@Override
    			public List<Item> stockItems(){
    				// TODO Auto-generate method stub
    				return Collections.singletonList(item);
    			}
    		};
    		
    		final InventoryManager im = new AceInventoryManager(db);
    		final LocalDate today = LocalDate.now();
    	
	    	// when
	    	final List<Order> actualOrders = im.getOrders(today);
    		
	    	// then
	    assertEquals(0, actualOrders.size());
    }
    
    @Test
    public void orderStockWithEqualInventory(){
	    	// given
    		int onHand = 10;
    		int shouldHave = 10;
    		
    		Item item = new StockedItem(shouldHave);
    		final InventoryDatabase db = new DatabaseTemplate() {
    			@Override
    			public int onHand(Item item){
    				// TODO Auto-generate method stub
    				return onHand;
    			}
    			
    			@Override
    			public List<Item> stockItems(){
    				// TODO Auto-generate method stub
    				return Collections.singletonList(item);
    			}
    		};
    		
    		final InventoryManager im = new AceInventoryManager(db);
    		final LocalDate today = LocalDate.now();
    	
	    	// when
	    	final List<Order> actualOrders = im.getOrders(today);
    		
	    	// then
		    assertEquals(0, actualOrders.size());
    }
}
