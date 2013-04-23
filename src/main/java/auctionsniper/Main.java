package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.AuctionHouse;
import auctionsniper.xmpp.XMPPAuctionHouse;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Added Chapter 11:
 * Original code from GOOS, pg 96, 99, 101
 * - Adds startUserInterface() in response to end-to-end test failure.  This method opens up the main
 *     JFrame with the appropriate title.
 * - Adds sniper status label in response to next test failure.
 * - Implements functionality to join an auction.
 * - As a short term hack, keeps a reference to a chat to prevent it from being GC'd.
 *
 * Changed while implementing Chapter 12:
 * Code change is not from the book
 *  -Removed special code to disable SASL.  After restarting openfire, was unable to connect without SASL
 *     enabled.  Received a "Forbidden (403)" error.  I am uncertain what caused this, but am removing the extra code
 *     to match the original text.
 *
 * Changed Chapter 12:
 * Code From GOOS, pg 110, 111, 117
 * - joinAuction() now sends JOIN_COMMAND_FORMAT as a message response instead of an empty message.=
 * - Connection to openfire is now disconnected when the main window closes.  This resolves an issue with
 *     multiple tests logging in as the same user.  Without firt disconnecting, openfire thinks the user is still
 *     connected, and does not accept the second login.
 * - Added AuctionEventListener interface. Changed joinAuction() to use an AuctionMessageTranslator rather
 *     than handle messages itself.
 *
 * Changed Chapter 13:
 * Code from GOOS, pg 125, 129, 132
 * - The AuctionEventListener interface is now owned by AuctionSniper.  Instead, this class is now a
 *     Sniper Listener
 * - Removed implementations of auctionClosed and currentPrice as this class is no longer responsible for these
 *     actions.
 * - Added implementation of sniperLost() as per the SniperListener interface.
 * - Added Auction to joinAuction().  This forced the method to be reorganized to successfully instantiate
 *     the circular dependencies of the Chat, AuctionMessageTranslator, AuctionSniper, and Auction.
 * - Added implementation of sniperBidding() added to SniperListener interface.
 * - Added inner class XMPPAuction as an implementation of Auction interface.
 * - Added inner class SwingThreadSniperListener.  As a side note, the code in the book lists sniperWinning() as a
 *     method in the class.  However, this is not on the interface yet, and is not used yet.  Therefore, I've
 *     omitted it from the implementation at this time.
 * - Removed the SniperListener methods from Main as they are no longer used.
 *
 * Changed Chapter 14:
 * Code from GOOS, pg 142, 147
 * - Added sniper id to the AuctionMessageTranslator constructor call.  The value comes from connection.getUser() as
 *     it already has the id properly formatted.
 * - As A consequence of adding sniperWon() to the SniperListener interface, added implementation of the method
 *     to SwingThreadSniperListener.
 *
 * Changed Chapter 15:
 * Code change mentioned in GOOS, pg 155, but not shown
 * - Changed signature of sniperBidding() to take in a SniperSnapshot.
 * - Removed sniperBidding(), sniperWinning(), sniperLost(), etc on SwingThreadSniperListener as these were removed
 *     from the SniperListener interface in favor of sniperStatusChanged() which handles all the responsibilities
 *     of the previous state change methods.
 * - Renamed SniperStateDisplayer to SwingThreadSniperListener.  It now directly interacts with the SniperTableModel,
 *     which now implements the SniperListener interface.  This bypasses the MainWindow, which was simply forwarding
 *     the message to the table model.
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 178, 179, 180, 188
 * - Finally defined the JOIN_COMMAND_FORMAT.  This came indirectly from GOOS, pg 178.
 * - Changed main() to hold reference to connection
 * - Changed notToBeGCd to a Set<Chat> to track multiple chats.
 * - Changed joinAuction to join more than one auction at a time.
 * - Added call to safelyAddItemToModel() in joinAuction
 * - Changed main() to add a UserRequestListener for the newly created connection to the auction chat.  This
 *     is actually accomplished indirectly by calling the SnipersTableModel.addSniper() method.
 *
 * Changed Chapter 17:
 * Code from GOOS, pg 193, 194, 200
 * - Extracted Chat code from addUserRequestListenerFor() by first introducing an Announcer to sit between the Auction
 *     and the AuctionMessageTranslator.  Note, rather than use the Announcer from JMock examples, I created
 *     AuctionEventListener to perform the same function.  After the link between auction and message translator was
 *     removed, it was possible to push the code for the Chat into the XMPPAuction class, where it seems to have a
 *     better fit.
 * - Moved XMPPAuction to its own class in the xmpp package as described in GOOS, pg 195.
 * - Instantiates SniperPortfolio and now sends it as a constructor argument to MainWindow.
 */
public class Main{
    private final SniperPortfolio portfolio = new SniperPortfolio();
    public static final int ARG_HOSTNAME = 0;
    public static final int ARG_USERNAME = 1;
    public static final int ARG_PASSWORD = 2;
    public static final int ARG_ITEM_ID = 3;


    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception{
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
                args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(final AuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(portfolio);
            }
        });
    }

}
