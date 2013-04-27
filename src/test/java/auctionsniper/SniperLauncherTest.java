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
 *
 * Changed Chapter 18:
 * Code not in GOOS.
 * - Updated to use Item on AuctionHouse.auctionFor().
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
        Item item = new Item("item 123", 789);
        when(auctionHouse.auctionFor(item)).thenReturn(auction);
        InOrder orderedVerifier = inOrder(auction, sniperCollector, auction);

        sniperLauncher.joinAuction(item);

        orderedVerifier.verify(auction).addAuctionEventListener(any(AuctionSniper.class));
        orderedVerifier.verify(sniperCollector).addSniper(any(AuctionSniper.class));
        orderedVerifier.verify(auction).join();
    }
}
