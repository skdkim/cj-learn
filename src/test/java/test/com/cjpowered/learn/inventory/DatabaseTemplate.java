package test.com.cjpowered.learn.inventory;

import java.util.List;

import com.cjpowered.learn.inventory.InventoryDatabase;
import com.cjpowered.learn.inventory.Item;
import com.cjpowered.learn.inventory.Warehouse;


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
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onHand(final Item item, final Warehouse warehouse) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onOrder(Item item, Warehouse warehouse) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void setRequiredOnHand(Item item, Warehouse warehouse, int newAmount) {
        throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void setRequiredOnHand(Item item, int newAmount) {
		// TODO Auto-generated method stub
		
	}
}
