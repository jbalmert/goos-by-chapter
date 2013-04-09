package auctionsniper;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 128, 132
 * -   As a side effect of extracting XMPPAuction from Main, added the join() method to the interface.
 */
public interface Auction {
    void bid(int amount);
    void join();
}
