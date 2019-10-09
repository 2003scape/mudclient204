import java.io.IOException;

public class Packet {

    //static char charMap[];
    public static int anIntArray537[] = new int[256];
    public static int anIntArray541[] = new int[256];
    public int readTries;
    public int maxReadTries;
    public int packetStart;
    public byte packetData[];
    /*private static int anIntArray521[] = {
        0, 1, 3, 7, 15, 31, 63, 127, 255, 511,
        1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff,
        0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff,
        0x3fffffff, 0x7fffffff, -1
    };
    int anInt522 = 61;
    int anInt523 = 59;
    int anInt524 = 42;
    int anInt525 = 43;
    int anInt526 = 44; // index list for charMap
    int anInt527 = 45;
    int anInt528 = 46;
    int anInt529 = 47;
    int anInt530 = 92;
    int anInt531 = 32;
    int anInt532 = 124;
    int anInt533 = 34;    */
    public ISAAC isaacIncoming;
    public ISAAC isaacOutgoing;
    protected int length;
    protected int packetMaxLength;
    protected boolean socketException;
    protected String socketExceptionMessage;
    protected int delay;
    private int packetEnd;
    private int packet8Check;

    public Packet() {
        packetEnd = 3;
        packet8Check = 8;
        packetMaxLength = 5000;
        socketException = false;
        socketExceptionMessage = "";
    }

    public void seedIsaac(int seed[]) {
        // TODO toggle isaac
        //isaacIncoming = new ISAAC(seed);
        //isaacOutgoing = new ISAAC(seed);
    }

    public void closeStream() {
    }

    public void readBytes(int len, byte buff[])
            throws IOException {
        readStreamBytes(len, 0, buff);
    }

    public int readPacket(byte buff[]) {
        try {
            readTries++;
            if (maxReadTries > 0 && readTries > maxReadTries) {
                socketException = true;
                socketExceptionMessage = "time-out";
                maxReadTries += maxReadTries;
                return 0;
            }
            if (length == 0 && availableStream() >= 2) {
                length = readStream();
                if (length >= 160)
                    length = (length - 160) * 256 + readStream();
            }
            if (length > 0 && availableStream() >= length) {
                if (length >= 160) {
                    readBytes(length, buff);
                } else {
                    buff[length - 1] = (byte) readStream();
                    if (length > 1)
                        readBytes(length - 1, buff);
                }
                int i = length;
                length = 0;
                readTries = 0;
                return i;
            }
        } catch (IOException ioexception) {
            socketException = true;
            socketExceptionMessage = ioexception.getMessage();
        }
        return 0;
    }

    public int availableStream()
            throws IOException {
        return 0;
    }

    public void readStreamBytes(int i, int j, byte abyte0[])
            throws IOException {
    }

    public boolean hasPacket() {
        return packetStart > 0;
    }

    public void writePacket(int i)
            throws IOException {
        if (socketException) {
            packetStart = 0;
            packetEnd = 3;
            socketException = false;
            throw new IOException(socketExceptionMessage);
        }
        delay++;
        if (delay < i)
            return;
        if (packetStart > 0) {
            delay = 0;
            writeStreamBytes(packetData, 0, packetStart);
        }
        packetStart = 0;
        packetEnd = 3;
    }

    public void sendPacket() {
        if (isaacOutgoing != null) {
            int i = packetData[packetStart + 2] & 0xff;
            packetData[packetStart + 2] = (byte) (i + isaacOutgoing.getNextValue());
        }
        if (packet8Check != 8) // what the fuck is this even for? legacy?
            packetEnd++;
        int j = packetEnd - packetStart - 2;
        if (j >= 160) {
            packetData[packetStart] = (byte) (160 + j / 256);
            packetData[packetStart + 1] = (byte) (j & 0xff);
        } else {
            packetData[packetStart] = (byte) j;
            packetEnd--;
            packetData[packetStart + 1] = packetData[packetEnd];
        }
        if (packetMaxLength <= 10000) // this seems largely useless and doesn't appear to do anything
        {
            int k = packetData[packetStart + 2] & 0xff;
            anIntArray537[k]++;
            anIntArray541[k] += packetEnd - packetStart;
        }
        packetStart = packetEnd;
    }

    public void putBytes(byte src[], int srcPos, int len) {
        //for (int k = 0; k < len; k++)
        //    packetData[packetEnd++] = src[srcPos + k];
        System.arraycopy(src, srcPos, packetData, packetEnd, len);
        packetEnd += len;

    }

    public void putLong(long l) {
        putInt((int) (l >> 32));
        putInt((int) (l & -1L));
    }

    public void newPacket(int i) {
        if (packetStart > (packetMaxLength * 4) / 5)
            try {
                writePacket(0);
            } catch (IOException ioexception) {
                socketException = true;
                socketExceptionMessage = ioexception.getMessage();
            }
        if (packetData == null)
            packetData = new byte[packetMaxLength];
        packetData[packetStart + 2] = (byte) i;
        packetData[packetStart + 3] = 0;
        packetEnd = packetStart + 3;
        packet8Check = 8;
    }

    public void writeStreamBytes(byte abyte0[], int i, int j)
            throws IOException {
    }

    public int readStream()
            throws IOException {
        return 0;
    }

    public long getLong()
            throws IOException {
        long l = getShort();
        long l1 = getShort();
        long l2 = getShort();
        long l3 = getShort();
        return (l << 48) + (l1 << 32) + (l2 << 16) + l3;
    }

    public void putShort(int i) {
        packetData[packetEnd++] = (byte) (i >> 8);
        packetData[packetEnd++] = (byte) i;
    }

    public void putInt(int i) {
        packetData[packetEnd++] = (byte) (i >> 24);
        packetData[packetEnd++] = (byte) (i >> 16);
        packetData[packetEnd++] = (byte) (i >> 8);
        packetData[packetEnd++] = (byte) i;
    }

    public int getShort()
            throws IOException {
        int i = getByte();
        int j = getByte();
        return i * 256 + j;
    }

    public void putString(String s) {
        //s.getBytes(0, s.length(), packetData, packetEnd);
        System.arraycopy(s.getBytes(), 0, packetData, packetEnd, s.length());
        packetEnd += s.length();
    }

    public void putByte(int i) {
        packetData[packetEnd++] = (byte) i;
    }

    public int isaacCommand(int i) {
        // TODO toggle isaac
        //return i - isaacIncoming.getNextValue() & 0xff;
        return i;
    }

    public int getByte()
            throws IOException {
        return readStream();
    }

    public void flushPacket()
            throws IOException {
        sendPacket();
        writePacket(0);
    }
    // public static int anInt543;

    /*static
    {
        charMap = new char[256];
        for(int i = 0; i < 256; i++)
            charMap[i] = (char)i;

        charMap[61] = '=';
        charMap[59] = ';';
        charMap[42] = '*';
        charMap[43] = '+';
        charMap[44] = ',';
        charMap[45] = '-';
        charMap[46] = '.';
        charMap[47] = '/';
        charMap[92] = '\\';
        charMap[124] = '|';
        charMap[33] = '!';
        charMap[34] = '"';
    }            */
}
