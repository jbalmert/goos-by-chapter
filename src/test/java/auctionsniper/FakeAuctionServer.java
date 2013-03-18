package auctionsniper;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Added Chapter 10:
 * Original code form consists of stubs to allow compilation.
 *
 * Changed Chapter 11:
 * Basic implementation to allow first use case to be completed.
 * From GOOS, pg 93-94
 * <ul>
 *     <li>Creates connection to XMPPConnection.  As stated in the notes at the beginning of the chapter,
 *     this calls assumes the OpenFire server is running locally for development purposes.</li>
 *     <li>Defines SingleMessageListener inner class to wrap a blocking queue allowing message at a time.</li>
 *     <li>Diverges slightly from the original code to disable SASL authentication.  This was the simplest
 *     option to get around an authentication error not mentioned in the original text.</li>
 * </ul>
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
        ConnectionConfiguration cc = createConnectionConfiguration();  // different than original
        connection = new XMPPConnection(cc);                           // see createConnectionConfiguration javadoc
    }

    public void startSellingItem() throws XMPPException {
        connection.connect();
        connection.login(String.format(ITEM_ID_AS_LOGIN, itemId),
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

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receiveAMessage();
    }

    public void announcesClosed() throws XMPPException {
        currentChat.sendMessage(new Message());
    }

    public void stop() {
        connection.disconnect();
    }

    public String getItemId() {
        return itemId;
    }

    /**
     * This is different from the original code.  Using OpenFire 3.8, it appears the default connection mode
     * uses SASL.  Rather than worry about configuring encryption correctly, this implementation explicitly
     * disables the SASL authentication.
     */
    private ConnectionConfiguration createConnectionConfiguration() {
        ConnectionConfiguration cc = new ConnectionConfiguration(XMPP_HOSTNAME);
        cc.setSASLAuthenticationEnabled(false);
        return cc;
    }

    public class SingleMessageListener implements MessageListener {
        private final ArrayBlockingQueue<Message> messages =
                new ArrayBlockingQueue<Message>(1);

        public void processMessage(Chat chat, Message message) {
            messages.add(message);
        }

        public void receiveAMessage() throws  InterruptedException {
            assertThat("Message", messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
        }
    }
}
