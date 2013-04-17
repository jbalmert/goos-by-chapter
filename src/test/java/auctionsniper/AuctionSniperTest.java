package auctionsniper;

import static org.mockito.Mockito.*;
import static auctionsniper.AuctionEventListener.PriceSource.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
 */

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {

    @Mock Auction auction;
    @Mock SniperListener sniperListener;

    AuctionSniper sniper;

    @Before
    public void initializeSniper() {
        sniper = new AuctionSniper(auction, sniperListener);
    }

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();

        verify(sniperListener).sniperLost();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener).sniperBidding();
        verify(sniperListener).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment, FromSniper);

        verify(auction).bid(price + increment);
        verify(sniperListener).sniperBidding();
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, FromSniper);

        verify(sniperListener).sniperWinning();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener).sniperWinning();
        verify(sniperListener).sniperWon();
    }

}
