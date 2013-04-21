package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static auctionsniper.SniperSnapshot.SniperState;

/**
 * Added Chapter 15:
 * Code from GOOS, pg 156, 157, 170
 * - Added unit test for setting up column names for the JTable.
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 180, 181
 * - Added notifiesListenerWhenAddingASniper()
 * - Adjusted setsSniperValueColumns() to work with new addSniper() method on SnipersTableModel
 * - Added holdsSniperInAdditionOrder()
 * - Added updatesCorrectRowForSniper()
 * - Added throwsDefectIfNotExistingSniperForAnUpdate()
 * - Based on tweaks made in the model to make the rows display in the JTable when they are added to the model,
 *     a tableChange event is fired off for every snapshot added.  To account for this, setsSniperValuesInColumns()
 *     was adjusted to accept at least one tableChanged event on the listener, rather than exactly one.
 */

@RunWith(MockitoJUnitRunner.class)
public class SnipersTableModelTest {

    @Mock TableModelListener listener;
    private final SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        verify(listener, atLeastOnce()).tableChanged(any(TableModelEvent.class));
        assertRowMatchesSnapshot(0, bidding);
    }

    @Test
    public void setsUpColumnHeadings() {
        for(Column column: Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    public void notifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        assertEquals(0, model.getRowCount());

        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
        assertRowMatchesSnapshot(0, joining);
    }

    @Test
    public void holdsSniperInAdditionOrder() {
        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertColumnEquals(0, Column.ITEM_IDENTIFIER, "item 0");
        assertColumnEquals(1, Column.ITEM_IDENTIFIER, "item 1");
    }

    @Test
    public void updatesCorrectRowForSniper() {
        SniperSnapshot joining1 = SniperSnapshot.joining("item 0");
        SniperSnapshot joining2 = SniperSnapshot.joining("item 1");
        SniperSnapshot bidding = joining2.bidding(22, 22);

        model.addSniper(joining1);
        model.addSniper(joining2);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(1, bidding);
    }

    @Test(expected=Exception.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        model.sniperStateChanged(SniperSnapshot.joining("Item 0").bidding(22,22));
    }

    private void assertRowMatchesSnapshot(int index, SniperSnapshot snapshot) {
        assertColumnEquals(index, Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertColumnEquals(index, Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnEquals(index, Column.LAST_BID, snapshot.lastBid);
        assertColumnEquals(index, Column.SNIPER_STATUS, SnipersTableModel.textFor(snapshot.state));
    }

    private void assertColumnEquals(int rowIndex, Column column, Object expected) {
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }
}
