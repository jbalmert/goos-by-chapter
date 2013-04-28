package auctionsniper.xmpp;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.mockito.Mockito.verify;

/**
 * Added Chapter 19:
 * Code from GOOS, pg 223
 */
@RunWith(MockitoJUnitRunner.class)
public class LoggingXMPPFailureReporterTest {
    @Mock Logger logger;
    LoggingXMPPFailureReporter reporter;

    @Before
    public void constructFailureReporter() {
        reporter = new LoggingXMPPFailureReporter(logger);
    }

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    public void writerMessageTranslationFailureToLog() {
        reporter.cannotTranslateMessage("<auction id>", "bad message", new Exception("bad"));

        verify(logger).severe("<auction id> "
                            + "Could not translate message \"bad message\" "
                            + "because \"java.lang.Exception: bad\"");
    }
}
