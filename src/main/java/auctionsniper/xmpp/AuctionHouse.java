package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.Item;

/**
 * Added Chapter 17:
 * Code from GOOS, pg 196.
 *
 * Update Chapter 18:
 * Code not in GOOS.
 * - Changed auctionFor to take in an Item instead of a String
 */
public interface AuctionHouse {
    Auction auctionFor(Item itemId);
    void disconnect();
}
