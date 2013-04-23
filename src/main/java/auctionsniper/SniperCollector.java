package auctionsniper;

/**
 * Added Chapter 17:
 * Described in GOOS, pg 198, 199.
 * The collector defines an interface for aggregating all the snipers into a single location.
 */
public interface SniperCollector {
    void addSniper(AuctionSniper sniper);
}
