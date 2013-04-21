package auctionsniper.ui;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

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
 *
 * Changed Chapter 16:
 * Code derived from changes to Main in GOOS, pg 180, and unit tests on pg 180, 181
 * - Replaced single SniperSnapshot with a List<Snapshot> to track multiple auction states.
 * - Added method addSniper() to populate different sniper snapshots.
 * - Changed sniperStateChanged() to first find the matching row before updating the snapshot to the correct state.
 * - A failure to find a Snapshot to update is considered a programming error, and so a Defect is thrown.
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener{
    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Lost", "Won"
    };
    private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

    @Override
    public int getRowCount() {
        return snapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameItemAs(snapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + newSnapshot);
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

    public void addSniper(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        fireTableDataChanged();
    }

    private class Defect extends RuntimeException {
        public Defect(String s) {
            super(s);
        }
    }
}
