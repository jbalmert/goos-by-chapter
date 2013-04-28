package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventAnnouncer;
import auctionsniper.AuctionEventListener;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static java.lang.String.format;

/**
 * Added as its own class Chapter 17:
 * Forced to extract this class to put it in the xmpp package as described in GOOS pg. 195.  I suspect this was
 * supposed to be its own class long ago.  I moved all of the constants this class needs out of of Main.  This
 * forced a few updates in other classes that reference those constants.  I'm not sure if this is how the authors
 * intended it to be.
 *
 * Changed Chapter 19:
 * Code from GOOS, pg 220
 * - Added extra listener to unsubscribe from the chat if the auction fails.
 */
public class XMPPAuction implements Auction {

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";


    private AuctionEventAnnouncer auctionEventListeners = new AuctionEventAnnouncer();
    private final Chat chat;
    private XMPPFailureReporter failureReporter;

    public XMPPAuction(XMPPConnection connection, String auctionId,
                       XMPPFailureReporter failureReporter) {
        this.failureReporter = failureReporter;
        AuctionMessageTranslator translator = translatorFor(connection);
        chat = connection.getChatManager().createChat(auctionId, translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    @Override
    public void bid(int amount) {
        sendMessage(format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener auctionSniper) {
        auctionEventListeners.addListener(auctionSniper);
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(),
                auctionEventListeners, failureReporter);
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override public void auctionClosed() {}
            @Override public void currentPrice(int price, int increment, PriceSource fromSniper) {}

            @Override public void auctionFailed() {
                chat.removeMessageListener(translator);
            }
        };
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}