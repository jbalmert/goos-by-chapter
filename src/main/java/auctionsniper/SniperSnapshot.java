package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Added Chapter 15:
 * Code from GOOS, pg 154, 163, 164, 165
 * - This is an attempt to "bundle up" several parameters that are sent around together frequently.
 * - The equals(), hashCode(), and toString() methods are not listed in the book, but it does mention
 *     that they used Apache commons.lang to generate them.  As these will be needed, they are implemented
 *     here.
 * - Renamed to SniperSnapshot.  This is mentioned on pg 159. SniperState would be too close to SniperStatus, so
 *     the author opted to go with snapshot to represent the current state of the sniper.
 * - Since the SniperState concept is now available, it is used to define an enum of all the allowed sniper
 *     states.
 * - Added bidding(), joining(), winning() state change methods.  These return new instances of SniperSnapshot with a
 *     new SniperState.  In effect, this allows the SniperState to act as a state machine, changing from one state
 *     to another as the auction progresses.
 * - Added closed() state change methods, which references SniperState.whenAuctionClosed().  This maps a state to
 *     the state the sniper would be in if the auction closed at that time.
 * - Added implementations for whenAuctionClosed().
 */
public class SniperSnapshot {
    public final String itemId;
    public final int lastPrice;
    public final int lastBid;
    public SniperState state;

    public SniperSnapshot closed() {
        return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionClosed());
    }

    public enum SniperState {
        JOINING {
            @Override public SniperState whenAuctionClosed() {
                return LOST;
            }
        },
        BIDDING {
            @Override public SniperState whenAuctionClosed() {
                return LOST;
            }
        },
        WINNING {
            @Override public SniperState whenAuctionClosed() {
                return WON;
            }
        },
        LOST,
        WON;

        public SniperState whenAuctionClosed() {
            throw new Defect("Auction is already closed");
        }

        private class Defect extends RuntimeException {
            public Defect(String message) {
                super(message);
            }
        }
    }

    public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState sniperState)  {
        this.itemId = itemId;
        this.lastPrice = lastPrice;
        this.lastBid = lastBid;
        this.state = sniperState;
    }

    public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
        return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
    }

    public SniperSnapshot winning(int newLastPrice) {
        return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
    }

    public static SniperSnapshot joining(String itemId) {
        return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
