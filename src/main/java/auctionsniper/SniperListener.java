package auctionsniper;

import org.jivesoftware.smack.MessageListener;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124
 *
 * Changed Chapter 14:
 * Code not listed, but forced to make this change due to
 *     AuctionSniperTest.reportsIsWinningWhenCurrentPriceComesFromSniper().
 */
public interface SniperListener extends MessageListener {
    void sniperLost();
    void sniperBidding();
    void sniperWinning();
    void sniperWon();
}
