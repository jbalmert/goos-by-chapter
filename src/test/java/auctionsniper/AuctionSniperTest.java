package auctionsniper;

import static org.mockito.Mockito.*;
import static auctionsniper.AuctionEventListener.PriceSource.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static auctionsniper.SniperSnapshot.SniperState.*;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124, 127
 * This is another example of extracting a responsibility from the Main class.  An AuctionSniper represents
 * one of the main actors in the application.
 * - Added method to notify listeners that the Sniper lost the auction.
 * - Added reference to a new Auction class, which accepts "bid" events.
 * - Added another @Before method to initialize AuctionSniper after the class is instantiated
 *     to give Mockito a chance to mock out its dependencies.
 *
 * Changed Chapter 14:
 * Code from GOOS, pg 143
 * - Added test to drive out a winning status when current bid comes from the sniper.  The original implementation
 *     of this calls for using JMock States to sneak a peak of the internal state of the AuctionSniper.  This is used
 *     to ensure that the the sniper tried to bid and then acknowledged the loss when the auction closes.  Using
 *     Mockito, I simply verified that the SniperListener received both a sniperBidding() and a sniperLost() message.
 *
 * Changed Chapter 15:
 * Code from GOOS, pg 155, 161
 * - Added SniperSnapshot to SniperListener.sniperBidding() calls to pass the information on current bidding
 *     status for display.
 * - Changed the sniperBidding() calls to sniperStateChanged(), a more generic method which can handle all state
 *     changes.
 */

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

    private static final String ITEM_ID = "auction-1234";
    @Mock Auction auction;
    @Mock SniperListener sniperListener;

    AuctionSniper sniper;

    @Before
    public void initializeSniper() {
        sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);
    }

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 0, 0, LOST));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        final int price = 123;
        final int increment = 45;
        final int bid = price + increment;

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, price, bid, LOST));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction).bid(price + increment);
        verify(sniperListener).sniperStateChanged(eq(new SniperSnapshot(ITEM_ID, price, bid, BIDDING)));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 135, BIDDING));
        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 0, WINNING));
        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 0, WON));
    }

}
