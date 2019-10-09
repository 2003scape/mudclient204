public class ChatMessage {

    public static byte scrambledbytes[] = new byte[100];
    public static char chars[] = new char[100];
    private static char charmap[] = {
            ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r',
            'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
            'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?',
            '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\',
            '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
            ']'
    };

    public static String descramble(byte buff[], int off, int len) {
        try {
            int newLen = 0;
            int l = -1;
            for (int idx = 0; idx < len; idx++) {
                int current = buff[off++] & 0xff;
                int k1 = current >> 4 & 0xf;
                if (l == -1) {
                    if (k1 < 13)
                        chars[newLen++] = charmap[k1];
                    else
                        l = k1;
                } else {
                    chars[newLen++] = charmap[((l << 4) + k1) - 195];
                    l = -1;
                }
                k1 = current & 0xf;
                if (l == -1) {
                    if (k1 < 13)
                        chars[newLen++] = charmap[k1];
                    else
                        l = k1;
                } else {
                    chars[newLen++] = charmap[((l << 4) + k1) - 195];
                    l = -1;
                }
            }

            boolean flag = true;
            for (int l1 = 0; l1 < newLen; l1++) {
                char c = chars[l1];
                if (l1 > 4 && c == '@')
                    chars[l1] = ' ';
                if (c == '%')
                    chars[l1] = ' ';
                if (flag && c >= 'a' && c <= 'z') {
                    chars[l1] += '\uFFE0';// ????? ￠ 65504
                    flag = false;
                }
                if (c == '.' || c == '!')
                    flag = true;
            }

            return new String(chars, 0, newLen);
        } catch (Exception Ex) {
            return ".";
        }
    }

    public static int scramble(String s) {
        if (s.length() > 80)
            s = s.substring(0, 80);
        s = s.toLowerCase();
        int off = 0;
        int lshift = -1;
        for (int k = 0; k < s.length(); k++) {
            char currentchar = s.charAt(k);
            int foundcharmapidx = 0;
            for (int n = 0; n < charmap.length; n++) {
                if (currentchar != charmap[n])
                    continue;
                foundcharmapidx = n;
                break;
            }

            if (foundcharmapidx > 12)
                foundcharmapidx += 195;
            if (lshift == -1) {
                if (foundcharmapidx < 13)
                    lshift = foundcharmapidx;
                else
                    scrambledbytes[off++] = (byte) foundcharmapidx;
            } else if (foundcharmapidx < 13) {
                scrambledbytes[off++] = (byte) ((lshift << 4) + foundcharmapidx);
                lshift = -1;
            } else {
                scrambledbytes[off++] = (byte) ((lshift << 4) + (foundcharmapidx >> 4));
                lshift = foundcharmapidx & 0xf;
            }
        }

        if (lshift != -1)
            scrambledbytes[off++] = (byte) (lshift << 4);
        return off;
    }

}
