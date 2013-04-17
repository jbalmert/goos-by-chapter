package auctionsniper;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124
 * The AuctionSniper now owns the AuctionEventListener interface that Main previously owned.
 */
public class AuctionSniper implements AuctionEventListener{
    private final SniperListener sniperListener;
    private final Auction auction;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.sniperListener = sniperListener;
        this.auction = auction;
    }

    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment) {
        sniperListener.sniperBidding();
        auction.bid(price + increment);
    }
}
