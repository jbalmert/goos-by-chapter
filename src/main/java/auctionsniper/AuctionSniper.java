package auctionsniper;

/**
 * Added Chapter 13:
 * Code from GOOS, pg 124
 * The AuctionSniper now owns the AuctionEventListener interface that Main previously owned.
 *
 * Changed Chapter 14:
 * Code from GOOS, pg 143
 * - Updated currentPrice() to take into account the source of the event (PriceSource)
 *     to determine the action to take.
 * - Added flag to know if the sniper is winning an auction.
 * - Changed auctionClosed() to send a sniperWon() event if the auction closes when the sniper is winning.
 */
public class AuctionSniper implements AuctionEventListener{
    private final SniperListener sniperListener;
    private final Auction auction;
    private boolean isWinning;

    public AuctionSniper(Auction auction, SniperListener sniperListener) {
        this.sniperListener = sniperListener;
        this.auction = auction;
    }

    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
                sniperListener.sniperWinning();
        } else {
            auction.bid(price + increment);
            sniperListener.sniperBidding();
        }
    }
}
