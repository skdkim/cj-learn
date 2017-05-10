package test.com.cjpowered.learn.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;

public class FakeDatabase implements InventoryDatabase{

	private final Map<Item, Integer> dataStore;
	
	public FakeDatabase(final Map<Item, Integer> dataStore){
		this.dataStore = dataStore;
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

}
