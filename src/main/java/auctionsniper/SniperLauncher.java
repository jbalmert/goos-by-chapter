package auctionsniper;

import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.AuctionHouse;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * Added Chapter 17:
 * Code from GOOS, pg 197
 *
 * Changed Chapter 18:
 * Code not in GOOS.
 * - Updated to accept Item instead of Strings as mandated by the UserRequestListener.
 * - Pushed the new Item into the AuctionSniper constructor as well.
 */
public class SniperLauncher implements UserRequestListener {

    private AuctionHouse auctionHouse;
    private SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
