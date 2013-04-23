package auctionsniper;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 128, 132
 * -   As a side effect of extracting XMPPAuction from Main, added the join() method to the interface.
 *
 * Changed Chapter 17:
 * As a result of changes to Main on GOOS, pg 193, addAuctionEventListener had to be added to this interface.
 */
public interface Auction {
    void bid(int amount);
    void join();
    void addAuctionEventListener(AuctionEventListener auctionSniper);
}
