package auctionsniper.xmpp;

/**
 * Added Chapter 19:
 * Code described in GOOS, pg 224
 */
public class XMPPAuctionException extends Exception {
    public XMPPAuctionException(String message, Exception e) {
        super(message, e);
    }
}
