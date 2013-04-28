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
 *
 * Changed Chapter 15:
 * Code from GOOS, pg 152
 * - Changed sniperWinsAnAuctionByBiddingHigher() to expect the various state transitions to
 *     display the price and bid changes on the UI.  This leads to a JTable implementation to replace the
 *     JLabel in the UI currently.
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 175, 176
 * - Added auction as parameter to the ApplicationRunner methods to support multiple auctions at once.
 * - Added sniperBidsForMultipleItems() to test bidding for multiple auctions.
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 206
 * - Added sniperLosesAnAuctionWhenThePriceIsTooHigh() to drive out implementation of a stop price.
 *
 * Changed Chapter 19:
 * Code from GOOS, pg 216
 * - Added sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() to drive out error handling.
 */
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception{
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction.announcesClosed();
        application.showsSniperHasLostAuction(auction, 0, 0);
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.announcesClosed();
        application.showsSniperHasLostAuction(auction, 1000, 1098);
    }

    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception{
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction, 1098);

        auction.announcesClosed();
        application.showsSniperHasWonAuction(auction, 1098);
    }

    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

        application.hasShownSniperIsWinning(auction, 1098);
        application.hasShownSniperIsWinning(auction2, 521);

        auction.announcesClosed();
        auction2.announcesClosed();

        application.showsSniperHasWonAuction(auction, 1098);
        application.showsSniperHasWonAuction(auction2, 521);
    }

    @Test
    public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
        auction.startSellingItem();
        application.startsBiddingWithStopPrice(1100, auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1197, 110, "third party");
        application.hasShownSniperIsLosing(auction, 1197, 1098);

        auction.reportPrice(1207, 10, "fourth party");
        application.hasShownSniperIsLosing(auction, 1207, 1098);

        auction.announcesClosed();
        application.showsSniperHasLostAuction(auction, 1207, 1098);
    }

    @Test
    public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
        String brokenMessage = "a broken message";
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(500, 20, "other bidder");
        auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);

        auction.sendInvalidMessageContaining(brokenMessage);
        application.showsSniperHasFailed(auction);

        auction.reportPrice(520, 21, "other bidder");
        waitForAnotherAuctionEvent();

        application.reportsInvalidMessage(auction, brokenMessage);
        application.showsSniperHasFailed(auction);
    }

    private void waitForAnotherAuctionEvent() throws Exception {
        auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(600, 6, "other bidder");
        application.hasShownSniperIsBidding(auction2, 600, 606);
    }

    @After public void stopAuction() {
        auction.stop();
        auction2.stop();
    }

    @After public void stopApplication() {
        application.stop();
    }
}
