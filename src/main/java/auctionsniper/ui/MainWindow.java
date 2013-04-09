package auctionsniper.ui;

import auctionsniper.SniperSnapshot;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Added Chapter 11:
 * Original code from GOOS, pg 97, 102
 * - Creates a new JFrame and makes it visible.
 * - Implements ability to update status label text.
 *
 * Changed Chapter 15:
 * Extracted the SnipersTableModel to its own class so it can be unit tested.
 * - Changed implementation to expect a SniperTableModel as a constructor argument.  This is a result of
 *     code listed on pg 168.
 * - Removed unneeded sniperStatusChanged method.  The event now bypasses MainWindow and sends it directly
 *     to the SniperTableModel.
 */
public class MainWindow extends JFrame{
    private final SnipersTableModel snipers;
    private static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    private static final String SNIPERS_TABLE_NAME = "Snipers";
    public static final String APPLICATION_TITLE = "Auction Sniper";

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }
}
