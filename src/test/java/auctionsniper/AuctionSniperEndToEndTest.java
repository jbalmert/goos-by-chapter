package auctionsniper;

import org.junit.After;
import org.junit.Test;

/**
 * Original code from Growing Object Oriented Software, Guided By Tests, p. 85
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilActionCloses() {
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
