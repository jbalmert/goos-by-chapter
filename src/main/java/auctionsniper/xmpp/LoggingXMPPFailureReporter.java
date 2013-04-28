package auctionsniper.xmpp;

import java.util.logging.Logger;

/**
 * Added Chapter 19:
 * Code from GOOS, pg 223
 */
public class LoggingXMPPFailureReporter implements XMPPFailureReporter{
    private final Logger logger;

    public LoggingXMPPFailureReporter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.severe(auctionId + " "
                + "Could not translate message \"" + failedMessage + "\" "
                + "because \"" + exception + "\"");
    }
}
