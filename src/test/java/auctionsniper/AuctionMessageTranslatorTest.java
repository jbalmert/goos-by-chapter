package auctionsniper;

import static org.mockito.Mockito.*;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
/**
 * Added Chapter 12:
 * Code from GOOS, pg 114, 118, 119
 * Added initializeTranslator() to allow Mockito to create a mock instance of 'listener' before it is injected
 * into the 'translator'.
 * - notifiesAuctionClosedWhenClosedMessageReceived() forces a basic implementation
 * - notifiesBidDetailsWhenCurrentPriceMessageReceived() forces a more complex implementation capable of
 *     distinguishing different messages.
 *
 * Added Chapter 14:
 * - Added sniperId parameter to AuctionMessageTranslator constructor to allow the translator to determine if the
 * current bid is from the sniper or someone else.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "Sniper";

    @Mock
    AuctionEventListener listener;
    AuctionMessageTranslator translator;

    @Before
    public void initializeTranslator() {
        translator = new AuctionMessageTranslator(SNIPER_ID, listener);
    }

    @Test
    public void notifiesAuctionClosedWhenClosedMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(192, 7, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(234, 5, AuctionEventListener.PriceSource.FromSniper);
    }
}
