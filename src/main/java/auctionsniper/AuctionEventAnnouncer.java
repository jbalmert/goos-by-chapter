package auctionsniper;

import java.util.HashSet;
import java.util.Set;

/**
 * Added Chapter 17:
 * Code not found in GOOS.
 * - This is a stand in for the Announcer code I opted not to implement or reference from JMock.  It
 *     broadcasts messages to all subscribed listeners.
 */
public class AuctionEventAnnouncer implements AuctionEventListener {
    Set<AuctionEventListener> listeners = new HashSet<AuctionEventListener>();

    public void addListener(AuctionEventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void auctionClosed() {
        for (AuctionEventListener listener: listeners) {
            listener.auctionClosed();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource source) {
        for (AuctionEventListener listener: listeners) {
            listener.currentPrice(price, increment, source);
        }
    }
}
