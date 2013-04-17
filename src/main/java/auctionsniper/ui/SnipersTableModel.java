package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import javax.swing.table.AbstractTableModel;

/**
 * Extracted from MainWindow in Chapter 15:
 * Code from GOOS pg, 158, 166
 * - Updated to handle item id, last price, and last bid in addition to the original sniper status.
 * - Fixed STARTING_UP to default to JOINING instead of BIDDING.  This was a mistake on my part.
 * - Added values to STATUS_TEXT so all states are accounted for and used raw strings rather than
 *     rely on the values in MainWindow.
 * - Added textFor() method to translate a SniperState to a String by indexing STATUS_TEXT.  This is now used in
 *     getValueAt() to return the sniper status on the UI.
 * - Changed implementation of getValueAt to leverage Column.valueIn() rather than use a switch statement.
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener{
    private final static SniperSnapshot STARTING_UP =
            new SniperSnapshot("", 0, 0, SniperSnapshot.SniperState.JOINING);
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };
    private SniperSnapshot snapshot = STARTING_UP;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        snapshot = newSnapshot;
        fireTableRowsUpdated(0,0);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public static String textFor(SniperSnapshot.SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
