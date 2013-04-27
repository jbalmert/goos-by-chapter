package auctionsniper.ui;

import auctionsniper.AuctionSniperDriver;
import auctionsniper.Item;
import auctionsniper.SniperPortfolio;
import auctionsniper.UserRequestListener;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;


/**
 * Added Chapter 16:
 * Code from GOOS, pg 186
 * - Created makesUserRequestWhenJoinButtonClicked to force the MainWindow to fire off an event
 *     to the new UserRequestListener to handle joining an auction.
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 209
 * - Updated test to use Item on the userRequestListener.
 */
public class MainWindowTest {
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<Item>(equalTo(new Item("an item-id", 789)), "join request");

        mainWindow.addUserRequestListener(
            new UserRequestListener() {
                @Override
                public void joinAuction(Item item) {
                    itemProbe.setReceivedValue(item);
                }
            });

        driver.startBiddingFor("an item-id", 789);
        driver.check(itemProbe);
    }
}
