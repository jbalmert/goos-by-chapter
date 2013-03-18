package auctionsniper;

import org.junit.After;
import org.junit.Test;

/**
 * Added: Chapter 10
 * Original code from GOOS, p. 85
 * <ul>
 *     <li>Defines first use case: single item join, lose without bidding.</li>
 * </ul>
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilActionCloses() throws Exception{
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announcesClosed();
        application.showsSniperHasLostAuction();
    }

    @After public void stopAuction() {
        auction.stop();
    }

    @After public void stopApplication() {
        application.stop();
    }
}
