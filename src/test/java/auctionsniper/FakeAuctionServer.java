package auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static java.lang.String.format;

/**
 * Added Chapter 10:
 * Original code form consists of stubs to allow compilation.
 *
 * Changed Chapter 11:
 * Basic implementation to allow first use case to be completed.
 * From GOOS, pg 93-94
 * - Creates connection to XMPPConnection.  As stated in the notes at the beginning of the chapter,
 *     this calls assumes the OpenFire server is running locally for development purposes.
 * - Defines SingleMessageListener inner class to wrap a blocking queue allowing message at a time.
 * - Diverges slightly from the original code to disable SASL authentication.  This was the simplest
 *     option to get around an authentication error not mentioned in the original text.
 *
 * Changed Chapter 12:
 * Implementing "single item, bid, but still lose" use case.
 * From GOOS, pg 107, 108
 * - Adding method to report price to sniper.
 * - Adding method to assert that the server has received a bid from the sniper.
 * - Generalize SingleMessageListener.receivesAMessage to allow it to accept different message types.
 * - Removed specialized connection code.  See comments in Main.java for full explanation.
 * - Changed implementation of announceClosed() to send a real message.
 */
public class FakeAuctionServer {
    public static final String ITEM_ID_AS_LOGIN ="auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    private static final String AUCTION_PASSWORD = "auction";

    private final SingleMessageListener messageListener = new SingleMessageListener();
    private String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        connection = new XMPPConnection(XMPP_HOSTNAME);
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId),
                AUCTION_PASSWORD, AUCTION_RESOURCE);
        connection.getChatManager().addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createLocally) {
                        currentChat = chat;
                        chat.addMessageListener(messageListener);
                    }
                }
        );
    }

    public void announcesClosed() throws XMPPException {
        currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    public void reportPrice(int price, int increment, String bidder) throws XMPPException {
        currentChat.sendMessage(
            String.format("SOLVersion: 1.1; Event: PRICE; "
                + "CurrentPrice: %d; Increment: %d; Bidder: %s",
                price, increment, bidder));
    }

    public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
    }

    public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(format(Main.BID_COMMAND_FORMAT, bid)));
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher)
            throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

    public class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages =
                new ArrayBlockingQueue<Message>(1);

        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        public void receivesAMessage(Matcher<? super String> messageMatcher) throws  InterruptedException {
            final Message message = messages.poll(5, TimeUnit.SECONDS);
            assertThat("Message", message, is(notNullValue()));
            assertThat(message.getBody(), messageMatcher);
        }
    }
}
