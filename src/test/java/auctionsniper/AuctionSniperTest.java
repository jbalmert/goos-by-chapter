package auctionsniper;

import static auctionsniper.AuctionEventListener.*;
import static auctionsniper.SniperSnapshot.*;
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
 *
 * Changed Chapter 17:
 * - Adjusted instantiation of AuctionSniper to use the new order of arguments.  itemId is now the first argument.
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 210
 * - Code for doesNotBidAndReportsLosingIfSubsequentPricesIsAboveStopPrice() added from text.  The implementations
 *     for doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice(),
 *     reportsLostIfAuctionClosesWhenLosing(),
 *     continuesToBeLosingOnceStopPriceHasBeenReached(), and
 *     doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() are mentioned as extra tests, but the code
 *     is left as an exercise for the reader.  Implementations for these have been built.
 *
 * Changed Chapter 19:
 * Code from GOOS, pg 218
 * - Added reportsFailedIfAuctionFailsWhenBidding() to drive out the appropriate response when an auction fails.
 */

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

    private static final String ITEM_ID = "auction-1234";
    Item item = new Item(ITEM_ID, 1234);
    @Mock Auction auction;
    @Mock SniperListener sniperListener;

    AuctionSniper sniper;

    @Before
    public void initializeSniper() {
        sniper = new AuctionSniper(item, auction);
        sniper.addSniperListener(sniperListener);
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

    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        int bid = 123 + 45;
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 23, PriceSource.FromOtherBidder);

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        sniper.currentPrice(1235, 45, PriceSource.FromOtherBidder);

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1235, 0, LOSING));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        sniper.currentPrice(1235, 45, PriceSource.FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1235, 0, LOST));
    }

    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        sniper.currentPrice(1235, 45, PriceSource.FromOtherBidder);
        sniper.currentPrice(2000, 45, PriceSource.FromOtherBidder);

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1235, 0, LOSING));
        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2000, 0, LOSING));
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        sniper.currentPrice(123, 45, PriceSource.FromSniper);
        sniper.currentPrice(2000, 45, PriceSource.FromOtherBidder);

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 123, 0, WINNING));

        verify(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2000, 0, LOSING));
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() {
        sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
        sniper.auctionFailed();

        verify(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 0, 0, SniperState.FAILED));
    }
}
