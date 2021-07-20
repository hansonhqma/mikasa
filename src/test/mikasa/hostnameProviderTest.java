import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;

public final class hostnameProviderTest {

    @Test
    public void hexRepresentationTest() throws IOException {
        InetAddress addr = hostnameProvider.getPublicAddress();
        String beforeEncryption = addr.getHostName();
        InetAddress addrDecrypted = hostnameProvider.hexToAddr(hostnameProvider.addrToHex(addr));
        String afterDecryption = addrDecrypted.getHostName();
        assertTrue(beforeEncryption.equals(afterDecryption));
    }
}
