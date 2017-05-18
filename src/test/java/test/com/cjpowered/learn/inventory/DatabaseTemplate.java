package test.com.cjpowered.learn.inventory;

import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;

public class DatabaseTemplate implements InventoryDatabase {

    @Override
    public int onHand(final Item item) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Item> stockItems() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

	@Override
	public int onOrder(Item item) {
		// TODO Auto-generated method stub
		return 0;
	}
}
