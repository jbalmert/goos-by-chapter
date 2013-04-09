package auctionsniper;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import org.hamcrest.Matcher;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

/**
 * Added Chapter 11:
 * Original code from GOOS, pg 91
 * - Allows assertion of auction status so test conditions can be asserted.
 *
 * Changed Chapter 15:
 * Code from GOOS, pg 153
 * - Added an alternate implementation of showSniperStatus() which takes into account itemId, lastPrice,
 *     and lastBid in addition to the original status.  I left the original implementation because the
 *     current code snippets in the book make it unclear if the call to showSniperStatus() in
 *     ApplicationRunner.startBiddingIn() should be updated to use the new method signature.
 */
public class AuctionSniperDriver extends JFrameDriver{
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
                JFrameDriver.topLevelFrame(
                        named(Main.MAIN_WINDOW_NAME),
                        showingOnScreen()),
                new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String statusText) {
        new JTableDriver(this).hasCell(withLabelText(equalTo(statusText)));
    }

    public void showsSniperStatus(String itemId, int lastPrice, int lastBid,
                                  String statusText) {
        JTableDriver table = new JTableDriver(this);
        table.hasRow(
                matching(withLabelText(itemId), withLabelText(String.valueOf(lastPrice)),
                        withLabelText(String.valueOf(lastBid)), withLabelText(statusText)));
    }

    public void hasColumnTitles() {
        JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
        headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"),
                withLabelText("Last Bid"), withLabelText("State")));
    }
}
