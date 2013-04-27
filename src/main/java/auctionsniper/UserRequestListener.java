package auctionsniper;

/**
 * Added Chapter 16:
 * Code from GOOS, pg 186
 *
 * Changed Chapter 18:
 * Code from GOOS, pg 209
 * - Changed parameter of joinAuction() to be an Item instead of a String.
 */
public interface UserRequestListener {
    void joinAuction(Item item);
}
