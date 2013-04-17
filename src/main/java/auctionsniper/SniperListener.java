package auctionsniper;

import org.jivesoftware.smack.MessageListener;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124
 *
 * Changed Chapter 14:
 * Code not listed, but forced to make this change due to
 *     AuctionSniperTest.reportsIsWinningWhenCurrentPriceComesFromSniper().
 *
 * Changed Chapter 15:
 * Code not listed, but forced to make this chang due to changes in
 *     AuctionSniperTest.bidsHigherAndReportsBiddingWhenNewPriceArrives().
 * - Added SniperSnapshot parameter to sniperBidding();
 * - Removed all but the sniperStateChanged() method.  The separate methods for each state became redundant
 *     as soon as this method was introduced.
 */
public interface SniperListener extends MessageListener {
    void sniperStateChanged(SniperSnapshot sniperSnapshot);
}
