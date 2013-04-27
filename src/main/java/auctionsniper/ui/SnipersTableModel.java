package auctionsniper.ui;

import auctionsniper.*;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 *
 * Changed Chapter 17:
 * Code from GOOS, pg 199
 * - Moved SwingThreadSniperListener inner class here.  It is a better fit as it is concerned with Swing code.
 * - Added SniperCollector interface and implementation.  In doing so, this class no longer accepts SniperSnapshot
 *     instances to add to the table model.  Instead, it now accepts AuctionSnipers, and asks it for its snapshot.
 *     This also provides an opportunity to add the SwingThreadSniperListener as a SniperListener to the AuctionSniper.
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 207
 * - Added "Losing" to STATUS_TEXT.
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {

    private static String[] STATUS_TEXT = {
            "Joining", "Bidding", "Winning", "Losing", "Lost", "Won"
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

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot sniperSnapshot){
        snapshots.add(sniperSnapshot);
        int row = snapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }

    private class Defect extends RuntimeException {
        public Defect(String s) {
            super(s);
        }
    }

    public class SwingThreadSniperListener implements SniperListener {
        private SnipersTableModel snipers;

        public SwingThreadSniperListener(SnipersTableModel snipers) {
            this.snipers = snipers;
        }

        @Override
        public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
            snipers.sniperStateChanged(sniperSnapshot);
        }

        @Override
        public void processMessage(Chat chat, Message message) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
