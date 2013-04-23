package auctionsniper.xmpp;

import auctionsniper.Auction;

/**
 * Added Chapter 17:
 * Code from GOOS, pg 196.
 */
public interface AuctionHouse {
    Auction auctionFor(String itemId);
    void disconnect();
}
