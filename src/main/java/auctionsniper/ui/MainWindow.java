package auctionsniper.ui;

import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.SniperSnapshot;
import auctionsniper.UserRequestListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

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
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 185, 187
 * - Added makeControls() method to build an input field for adding new auction items.
 * - New control JPanel added to PAGE_START of BorderLayout.
 * - Added set of UserRequestListener's to handle adding joining auctions.  The book implementation used an
 *     Announcer class to handle firing events to multiple instances of the same class.  It's a pretty slick
 *     idea, but unfortunately, it ships with JMock, and I didn't want to implement my own copy here.  Instead,
 *     I used a simple set, and iterated over the set to ensure every listener receives a message.
 *
 * Changed Chapter 17:
 * Code from GOOS, pg 200
 * - Removed SnipersTableModel as a constructor argument.  Instead, replaced it with a SniperPortfolio.  The
 *     SnipersTableModel is now instantiated when creating the JTable, and immediately registered as a portfolio
 *     listener.
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 210
 * - Updated joinActionButton.addActionListener to have the anonymous implementation of ActionListener
 *     announce events to the updated UserRequestListener with an Item instead of a string.  Also, added some
 *     helper methods to make this method cleaner.
 */
public class MainWindow extends JFrame{
    private final Set<UserRequestListener> userRequests = new HashSet<UserRequestListener>();
    private static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "Winning";
    public static final String STATUS_WON = "Won";
    public static final String STATUS_LOST = "Lost";
    public static final String STATUS_LOSING = "Losing";
    private static final String SNIPERS_TABLE_NAME = "Snipers";
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String JOIN_BUTTON_NAME = "join button";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";


    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(25);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        final JTextField stopPriceField = new JTextField();
        stopPriceField.setColumns(20);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        controls.add(itemIdField);
        controls.add(stopPriceField);

        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (UserRequestListener listener : userRequests) {
                    listener.joinAuction(new Item(itemId(), stopPrice()));
                }
            }

            public String itemId() {
                return itemIdField.getText();
            }

            public int stopPrice() {
                return Integer.parseInt(stopPriceField.getText().trim());
            }
        });
        controls.add(joinAuctionButton);

        return controls;
    }

    private void fillContentPane(JTable snipersTable, JPanel controlPanel) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(controlPanel, BorderLayout.PAGE_START);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void addUserRequestListener(UserRequestListener userRequestListener) {
        userRequests.add(userRequestListener);
    }
}
