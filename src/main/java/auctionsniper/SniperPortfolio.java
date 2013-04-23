package auctionsniper;

import java.util.HashSet;
import java.util.Set;

/**
 * Added Chapter 17:
 * This code is described on pg 199.  The SniperPortfolio inherits the responsibility for tracking all the snipers
 *    from the SnipersTableModel.
 */
public class SniperPortfolio implements SniperCollector {
    private Set<AuctionSniper> snipers = new HashSet<AuctionSniper>();
    private PortfolioListener portfolioListener;

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        portfolioListener.sniperAdded(sniper);
    }

    public void addPortfolioListener(PortfolioListener listener) {
        this.portfolioListener = listener;
    }
}
