**Coming soon...**

## Design

---

### `hostnameProvider`

Handles public address information, encoding and decoding ip addresses

**Methods**

- `public static InetAddress getPublicAddress()`

Returns an `InetAddress` object referring to the public address of the machine this method is called from.

- `public static String addrToHex(InetAddress inetaddr)`

Takes `InetAddress` object and returns the string encoding of the address in 8-length hex format

- `public static InetAddress hexToAddr(String hexaddr)`

Takes String `hexaddr` in the format of an IPv4 address and returns InetAddress object referring to the given address





### `node`

Where `main` resides, stores all network information including neighbors and complete network.

