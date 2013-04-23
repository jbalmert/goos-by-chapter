package auctionsniper;

import auctionsniper.xmpp.AuctionHouse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Added Chapter 17:
 * Code from GOOS, pg 198
 * - Using Mockito, to verify the order of invocations, I leveraged Mockito's InOrder verification object.
 */
@RunWith(MockitoJUnitRunner.class)
public class SniperLauncherTest {

    @Mock AuctionHouse auctionHouse;
    @Mock Auction auction;
    @Mock SniperCollector sniperCollector;

    SniperLauncher sniperLauncher;

    @Before
    public void constructSniperLauncher() {
        sniperLauncher = new SniperLauncher(auctionHouse, sniperCollector);
    }

    @Test
    public void addsNewSniperToCollectorAndThenJoinsAuction() {
        String itemId = "item 123";
        when(auctionHouse.auctionFor(itemId)).thenReturn(auction);
        InOrder orderedVerifier = inOrder(auction, sniperCollector, auction);

        sniperLauncher.joinAuction(itemId);

        orderedVerifier.verify(auction).addAuctionEventListener(any(AuctionSniper.class));
        orderedVerifier.verify(sniperCollector).addSniper(any(AuctionSniper.class));
        orderedVerifier.verify(auction).join();
    }
}
