package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;

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
 */
public class Main {
    @SuppressWarnings("unused") private Chat notToBeGCd;
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
        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new MessageListener() {
                    public void processMessage(Chat aChat, Message message) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                               ui.showStatus(MainWindow.STATUS_LOST);
                            }
                        });
                    }
                });
        this.notToBeGCd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password)
            throws XMPPException {
        ConnectionConfiguration cc = createConnectionConfiguration(hostname);
        XMPPConnection connection = new XMPPConnection(cc);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    /**
     * This is different from the original code.  Using OpenFire 3.8, it appears the default connection mode
     * uses SASL.  Rather than worry about configuring encryption correctly, this implementation explicitly
     * disables the SASL authentication.
     */
    private static ConnectionConfiguration createConnectionConfiguration(String hostname) {
        ConnectionConfiguration cc = new ConnectionConfiguration(hostname);
        cc.setSASLAuthenticationEnabled(false);
        return cc;
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
