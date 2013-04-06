package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.String.format;

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
 * - Added inner class SniperStateDisplayer.  As a side note, the code in the book lists sniperWinning() as a
 *     method in the class.  However, this is not on the interface yet, and is not used yet.  Therefore, I've
 *     omitted it from the implementation at this time.
 * - Removed the SniperListener methods from Main as they are no longer used.
 *
 * Changed Chapter 14:
 * Code from GOOS, pg 142, 147
 * - Added sniper id to the AuctionMessageTranslator constructor call.  The value comes from connection.getUser() as
 *     it already has the id properly formatted.
 * - As A consequence of adding sniperWon() to the SniperListener interface, added implementation of the method
 *     to SniperStateDisplayer.
 */
public class Main{
    public static final String JOIN_COMMAND_FORMAT = "";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

    private Chat notToBeGCd;
    public static final int ARG_HOSTNAME = 0;
    public static final int ARG_USERNAME = 1;
    public static final int ARG_PASSWORD = 2;
    public static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception{
        Main main = new Main();
        main.joinAuction(
                connectTo(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                args[ARG_ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId)
            throws XMPPException{
        disconnectWhenUICloses(connection);

        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection), null);
        this.notToBeGCd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
                new AuctionMessageTranslator(connection.getUser(), new AuctionSniper(auction, new SniperStateDisplayer())));
        auction.join();
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    private static XMPPConnection connectTo(String hostname, String username, String password)
            throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    public static class XMPPAuction implements Auction {
        private final Chat chat;

        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void bid(int amount) {
            sendMessage(format(BID_COMMAND_FORMAT, amount));
        }

        @Override
        public void join() {
            sendMessage(JOIN_COMMAND_FORMAT);
        }

        private void sendMessage(final String message) {
            try {
                chat.sendMessage(message);
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
    }

    public class SniperStateDisplayer implements SniperListener {
        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);
        }

        @Override
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(MainWindow.STATUS_WON);
        }

        @Override
        public void processMessage(Chat chat, Message message) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        private void showStatus(final String status) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() { ui.showStatus(status);}
            });
        }
    }
}
