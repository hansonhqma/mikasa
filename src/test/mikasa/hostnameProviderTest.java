import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

public final class hostnameProviderTest {

    @Test
    public void hexRepresentationTest() throws IOException {
        InetAddress addr = hostnameProvider.getPublicAddress();
        String beforeEncoding = addr.getHostName();
        InetAddress addrDecoded = hostnameProvider.hexToAddr(hostnameProvider.addrToHex(addr));
        String afterDecryption = addrDecoded.getHostName();
        assertTrue(beforeEncoding.equals(afterDecryption));
    }
}
