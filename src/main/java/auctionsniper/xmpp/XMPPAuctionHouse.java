package auctionsniper.xmpp;

import auctionsniper.Auction;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static java.lang.String.format;

/**
 * Added Chapter 17:
 * Code not directly listed in GOOS, but it explains what to extract from Main on pages 196 adn 197.
 */
public class XMPPAuctionHouse implements AuctionHouse{
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private XMPPConnection connection;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException{
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    @Override
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId));
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    private XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    private String auctionId(String itemId) {
        return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
