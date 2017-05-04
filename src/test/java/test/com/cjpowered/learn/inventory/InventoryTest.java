package test.com.cjpowered.learn.inventory;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import com.cjpowered.learn.inventory.InventoryManager;
import com.cjpowered.learn.inventory.Order;
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
        final LocalDate today = LocalDate.now();
        final InventoryManager im = new AceInventoryManager();

        // when
        final List<Order> actual = im.getOrders(today);

        // then
        assertTrue(actual.isEmpty());

    }

}
