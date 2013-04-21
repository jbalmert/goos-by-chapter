package auctionsniper;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.*;
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
 *
 * Changed Chapter 16:
 * Code from GOOS, pg 184
 * - Added capability to use new input field on UI for adding new auction itemIds to bid on.  The driver
 *     can now inject the item id into the text field, and then click the button to fire off the event to add
 *     it to the JTable.
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

    public void startBiddingFor(String itemId) {
        itemIdField().replaceAllText(itemId);
        bidButton().click();
    }

    private JTextFieldDriver itemIdField() {
        JTextFieldDriver newItemId =
                new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
        newItemId.focusWithMouse();
        return newItemId;
    }

    private JButtonDriver bidButton() {
        return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
    }
}
