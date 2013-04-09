package auctionsniper;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static auctionsniper.SniperSnapshot.SniperState.*;
import static auctionsniper.SniperSnapshot.SniperState;

/**
 * Added Chapter 15:
 * The code is not in the book, but it does mention that they created the tests to keep themselves honest on pg 165.
 * The testing is concerned with making sure the state transitions when the auction closes are correct.
 */

public class SniperStateTest {

    @Test
    public void joiningTransitionsToLostWhenAuctionCloses() {
        assertSniperStateTransitionsTo(JOINING, LOST);
    }

    @Test
    public void biddingTransitionsToLostWhenAuctionCloses() {
        assertSniperStateTransitionsTo(BIDDING, LOST);
    }

    @Test
    public void winningTransitionsToWonWhenAuctionCloses() {
        assertSniperStateTransitionsTo(WINNING, WON);
    }

    @Test(expected=Exception.class)
    public void lostCannotTransitionToAnotherState() {
        LOST.whenAuctionClosed();
    }

    @Test(expected=Exception.class)
    public void wonCannotTransitionToAnotherState() {
        WON.whenAuctionClosed();
    }

    private void assertSniperStateTransitionsTo(SniperState currentState, SniperState targetState) {
        assertEquals(targetState, currentState.whenAuctionClosed());
    }
}
