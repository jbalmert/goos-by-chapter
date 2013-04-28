package auctionsniper.xmpp;

/**
 * Added Chapter 19:
 * Code from GOOS, pg 222
 */
public interface XMPPFailureReporter {
    void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);
}
