package auctionsniper;

import org.junit.After;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: jimmy
 * Date: 3/14/13
 * Time: 9:53 PM
 * To change this template use File | Settings | File Templates.
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
