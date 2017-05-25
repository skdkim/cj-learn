package test.com.cjpowered.learn.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Warehouse;

public class FakeDatabase implements InventoryDatabase{

	private final Map<Warehouse, Map<Item, Integer>> dataStore;
	private final Map<Item, Integer> onOrder;
	
	public FakeDatabase(final Map<Warehouse, Map<Item, Integer>> dataStore, final Map<Item, Integer> onOrder){
		this.dataStore = dataStore;
		this.onOrder = onOrder;
	}
	
	@Override
	public int onHand(Item item) {
		return dataStore.get(item);
	}

	@Override
	public List<Item> stockItems() {
		// TODO Auto-generated method stub
		final Set<Item> keys = dataStore.keySet();
		return new ArrayList<>(keys);
	}

	@Override
	public int onOrder(Item item) {
		// TODO Auto-generated method stub
		return onOrder.get(item);
	}

	@Override
	public void setRequiredOnHand(Item item, int newAmount) {
		// TODO Auto-generated method stub
	}

	@Override
	public int onOrder(Item item, Warehouse warehouse) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRequiredOnHand(Item item, Warehouse warehouse, int newAmount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int onHand(Item item, Warehouse warehouse) {
		// TODO Auto-generated method stub
		return 0;
	}
}
