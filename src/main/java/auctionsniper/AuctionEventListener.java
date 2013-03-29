package auctionsniper;

/**
 * Added Chapter 12:
 * Although the code is not listed in the book, this interface is referenced on pg 115, so it is
 * necessary to generate it at this time.
 */
public interface AuctionEventListener {
    public void auctionClosed();

    void currentPrice(int price, int increment);
}
