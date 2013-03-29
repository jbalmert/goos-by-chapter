package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Added Chapter 11:
 * Original code from GOOS, pg 96, 99, 101
 * <ul>
 *     <li>Adds startUserInterface() in response to end-to-end test failure.  This method opens up the main
 *     JFrame with the appropriate title.</li>
 *     <li>Adds sniper status label in response to next test failure.</li>
 *     <li>Implements functionality to join an auction.</li>
 *     <li>As a short term hack, keeps a reference to a chat to prevent it from being GC'd.</li>
 * </ul>
 *
 * Changed while implementing Chapter 12:
 * Code change is not from the book
 * <ul>
 *     <li>Removed special code to disable SASL.  After restarting openfire, was unable to connect without SASL
 *     enabled.  Received a "Forbidden (403)" error.  I am uncertain what caused this, but am removing the extra code
 *     to match the original text.</li>
 * </ul>
 *
 * Changed Chapter 12:
 * Code From GOOS, pg 110, 111, 117
 * <ul>
 *     <li>joinAuction() now sends JOIN_COMMAND_FORMAT as a message response instead of an empty message.</li>
 *     <li>Connection to openfire is now disconnected when the main window closes.  This resolves an issue with
 *     multiple tests logging in as the same user.  Without firt disconnecting, openfire thinks the user is still
 *     connected, and does not accept the second login.</li>
 *     <li>Added AuctionEventListener interface. Changed joinAuction() to use an AuctionMessageTranslator rather
 *     than handle messages itself.</li>
 * </ul>
 *
 */
public class Main implements AuctionEventListener {
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

    @Override
    public void auctionClosed() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui.showStatus(MainWindow.STATUS_LOST);
            }
        });
    }

    @Override
    public void currentPrice(int price, int increment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void joinAuction(XMPPConnection connection, String itemId)
            throws XMPPException{
        disconnectWhenUICloses(connection);
        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new AuctionMessageTranslator(this));
        this.notToBeGCd = chat;
        chat.sendMessage(JOIN_COMMAND_FORMAT);
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
        System.out.println("Logging in as " + username + ", with password " + password);
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
