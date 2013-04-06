package auctionsniper;

import org.junit.After;
import org.junit.Test;

/**
 * Added: Chapter 10
 * Original code from GOOS, pg 85
 * - Defines first use case: single item join, lose without bidding.
 *
 * Changed Chapter 12
 * Code from GOOS, pg 106, 109
 * - Adjust tests to use updated form of auction.hasReceivedJoinRequestFromSniper()
 *
 * Changed Chapter 14
 * Code from GOOS, pg 140
 * - Added sniperWinsAuctionByBiddingHigher() to drive out the functionality to win an auction.
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception{
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction.announcesClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announcesClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception{
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning();

        auction.announcesClosed();
        application.showsSniperHasWonAuction();
    }

    @After public void stopAuction() {
        auction.stop();
    }

    @After public void stopApplication() {
        application.stop();
    }
}
