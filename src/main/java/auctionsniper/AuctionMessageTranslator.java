package auctionsniper;

import static  auctionsniper.AuctionEventListener.PriceSource.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;

/**
 * Added Chapter 12:
 * Code from GOOS, pg 115
 * This class was extracted from Main.java as it was a separate responsibility.
 * - Add processMessage() and its implementation to handle a closed auction.
 * - Adding ability to distinguish between CLOSE and PRICE events.
 *
 * Changed Chapter 13:
 * Code from GOOS, pg 135
 * - An AuctionEvent class is extracted to own the responsibility of parsing event messages
 *
 * * Added Chapter 14:
 * - Added sniperId parameter to AuctionMessageTranslator constructor to allow the translator to determine if the
 *     current bid is from the sniper or someone else.
 */
public class AuctionMessageTranslator implements MessageListener {
    private final AuctionEventListener listener;
    private final String sniperId;

    public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
        this.listener = listener;
        this.sniperId = sniperId;
    }

    public void processMessage(Chat chat, Message message) {
        AuctionEvent event = AuctionEvent.from(message.getBody());

        String eventType = event.type();
        if ("CLOSE".equals(eventType)) {
            listener.auctionClosed();
        } else if("PRICE".equals(eventType)) {
            listener.currentPrice(event.currentPrice(),
                    event.increment(), event.isFrom(sniperId));
        }
    }

    private HashMap<String, String> unpackEventFrom(Message message) {
        HashMap<String, String> event = new HashMap<String, String>();
        for (String element : message.getBody().split(";")) {
            String[] pair = element.split(":");
            event.put(pair[0].trim(), pair[1].trim());
        }
        return event;
    }

    private static class AuctionEvent {
        HashMap<String, String> fields = new HashMap<String, String>();
        public String type() {
            return get("Event");
        }

        public int currentPrice() {
            return getInt("CurrentPrice");
        }

        public int increment() {
            return getInt("Increment");
        }

        private int getInt(String fieldName) {
            return Integer.parseInt(get(fieldName));
        }

        private String get(String fieldName) {
            return fields.get(fieldName);
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field: fieldsIn(messageBody))  {
                event.addField(field);
            }
            return event;
        }

        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }


        public AuctionEventListener.PriceSource isFrom(String sniperId) {
            return sniperId.equals(bidder()) ? FromSniper : FromOtherBidder;
        }

        private String bidder() {
           return get("Bidder");
        }
    }
}
