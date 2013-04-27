package auctionsniper;

import auctionsniper.ui.MainWindow;
import static auctionsniper.ui.SnipersTableModel.*;

/**
 * Added Chapter 10:
 * Original code form consists of stubs to allow compilation.
 *
 * Changed Chapter 11:
 * Code from GOOS, pg. 90.
 * - Application started in new thread.
 * - Assume a bidding on a single item for now.
 * - No error handling.  Exceptions are simply printed out.
 * - Dispose of the window after the test is done.
 *
 * Changed Chapter 12:
 * Code from GOOS, pg 110.
 * - Adding implementation for showsSniperIsBidding
 *
 * Changed Chapter 14:
 * Code not listed, but forced to implement from changes to AuctionSniperEndToEndTest on pg 140
 * - Added method to verify sniper is winning an auction.
 * - Added method to verify sniper has won an auction.
 *
 * Changed Chapter 15:
 * Code from GOOS, pg 153, 169
 * - Added an itemId field
 * - Responding to the updated sniperWinsAnAuctionByBiddingHigher() test driver.showSniperStatus is now
 *     expected to be able to verify values of itemId, lastPrice, and lastBid, in addition to the original
 *     status text.
 * - Added assertions for the JTable column headers.
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 176, 177, 184
 * - Refactored to have an auction sent as a parameter on all the assertion methods (sniperHasShownIsBidding(), etc) in
 *     preparation for bidding in multiple auctions.
 * - Changed startBiddingIn to accept a variable number of auctions as input.
 * - Changed startBiddingIn to leverage the new input field and "Join Auction" button as the data entry point for the
 *     application rather than directly inject the values into JTable model.
 *
 * Changed Chapter 18:
 * Code not in GOOS, but described on pg 207.
 * - Added startBiddingWithStopPrice().  To keep backward compatibility with startBiddingIn(), the implementation
 *     of startBiddingIn() was changed to call startBiddingWithStopPrice() with a default stopPrice of
 *     Integer.MAX_VALUE.
 * - Added hasShownSniperIsLosing() to drive out new LOSING state.
 */
public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    private final SniperSnapshot JOINING = SniperSnapshot.joining("");

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startsBiddingWithStopPrice(Integer.MAX_VALUE, auctions);
    }

    public void startsBiddingWithStopPrice(int stopPrice, FakeAuctionServer... auctions) {
        startSniper(auctions);
        for (FakeAuctionServer auction: auctions) {
            final String itemId = auction.getItemId();
            driver.startBiddingFor(itemId, stopPrice);
            driver.showsSniperStatus(itemId, 0, 0, textFor(SniperSnapshot.SniperState.JOINING));
        }
    }

    public void startSniper(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(arguments(auctions));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    protected static String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        for (int i=0; i< auctions.length; i++) {
            arguments[i+3] = auctions[i].getItemId();
        }
        return arguments;
    }



    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid,
                MainWindow.STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid,
                MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice,
                MainWindow.STATUS_WON);
    }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid,
                MainWindow.STATUS_LOSING);
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid,
                MainWindow.STATUS_LOST);
    }
}
