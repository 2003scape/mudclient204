public class WordFilter {

    static boolean DEBUGTLD;
    static boolean DEBUGWORD;
    static boolean forceLowercase = true;
    //static int unused = 3;// todo
    static int hashFragments[];
    static char badList[][];
    static byte badCharIds[][][];
    static char hostList[][];
    static byte hostCharIds[][][];
    static char tldList[][];
    static int tldType[];
    static String ignoreList[] = {
            "cook", "cook's", "cooks", "seeks", "sheet"
    };

    public static void loadFilters(Buffer fragments, Buffer bad, Buffer host, Buffer tld) {
        loadBad(bad);
        loadHost(host);
        loadFragments(fragments);
        loadTld(tld);
    }

    public static void loadTld(Buffer buffer) {
        int wordcount = buffer.getUnsignedInt();
        tldList = new char[wordcount][];
        tldType = new int[wordcount];
        for (int idx = 0; idx < wordcount; idx++) {
            tldType[idx] = buffer.getUnsignedByte();
            char ac[] = new char[buffer.getUnsignedByte()];
            for (int k = 0; k < ac.length; k++)
                ac[k] = (char) buffer.getUnsignedByte();

            tldList[idx] = ac;
        }

    }

    public static void loadBad(Buffer buffer) {
        int wordcount = buffer.getUnsignedInt();
        badList = new char[wordcount][];
        badCharIds = new byte[wordcount][][];
        readBuffer(buffer, badList, badCharIds);
    }

    public static void loadHost(Buffer buffer) {
        int wordcount = buffer.getUnsignedInt();
        hostList = new char[wordcount][];
        hostCharIds = new byte[wordcount][][];
        readBuffer(buffer, hostList, hostCharIds);
    }

    public static void loadFragments(Buffer buffer) {
        hashFragments = new int[buffer.getUnsignedInt()];
        for (int i = 0; i < hashFragments.length; i++) {
            hashFragments[i] = buffer.getUnsignedShort();
        }

    }

    public static void readBuffer(Buffer buffer, char wordList[][], byte charIds[][][]) {
        for (int i = 0; i < wordList.length; i++) {
            char currentWord[] = new char[buffer.getUnsignedByte()];
            for (int j = 0; j < currentWord.length; j++)
                currentWord[j] = (char) buffer.getUnsignedByte();

            wordList[i] = currentWord;
            byte ids[][] = new byte[buffer.getUnsignedInt()][2];
            for (int j = 0; j < ids.length; j++) {
                ids[j][0] = (byte) buffer.getUnsignedByte();
                ids[j][1] = (byte) buffer.getUnsignedByte();
            }

            if (ids.length > 0)
                charIds[i] = ids;
        }

    }

    public static String filter(String input) {
        char inputChars[] = input.toLowerCase().toCharArray();
        applyDotSlashFilter(inputChars);
        applyBadwordFilter(inputChars);
        applyHostFilter(inputChars);
        heywhathteufck(inputChars);
        for (int ignoreIdx = 0; ignoreIdx < ignoreList.length; ignoreIdx++) {
            for (int inputIgnoreIdx = -1; (inputIgnoreIdx = input.indexOf(ignoreList[ignoreIdx], inputIgnoreIdx + 1)) != -1; ) {
                char ignorewordChars[] = ignoreList[ignoreIdx].toCharArray();
                for (int ignorewordIdx = 0; ignorewordIdx < ignorewordChars.length; ignorewordIdx++)
                    inputChars[ignorewordIdx + inputIgnoreIdx] = ignorewordChars[ignorewordIdx];

            }

        }

        if (forceLowercase) {
            stripLowercase(input.toCharArray(), inputChars);
            toLowercase(inputChars);
        }
        return new String(inputChars);
    }

    public static void stripLowercase(char input[], char output[]) {
        for (int i = 0; i < input.length; i++)
            if (output[i] != '*' && isUppercase(input[i]))
                output[i] = input[i];

    }

    public static void toLowercase(char input[]) {
        boolean isUppercase = true;
        for (int i = 0; i < input.length; i++) {
            char current = input[i];
            if (isLetter(current)) {
                if (isUppercase) {
                    if (isLowercase(current))
                        isUppercase = false;
                } else if (isUppercase(current))
                    input[i] = (char) ((current + 97) - 65);
            } else {
                isUppercase = true;
            }
        }

    }

    public static void applyBadwordFilter(char input[]) {
        for (int i = 0; i < 2; i++) {// why lol
            for (int j = badList.length - 1; j >= 0; j--)
                applyWordFilter(input, badList[j], badCharIds[j]);

        }

    }

    public static void applyHostFilter(char input[]) {
        for (int i = hostList.length - 1; i >= 0; i--)
            applyWordFilter(input, hostList[i], hostCharIds[i]);

    }

    public static void applyDotSlashFilter(char input[]) {
        char input1[] = input.clone();
        char dot[] = {
                'd', 'o', 't'
        };
        applyWordFilter(input1, dot, null);
        char input2[] = input.clone();
        char slash[] = {
                's', 'l', 'a', 's', 'h'
        };
        applyWordFilter(input2, slash, null);
        for (int i = 0; i < tldList.length; i++)
            applyTldFilter(input, input1, input2, tldList[i], tldType[i]);

    }

    public static void applyTldFilter(char input[], char input1[], char input2[], char tld[], int type) {
        if (tld.length > input.length)
            return;
        for (int charIndex = 0; charIndex <= input.length - tld.length; charIndex++) {
            int inputCharCount = charIndex;
            int l = 0;
            while (inputCharCount < input.length) {
                int i1 = 0;
                char current = input[inputCharCount];
                char next = '\0';
                if (inputCharCount + 1 < input.length)
                    next = input[inputCharCount + 1];
                if (l < tld.length && (i1 = compareLettersNumbers(tld[l], current, next)) > 0) {
                    inputCharCount += i1;
                    l++;
                    continue;
                }
                if (l == 0)
                    break;
                if ((i1 = compareLettersNumbers(tld[l - 1], current, next)) > 0) {
                    inputCharCount += i1;
                    continue;
                }
                if (l >= tld.length || !isSpecial(current))
                    break;
                inputCharCount++;
            }
            if (l >= tld.length) {
                boolean flag = false;
                int startMatch = getAsteriskCount(input, input1, charIndex);
                int endMatch = getAsteriskCount2(input, input2, inputCharCount - 1);
                if (DEBUGTLD)
                    System.out.println("Potential tld: " + tld + " at char " + charIndex + " (type=" + type + ", startmatch=" + startMatch + ", endmatch=" + endMatch + ")");
                if (type == 1 && startMatch > 0 && endMatch > 0)
                    flag = true;
                if (type == 2 && (startMatch > 2 && endMatch > 0 || startMatch > 0 && endMatch > 2))
                    flag = true;
                if (type == 3 && startMatch > 0 && endMatch > 2)
                    flag = true;
                boolean tmp = type == 3 && startMatch > 2 && endMatch > 0;
                if (flag) {
                    if (DEBUGTLD)
                        System.out.println("Filtered tld: " + tld + " at char " + charIndex);
                    int l1 = charIndex;
                    int i2 = inputCharCount - 1;
                    if (startMatch > 2) {
                        if (startMatch == 4) {
                            boolean flag1 = false;
                            for (int k2 = l1 - 1; k2 >= 0; k2--)
                                if (flag1) {
                                    if (input1[k2] != '*')
                                        break;
                                    l1 = k2;
                                } else if (input1[k2] == '*') {
                                    l1 = k2;
                                    flag1 = true;
                                }

                        }
                        boolean flag2 = false;
                        for (int l2 = l1 - 1; l2 >= 0; l2--)
                            if (flag2) {
                                if (isSpecial(input[l2]))
                                    break;
                                l1 = l2;
                            } else if (!isSpecial(input[l2])) {
                                flag2 = true;
                                l1 = l2;
                            }

                    }
                    if (endMatch > 2) {
                        if (endMatch == 4) {
                            boolean flag3 = false;
                            for (int i3 = i2 + 1; i3 < input.length; i3++)
                                if (flag3) {
                                    if (input2[i3] != '*')
                                        break;
                                    i2 = i3;
                                } else if (input2[i3] == '*') {
                                    i2 = i3;
                                    flag3 = true;
                                }

                        }
                        boolean flag4 = false;
                        for (int j3 = i2 + 1; j3 < input.length; j3++)
                            if (flag4) {
                                if (isSpecial(input[j3]))
                                    break;
                                i2 = j3;
                            } else if (!isSpecial(input[j3])) {
                                flag4 = true;
                                i2 = j3;
                            }

                    }
                    for (int j2 = l1; j2 <= i2; j2++)
                        input[j2] = '*';

                }
            }
        }

    }

    public static int getAsteriskCount(char input[], char input1[], int len) {// fldajmolfmiALFKM
        if (len == 0)
            return 2;
        for (int j = len - 1; j >= 0; j--) {
            if (!isSpecial(input[j]))
                break;
            if (input[j] == ',' || input[j] == '.')
                return 3;
        }

        int filtered = 0;
        for (int l = len - 1; l >= 0; l--) {
            if (!isSpecial(input1[l]))
                break;
            if (input1[l] == '*')
                filtered++;
        }

        if (filtered >= 3)
            return 4;
        return isSpecial(input[len - 1]) ? 1 : 0;
    }

    public static int getAsteriskCount2(char input[], char input1[], int len) {// lolmnafomLMAFOA
        if (len + 1 == input.length)
            return 2;
        for (int j = len + 1; j < input.length; j++) {
            if (!isSpecial(input[j]))
                break;
            if (input[j] == '\\' || input[j] == '/')
                return 3;
        }

        int filtered = 0;
        for (int l = len + 1; l < input.length; l++) {
            if (!isSpecial(input1[l]))
                break;
            if (input1[l] == '*')
                filtered++;
        }

        if (filtered >= 5)
            return 4;
        return isSpecial(input[len + 1]) ? 1 : 0;
    }

    public static void applyWordFilter(char input[], char wordlist[], byte charIds[][]) {
        if (wordlist.length > input.length)
            return;
        for (int charIndex = 0; charIndex <= input.length - wordlist.length; charIndex++) {
            int inputCharCount = charIndex;
            int k = 0;
            boolean specialChar = false;
            while (inputCharCount < input.length) {
                int l = 0;
                char inputChar = input[inputCharCount];
                char nextChar = '\0';
                if (inputCharCount + 1 < input.length)
                    nextChar = input[inputCharCount + 1];
                if (k < wordlist.length && (l = compareLettersSymbols(wordlist[k], inputChar, nextChar)) > 0) {
                    inputCharCount += l;
                    k++;
                    continue;
                }
                if (k == 0)
                    break;
                if ((l = compareLettersSymbols(wordlist[k - 1], inputChar, nextChar)) > 0) {
                    inputCharCount += l;
                    continue;
                }
                if (k >= wordlist.length || !isNotLowercase(inputChar))
                    break;
                if (isSpecial(inputChar) && inputChar != '\'')
                    specialChar = true;
                inputCharCount++;
            }
            if (k >= wordlist.length) {
                boolean filter = true;
                if (DEBUGTLD)
                    System.out.println("Potential word: " + wordlist + " at char " + charIndex);
                if (!specialChar) {
                    char prevChar = ' ';
                    if (charIndex - 1 >= 0)
                        prevChar = input[charIndex - 1];
                    char curChar = ' ';
                    if (inputCharCount < input.length)
                        curChar = input[inputCharCount];
                    byte prevId = getCharId(prevChar);
                    byte curId = getCharId(curChar);
                    if (charIds != null && compareCharIds(charIds, prevId, curId))
                        filter = false;
                } else {
                    boolean flag2 = false;
                    boolean flag3 = false;
                    if (charIndex - 1 < 0 || isSpecial(input[charIndex - 1]) && input[charIndex - 1] != '\'')
                        flag2 = true;
                    if (inputCharCount >= input.length || isSpecial(input[inputCharCount]) && input[inputCharCount] != '\'')
                        flag3 = true;
                    if (!flag2 || !flag3) {
                        boolean flag4 = false;
                        int j1 = charIndex - 2;
                        if (flag2)
                            j1 = charIndex;
                        for (; !flag4 && j1 < inputCharCount; j1++)
                            if (j1 >= 0 && (!isSpecial(input[j1]) || input[j1] == '\'')) {
                                char ac2[] = new char[3];
                                int k1;
                                for (k1 = 0; k1 < 3; k1++) {
                                    if (j1 + k1 >= input.length || isSpecial(input[j1 + k1]) && input[j1 + k1] != '\'')
                                        break;
                                    ac2[k1] = input[j1 + k1];
                                }

                                boolean flag5 = true;
                                if (k1 == 0)
                                    flag5 = false;
                                if (k1 < 3 && j1 - 1 >= 0 && (!isSpecial(input[j1 - 1]) || input[j1 - 1] == '\''))
                                    flag5 = false;
                                if (flag5 && !containsFragmentHashes(ac2))
                                    flag4 = true;
                            }

                        if (!flag4)
                            filter = false;
                    }
                }
                if (filter) {
                    if (DEBUGWORD)
                        System.out.println("Filtered word: " + wordlist + " at char " + charIndex);
                    for (int i1 = charIndex; i1 < inputCharCount; i1++)
                        input[i1] = '*';

                }
            }
        }

    }

    public static boolean compareCharIds(byte charIdData[][], byte prevCharId, byte curCharId) {
        int first = 0;
        if (charIdData[first][0] == prevCharId && charIdData[first][1] == curCharId)
            return true;
        int last = charIdData.length - 1;
        if (charIdData[last][0] == prevCharId && charIdData[last][1] == curCharId)
            return true;
        while (first != last && first + 1 != last) {
            int middle = (first + last) / 2;
            if (charIdData[middle][0] == prevCharId && charIdData[middle][1] == curCharId)
                return true;
            if (prevCharId < charIdData[middle][0] || prevCharId == charIdData[middle][0] && curCharId < charIdData[middle][1])
                last = middle;
            else
                first = middle;
        }
        return false;
    }

    /**
     * @param filterChar
     * @param currentChar
     * @param nextChar
     * @return 0 for no match, 1 for currentChar matches, 2 for both currentChar and nextChar matching
     */
    public static int compareLettersNumbers(char filterChar, char currentChar, char nextChar) {
        if (filterChar == currentChar)
            return 1;
        if (filterChar == 'e' && currentChar == '3')
            return 1;
        if (filterChar == 't' && (currentChar == '7' || currentChar == '+'))
            return 1;
        if (filterChar == 'a' && (currentChar == '4' || currentChar == '@'))
            return 1;
        if (filterChar == 'o' && currentChar == '0')
            return 1;
        if (filterChar == 'i' && currentChar == '1')
            return 1;
        if (filterChar == 's' && currentChar == '5')
            return 1;
        if (filterChar == 'f' && currentChar == 'p' && nextChar == 'h')
            return 2;
        return filterChar == 'g' && currentChar == '9' ? 1 : 0;
    }

    /**
     * @param filterChar  character to compare against
     * @param currentChar current character
     * @param nextChar    next character
     * @return 0 for no match, 1 for currentChar matches, 2 for both currentChar and nextChar matching
     */
    public static int compareLettersSymbols(char filterChar, char currentChar, char nextChar) {
        if (filterChar == '*')
            return 0;
        if (filterChar == currentChar)
            return 1;
        if (filterChar >= 'a' && filterChar <= 'z') {
            if (filterChar == 'e')
                return currentChar == '3' ? 1 : 0;
            if (filterChar == 't')
                return currentChar == '7' ? 1 : 0;
            if (filterChar == 'a')
                return currentChar == '4' || currentChar == '@' ? 1 : 0;
            if (filterChar == 'o') {
                if (currentChar == '0' || currentChar == '*')
                    return 1;
                return currentChar == '(' && nextChar == ')' ? 2 : 0;
            }
            if (filterChar == 'i')
                return currentChar == 'y' || currentChar == 'l' || currentChar == 'j' || currentChar == 'l' || currentChar == '!' || currentChar == ':' || currentChar == ';' ? 1 : 0;
            if (filterChar == 'n')
                return 0;
            if (filterChar == 's')
                return currentChar == '5' || currentChar == 'z' || currentChar == '$' ? 1 : 0;
            if (filterChar == 'r')
                return 0;
            if (filterChar == 'h')
                return 0;
            if (filterChar == 'l')
                return currentChar == '1' ? 1 : 0;
            if (filterChar == 'd')
                return 0;
            if (filterChar == 'c')
                return currentChar == '(' ? 1 : 0;
            if (filterChar == 'u')
                return currentChar == 'v' ? 1 : 0;
            if (filterChar == 'm')
                return 0;
            if (filterChar == 'f')
                return currentChar == 'p' && nextChar == 'h' ? 2 : 0;
            if (filterChar == 'p')
                return 0;
            if (filterChar == 'g')
                return currentChar == '9' || currentChar == '6' ? 1 : 0;
            if (filterChar == 'w')
                return currentChar == 'v' && nextChar == 'v' ? 2 : 0;
            if (filterChar == 'y')
                return 0;
            if (filterChar == 'b')
                return currentChar == '1' && nextChar == '3' ? 2 : 0;
            if (filterChar == 'v')
                return 0;
            if (filterChar == 'k')
                return 0;
            if (filterChar == 'x')
                return currentChar == ')' && nextChar == '(' ? 2 : 0;
            if (filterChar == 'j')
                return 0;
            if (filterChar == 'q')
                return 0;
            if (filterChar == 'z')
                return 0;
        }
        if (filterChar >= '0' && filterChar <= '9') {
            if (filterChar == '0') {
                if (currentChar == 'o' || currentChar == 'O')
                    return 1;
                return currentChar == '(' && nextChar == ')' ? 2 : 0;
            }
            if (filterChar == '1')
                return currentChar != 'l' ? 0 : 1;
            if (filterChar == '2')
                return 0;
            if (filterChar == '3')
                return 0;
            if (filterChar == '4')
                return 0;
            if (filterChar == '5')
                return 0;
            if (filterChar == '6')
                return 0;
            if (filterChar == '7')
                return 0;
            if (filterChar == '8')
                return 0;
            if (filterChar == '9')
                return 0;
        }
        if (filterChar == '-')
            return 0;
        if (filterChar == ',')
            return currentChar == '.' ? 1 : 0;
        if (filterChar == '.')
            return currentChar == ',' ? 1 : 0;
        if (filterChar == '(')
            return 0;
        if (filterChar == ')')
            return 0;
        if (filterChar == '!')
            return currentChar == 'i' ? 1 : 0;
        if (filterChar == '\'')
            return 0;
        if (DEBUGWORD)
            System.out.println("Letter=" + filterChar + " not matched");
        return 0;
    }

    /**
     * Returns the id for the given char, ranging from {@code 1} to {@code 38}.
     * <p>
     * <pre>
     * id     range
     * 1-26   a-z
     * 27     unknown
     * 28     apostrophe
     * 29-38  0-9
     * </pre>
     *
     * @param c
     * @return id for char {@code c}
     */
    public static byte getCharId(char c) {
        if (c >= 'a' && c <= 'z')
            return (byte) (c - 97 + 1);
        if (c == '\'')
            return 28;
        if (c >= '0' && c <= '9')
            return (byte) (c - 48 + 29);
        else
            return 27;
    }

    public static void heywhathteufck(char input[]) {
        int digitIndex = 0;
        int fromIndex = 0;
        int k = 0;
        int l = 0;
        while ((digitIndex = indexOfDigit(input, fromIndex)) != -1) {
            boolean flag = false;
            for (int i = fromIndex; i >= 0 && i < digitIndex && !flag; i++)
                if (!isSpecial(input[i]) && !isNotLowercase(input[i]))
                    flag = true;

            if (flag)
                k = 0;
            if (k == 0)
                l = digitIndex;
            fromIndex = indexOfNonDigit(input, digitIndex);
            int j1 = 0;
            for (int k1 = digitIndex; k1 < fromIndex; k1++)
                j1 = (j1 * 10 + input[k1]) - 48;

            if (j1 > 255 || fromIndex - digitIndex > 8)
                k = 0;
            else
                k++;
            if (k == 4) {
                for (int i = l; i < fromIndex; i++)
                    input[i] = '*';

                k = 0;
            }
        }
    }

    public static int indexOfDigit(char input[], int fromIndex) {
        for (int i = fromIndex; i < input.length && i >= 0; i++)
            if (input[i] >= '0' && input[i] <= '9')
                return i;

        return -1;
    }

    public static int indexOfNonDigit(char input[], int fromIndex) {
        for (int i = fromIndex; i < input.length && i >= 0; i++)
            if (input[i] < '0' || input[i] > '9')
                return i;

        return input.length;
    }

    public static boolean isSpecial(char c) {
        return !isLetter(c) && !isDigit(c);
    }

    public static boolean isNotLowercase(char c) {
        if (c < 'a' || c > 'z')
            return true;
        return c == 'v' || c == 'x' || c == 'j' || c == 'q' || c == 'z';
    }

    public static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isLowercase(char c) {
        return c >= 'a' && c <= 'z';
    }

    public static boolean isUppercase(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean containsFragmentHashes(char input[]) {
        boolean notNum = true;
        for (int i = 0; i < input.length; i++)
            if (!isDigit(input[i]) && input[i] != 0)
                notNum = false;

        if (notNum)
            return true;
        int inputHash = word2hash(input);
        int first = 0;
        int last = hashFragments.length - 1;
        if (inputHash == hashFragments[first] || inputHash == hashFragments[last])
            return true;
        while (first != last && first + 1 != last) {
            int middle = (first + last) / 2;
            if (inputHash == hashFragments[middle])
                return true;
            if (inputHash < hashFragments[middle])
                last = middle;
            else
                first = middle;
        }
        return false;
    }

    /**
     * @param word
     * @return
     * @see WordFilter#getCharId(char)
     */
    public static int word2hash(char word[]) {
        if (word.length > 6)
            return 0;
        int hash = 0;
        for (int i = 0; i < word.length; i++) {
            char c = word[word.length - i - 1];
            if (c >= 'a' && c <= 'z')
                hash = hash * 38 + c - 97 + 1;
            else if (c == '\'')
                hash = hash * 38 + 27;
            else if (c >= '0' && c <= '9')
                hash = hash * 38 + c - 48 + 28;
            else if (c != 0) {
                if (DEBUGWORD)
                    System.out.println("word2hash failed on " + new String(word));
                return 0;
            }
        }

        return hash;
    }
}
