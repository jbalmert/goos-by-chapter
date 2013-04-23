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
 *
 * * Changed Chapter 15:
 * Code from GOOS, pg 155, 160, 164
 * - Added SniperSnapshot to SniperListener.sniperBidding() calls to pass the information on current bidding
 *     status for display.
 * - Changed currentPrice() to use sniperListener.sniperStateChanged() instead of sniperBidding().  This single
 *     method can now handle listening to all possible state changes rather than having a separate method for
 *     each state (bidding, winning, losing, etc.).
 * - Changed implementation of currentPrice to switch on priceSource rather than use the isWinning flag.
 * - Changed implementation of auctionClosed() to leverage snapshot.closed();
 *
 * Changed Chapter 17:
 * - Changed the order of the constructor arguments to match the book.  I'm not sure if I just got these wrong before
 * or the text is inconsistent.
 */
public class AuctionSniper implements AuctionEventListener{
    private SniperSnapshot snapshot;
    private SniperListener sniperListener;
    private final Auction auction;
    private boolean isWinning;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);

    }

    public void addSniperListener(SniperListener listener) {
        this.sniperListener = listener;
    }

    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    private void notifyChange() {
        sniperListener.sniperStateChanged(snapshot);
    }

}
