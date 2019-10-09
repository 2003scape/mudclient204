import java.math.BigInteger;

public class Buffer {

    /*static CRC32 unusedCRC = new CRC32();
    private static int unusedArray[] = {
            0, 1, 3, 7, 15, 31, 63, 127, 255, 511,
            1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff,
            0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff,
            0x3fffffff, 0x7fffffff, -1
    };*/
    public byte buffer[];
    public int offset;

    public Buffer(byte buff[]) {
        buffer = buff;
        offset = 0;
    }

    public void putByte(int i) {
        buffer[offset++] = (byte) i;
    }

    public void putInt(int i) {
        buffer[offset++] = (byte) (i >> 24);
        buffer[offset++] = (byte) (i >> 16);
        buffer[offset++] = (byte) (i >> 8);
        buffer[offset++] = (byte) i;
    }

    public void putString(String s) {
        //s.getBytes(0, s.length(), buffer, offset);
        System.arraycopy(s.getBytes(), 0, buffer, offset, s.length());
        offset += s.length();
        buffer[offset++] = 10; // null terminate
    }

    public void putBytes(byte src[], int srcPos, int len) {
        //for (int k = srcPos; k < srcPos + len; k++)
        //    buffer[offset++] = src[k];
        System.arraycopy(src, srcPos, buffer, offset, len);
        offset += len;
    }

    public int getUnsignedByte() {
        return buffer[offset++] & 0xff;
    }

    public int getUnsignedShort() {
        offset += 2;
        return ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
    }

    public int getUnsignedInt() {
        offset += 4;
        return ((buffer[offset - 4] & 0xff) << 24) + ((buffer[offset - 3] & 0xff) << 16) + ((buffer[offset - 2] & 0xff) << 8) + (buffer[offset - 1] & 0xff);
    }

    public void getBytes(byte dest[], int destPos, int len) {
        //for (int k = destPos; k < destPos + len; k++)
        //    dest[k] = buffer[offset++];
        System.arraycopy(buffer, offset, dest, destPos, len);
        offset += len;
    }

    public void encrypt(BigInteger exponent, BigInteger modulus) {
        int i = offset;
        offset = 0;
        byte buf[] = new byte[i];
        getBytes(buf, 0, i);
        BigInteger biginteger2 = new BigInteger(buf);
        BigInteger biginteger3 = biginteger2.modPow(exponent, modulus);
        byte abyte1[] = biginteger3.toByteArray();
        offset = 0;
        putByte(abyte1.length);
        putBytes(abyte1, 0, abyte1.length);
    }

}
