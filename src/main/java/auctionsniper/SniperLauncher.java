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
 */
public class SniperLauncher implements UserRequestListener {

    private AuctionHouse auctionHouse;
    private SniperCollector collector;

    public SniperLauncher(AuctionHouse auctionHouse, SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
