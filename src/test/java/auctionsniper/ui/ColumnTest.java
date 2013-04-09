package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Added Chapter 15:
 * The code is not in the book, but on pg 167, the authors mention that they wrote unit tests
 * for this enum to drive out some new behavior that makes the SnipersTableModel.getValueAt()
 * implementation trivial.
 */
public class ColumnTest {
    private static final String ITEM_ID = "item id";
    private static final int LAST_PRICE = 123;
    private static final int LAST_BID = 234;

    private SniperSnapshot snapshot =
            new SniperSnapshot(ITEM_ID, LAST_PRICE, LAST_BID, SniperSnapshot.SniperState.BIDDING);

    @Test
    public void itemIdentifierReturnsTheItemIdValueInSniperSnapshot() {
        assertEquals(ITEM_ID, Column.ITEM_IDENTIFIER.valueIn(snapshot));
    }

    @Test
    public void lastPriceReturnsTheLastPriceValueInSnapshot() {
        assertEquals(LAST_PRICE, Column.LAST_PRICE.valueIn(snapshot));
    }

    @Test
    public void lastBidReturnsTheLastBidValueInSnapshot() {
        assertEquals(LAST_BID, Column.LAST_BID.valueIn(snapshot));
    }

    @Test
    public void sniperStatusReturnsTheTextForTheSniperStateValueInSnapshot() {
        assertEquals("Bidding", Column.SNIPER_STATUS.valueIn(snapshot));
    }
}
