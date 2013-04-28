package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static auctionsniper.xmpp.AuctionMessageTranslator.*;
import static java.lang.String.format;
import static org.apache.commons.io.FilenameUtils.getFullPath;

/**
 * Added Chapter 17:
 * Code not directly listed in GOOS, but it explains what to extract from Main on pages 196 adn 197.
 *
 * Changed Chapter 18:
 * Code not in GOOS.
 * - Updated to use Item instead of a String in auctionFor()
 *
 * Changed Chapter 19:
 * Code from GOOS, pg 224
 * - Added support for logging errors.
 */
public class XMPPAuctionHouse implements AuctionHouse{
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    private static final String LOGGER_NAME = "XMPPLogger";

    private XMPPConnection connection;
    private final XMPPFailureReporter failureReporter;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws XMPPException,
            XMPPAuctionException{
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    @Override
    public Auction auctionFor(Item itemId) {
        return new XMPPAuction(connection, auctionId(itemId.identifier), failureReporter);
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    private XMPPAuctionHouse(XMPPConnection connection) throws XMPPAuctionException{
        this.connection = connection;
        this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }

    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFilerHandler());
        return logger;
    }

    private FileHandler simpleFilerHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new XMPPAuctionException("Could nto create logger FileHandler = "
                                         + getFullPath(LOG_FILE_NAME), e);
        }
    }

    private String auctionId(String itemId) {
        return format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
