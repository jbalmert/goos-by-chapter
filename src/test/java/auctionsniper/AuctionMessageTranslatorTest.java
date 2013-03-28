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
 * Code from GOOS, pg 114
 * Added initializeTranslator() to allow Mockito to create a mock instance of 'listener' before it is injected
 * into the 'translator'.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;

    @Mock
    AuctionEventListener listener;
    AuctionMessageTranslator translator;

    @Before
    public void initializeTranslator() {
        translator = new AuctionMessageTranslator(listener);
    }

    @Test
    public void notifiesAuctionClosedWhenClosedMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }
}
