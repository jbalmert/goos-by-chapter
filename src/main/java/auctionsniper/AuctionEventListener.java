package auctionsniper;

/**
 * Added Chapter 12:
 * Although the code is not listed in the book, this interface is referenced on pg 115, so it is
 * necessary to generate it at this time.
 *
 * Changed Chapter 14:
 * Code from GOOS, pg 141
 * - Added PriceSource enum to identify the source of a bid (from this sniper or someone else).
 *
 * Changed Chapter 19:
 * Code decribed in GOOS, pg 218
 * - Added auctionFailed() method.
 */
public interface AuctionEventListener {
    enum PriceSource {
        FromSniper, FromOtherBidder;
    }

    void auctionClosed();
    void auctionFailed();
    void currentPrice(int price, int increment, PriceSource fromSniper);
}
