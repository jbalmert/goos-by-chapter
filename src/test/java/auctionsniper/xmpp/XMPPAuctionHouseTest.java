package auctionsniper.xmpp;

import auctionsniper.*;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Added Chapter 17:
 * Code from GOOS, pg 195
 * - Created receivesEventsFromAuctionServerAfterJoining() to prove the new XMPPAuction code can interact with
 *     its event listeners correctly.
 * - The code in the book has a few errors:
 *     The server variable is actually auctionServer.
 *     Before the auction can join, the server must first start selling an item.
 * - Moved into xmpp package, as described in GOOS, pg 195.
 * - Renamed to XMPPAuctionHouseTest as described in GOOS, pg 197.  Replaced XMPPConnection reference with
 *     AuctionHouse reference to fix the integration tests broken by the refactoring of adding the AuctionHouse
 *     interface to Main.
 */
public class XMPPAuctionHouseTest {

    private AuctionHouse auctionHouse;
    private FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private String hostname = "localhost";
    private String username = "sniper";
    private String password = "sniper";
    private final String AUCTION_RESOURCE = "Auction";

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(hostname, username, password);
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auctionServer.startSellingItem();
        auction.join();
        auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auctionServer.announcesClosed();
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {

            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource fromSniper) {
                // not implemented
            }
        };
    }
}
