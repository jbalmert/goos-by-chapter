package auctionsniper;

import static org.mockito.Mockito.*;

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
    public void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();

        verify(sniperListener).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction).bid(price + increment);
        verify(sniperListener).sniperBidding();
    }

}
