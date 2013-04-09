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
 */
public class ApplicationRunner {
    private String itemId;
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String STATUS_LOST = "Lost";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    private final SniperSnapshot JOINING = SniperSnapshot.joining("");

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        itemId = auction.getItemId();
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
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
        driver.showsSniperStatus(JOINING.itemId, JOINING.lastPrice,
                JOINING.lastBid, textFor(SniperSnapshot.SniperState.JOINING));
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(int lastPrice, int lastBid) {
        driver.showsSniperStatus(itemId, lastPrice, lastBid,
                MainWindow.STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning(int winningBid) {
        driver.showsSniperStatus(itemId, winningBid, winningBid,
                MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction(int lastPrice) {
        driver.showsSniperStatus(itemId, lastPrice, lastPrice,
                MainWindow.STATUS_WON);
    }
}
