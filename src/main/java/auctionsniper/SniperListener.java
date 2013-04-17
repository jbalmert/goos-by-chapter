package auctionsniper;

import org.jivesoftware.smack.MessageListener;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124
 */
public interface SniperListener extends MessageListener {
    void sniperLost();
    void sniperBidding();
}
