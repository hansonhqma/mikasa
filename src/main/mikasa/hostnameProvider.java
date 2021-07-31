import java.net.*;
import java.io.*;
import java.util.ArrayList;

/** hostnameProvider documentation
 *
 * A class that handles and provides all node network information including
 * 1. active public address
 * 2. network load
 * 3. network devices
 * 4. encoding and decoding hostnames to and from hexidecimal format
 *
 */

public class hostnameProvider {

    /**
     * @return Inet4Address object of the active public address
     * @throws IOException
     */

    public static InetAddress getPublicAddress() throws IOException{
        URL source = new URL("http://checkip.amazonaws.com");
        String publicIP = new BufferedReader(new InputStreamReader(source.openStream())).readLine(); // array of length 4

        return InetAddress.getByName(publicIP); // creates Inet4Address object and returns
    }

    /**
     * @param inetaddr InetAddress to be encrypted
     * @return String value of hex address representation
     */

    public static String addrToHex(InetAddress inetaddr){
        String[] nums = inetaddr.getHostAddress().split("\\.");
        StringBuilder val = new StringBuilder();
        for (String num : nums) {
            int temp = Integer.parseInt(num);
            String hex = Integer.toHexString(temp);
            if(hex.length() == 1){hex = "0"+hex;}
            val.append(hex);
        }
        return val.toString();
    }

    /**
     * @param hexaddr String to be decoded
     * @return InetAddress object of hex address
     * @throws UnknownHostException if the deciphered hex string isn't a valid address
     */

    public static InetAddress hexToAddr(String hexaddr) throws UnknownHostException{
        ArrayList<String> nums= new ArrayList<>();
        for (int i = 0; i < hexaddr.length(); i+=2) {
            String k = hexaddr.substring(i, i+2).toUpperCase();
            int temp = Integer.parseInt(k, 16);
            String dec = Integer.toString(temp);
            nums.add(dec);
        }
        return InetAddress.getByName(String.join(".", nums));
    }

    /**
     * @throws IOException
     */
    public static void quickTest() throws IOException{
        InetAddress ip = getPublicAddress();
        System.out.println("public IP: " + ip.getHostAddress());
        String hex = addrToHex(ip);
        System.out.println("encoded: "+hex);
        InetAddress back = hexToAddr(hex);
        System.out.println("decoded: "+back.getHostAddress());
    }

    public static void main(String[] args) throws IOException{
        quickTest();
    }
}
