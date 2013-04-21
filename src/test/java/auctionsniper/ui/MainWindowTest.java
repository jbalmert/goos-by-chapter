package auctionsniper.ui;

import auctionsniper.AuctionSniperDriver;
import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;


/**
 * Added Chapter 16:
 * Code from GOOS, pg 186
 * - Created makesUserRequestWhenJoinButtonClicked to force the MainWindow to fire off an event
 *     to the new UserRequestListener to handle joining an auction.
 */
public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe =
                new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");

        mainWindow.addUserRequestListener(
            new UserRequestListener() {
                @Override
                public void joinAuction(String itemId) {
                    buttonProbe.setReceivedValue(itemId);
                }
            });

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
}
