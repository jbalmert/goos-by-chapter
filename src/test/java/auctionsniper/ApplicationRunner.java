package auctionsniper;

import auctionsniper.ui.MainWindow;

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
 * Chaged Chapter 12:
 * Code from GOOS, pg 110.
 * - Adding implementation for showsSniperIsBidding
 */
public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String STATUS_JOINING = "Joining";
    private static final String STATUS_LOST = "Lost";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
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
        driver.showsSniperStatus(STATUS_JOINING);

    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
    }
}
