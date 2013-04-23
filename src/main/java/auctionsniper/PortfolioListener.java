package auctionsniper;

import java.util.EventListener;

/**
 * Added Chapter 17:
 * code from GOOS, pg 199
 */
public interface PortfolioListener extends EventListener {
    void sniperAdded(AuctionSniper sniper);
}
