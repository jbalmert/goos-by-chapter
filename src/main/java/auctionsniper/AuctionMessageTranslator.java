package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Added Chapter 12:
 * Code from GOOS, pg 115
 * This class was extracted from Main.java as it was a separate responsibility.
 * <ul>
 *     <li>Add processMessage() and its implementation to handle a closed auction.</li>
 * </ul>
 *
 */
public class AuctionMessageTranslator implements MessageListener {
    private final AuctionEventListener listener;

    public AuctionMessageTranslator(AuctionEventListener listener) {
        this.listener = listener;
    }

    public void processMessage(Chat chat, Message message) {
        listener.auctionClosed();
    }
}
