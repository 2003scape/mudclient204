public class GameData {

    public static String modelName[] = new String[5000];
    public static String textureName[];
    public static String textureSubtypeName[];
    public static int objectModelIndex[];
    public static int objectWidth[];
    public static int objectHeight[];
    public static int objectType[];
    public static int objectElevation[];
    public static int spellCount;
    public static int npcWidth[];
    public static int npcHeight[];
    public static int npcSprite[][];
    public static int npcAttack[];
    public static int npcStrength[];
    public static int npcHits[];
    public static int npcDefense[];
    public static int npcAttackable[];
    public static int spellLevel[];
    public static int spellRunesRequired[];
    public static int spellType[];
    public static int spellRunesId[][];
    public static int spellRunesCount[][];
    //public static String unused[] = new String[5000];
    public static int itemCount;
    public static int itemSpriteCount;
    public static int npcColourHair[];
    public static int npcColourTop[];
    public static int npcColorBottom[];
    public static int npcColourSkin[];
    public static int wallObjectHeight[];
    public static int wallObjectTextureFront[];
    public static int wallObjectTextureBack[];
    public static int wallObjectAdjacent[];
    public static int wallObjectInvisible[];
    //public static String unused2[] = new String[5000];
    public static int tileCount;
    public static int animationCharacterColour[];
    public static int animationSomething[];
    public static int animationHasA[];
    public static int animationHasF[];
    public static int animationNumber[];
    public static int wallObjectCount;
    public static int prayerLevel[];
    public static int prayerDrain[];
    public static int tileDecoration[];
    public static int tileType[];
    public static int tileAdjacent[];
    public static int modelCount;
    public static int roofHeight[];
    public static int roofNumVertices[];
    public static int prayerCount;
    public static String itemName[];
    public static String itemDescription[];
    public static String itemCommand[];
    public static int projectileSprite;
    public static int npcCount;
    public static String spellName[];
    public static String spellDescription[];
    public static int textureCount;
    public static String wallObjectName[];
    public static String wallObjectDescription[];
    public static String wallObjectCommand1[];
    public static String wallObjectCommand2[];
    public static int roofCount;
    public static int objectCount;
    public static String npcName[];
    public static String npcDescription[];
    public static String npcCommand[];
    public static String animationName[];
    public static int itemPicture[];
    public static int itemBasePrice[];
    public static int itemStackable[];
    public static int itemUnused[];
    public static int itemWearable[];
    public static int itemMask[];
    public static int itemSpecial[];
    public static int itemMembers[];
    public static int animationCount;
    public static String prayerName[];
    public static String prayerDescription[];
    public static String objectName[];
    public static String objectDescription[];
    public static String objectCommand1[];
    public static String objectCommand2[];
    public static int npcWalkModel[];
    public static int npcCombatModel[];
    public static int npcCombatAnimation[];
    static byte dataString[];
    static byte dataInteger[];
    static int stringOffset;
    static int offset;

    public static int getModelIndex(String s) {
        if (s.equalsIgnoreCase("na"))
            return 0;
        for (int i = 0; i < modelCount; i++)
            if (modelName[i].equalsIgnoreCase(s))
                return i;

        modelName[modelCount++] = s;
        return modelCount - 1;
    }

    public static int getUnsignedByte() {
        int i = dataInteger[offset] & 0xff;
        offset++;
        return i;
    }

    public static int getUnsignedShort() {
        int i = Utility.getUnsignedShort(dataInteger, offset);
        offset += 2;
        return i;
    }

    public static int getUnsignedInt() {
        int i = Utility.getUnsignedInt(dataInteger, offset);
        offset += 4;
        if (i > 99999999)
            i = 99999999 - i;
        return i;
    }

    public static String getString() {
        String s;
        for (s = ""; dataString[stringOffset] != 0; s = s + (char) dataString[stringOffset++]) ;
        stringOffset++;
        return s;
    }

    public static void loadData(byte buffer[], boolean isMembers) {
        dataString = Utility.loadData("string.dat", 0, buffer);
        stringOffset = 0;
        dataInteger = Utility.loadData("integer.dat", 0, buffer);
        offset = 0;

        int i;

        itemCount = getUnsignedShort();
        itemName = new String[itemCount];
        itemDescription = new String[itemCount];
        itemCommand = new String[itemCount];
        itemPicture = new int[itemCount];
        itemBasePrice = new int[itemCount];
        itemStackable = new int[itemCount];
        itemUnused = new int[itemCount];
        itemWearable = new int[itemCount];
        itemMask = new int[itemCount];
        itemSpecial = new int[itemCount];
        itemMembers = new int[itemCount];
        for (i = 0; i < itemCount; i++)
            itemName[i] = getString();

        for (i = 0; i < itemCount; i++)
            itemDescription[i] = getString();

        for (i = 0; i < itemCount; i++)
            itemCommand[i] = getString();

        for (i = 0; i < itemCount; i++) {
            itemPicture[i] = getUnsignedShort();
            if (itemPicture[i] + 1 > itemSpriteCount)
                itemSpriteCount = itemPicture[i] + 1;
        }

        for (i = 0; i < itemCount; i++)
            itemBasePrice[i] = getUnsignedInt();

        for (i = 0; i < itemCount; i++)
            itemStackable[i] = getUnsignedByte();

        for (i = 0; i < itemCount; i++)
            itemUnused[i] = getUnsignedByte();

        for (i = 0; i < itemCount; i++)
            itemWearable[i] = getUnsignedShort();

        for (i = 0; i < itemCount; i++)
            itemMask[i] = getUnsignedInt();

        for (i = 0; i < itemCount; i++)
            itemSpecial[i] = getUnsignedByte();

        for (i = 0; i < itemCount; i++)
            itemMembers[i] = getUnsignedByte();

        for (i = 0; i < itemCount; i++)
            if (!isMembers && itemMembers[i] == 1) {
                itemName[i] = "Members object";
                itemDescription[i] = "You need to be a member to use this object";
                itemBasePrice[i] = 0;
                itemCommand[i] = "";
                itemUnused[0] = 0;
                itemWearable[i] = 0;
                itemSpecial[i] = 1;
            }

        npcCount = getUnsignedShort();
        npcName = new String[npcCount];
        npcDescription = new String[npcCount];
        npcCommand = new String[npcCount];
        npcAttack = new int[npcCount];
        npcStrength = new int[npcCount];
        npcHits = new int[npcCount];
        npcDefense = new int[npcCount];
        npcAttackable = new int[npcCount];
        npcSprite = new int[npcCount][12];
        npcColourHair = new int[npcCount];
        npcColourTop = new int[npcCount];
        npcColorBottom = new int[npcCount];
        npcColourSkin = new int[npcCount];
        npcWidth = new int[npcCount];
        npcHeight = new int[npcCount];
        npcWalkModel = new int[npcCount];
        npcCombatModel = new int[npcCount];
        npcCombatAnimation = new int[npcCount];
        for (i = 0; i < npcCount; i++)
            npcName[i] = getString();

        for (i = 0; i < npcCount; i++)
            npcDescription[i] = getString();

        for (i = 0; i < npcCount; i++)
            npcAttack[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcStrength[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcHits[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcDefense[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcAttackable[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++) {
            for (int i5 = 0; i5 < 12; i5++) {
                npcSprite[i][i5] = getUnsignedByte();
                if (npcSprite[i][i5] == 255)
                    npcSprite[i][i5] = -1;
            }

        }

        for (i = 0; i < npcCount; i++)
            npcColourHair[i] = getUnsignedInt();

        for (i = 0; i < npcCount; i++)
            npcColourTop[i] = getUnsignedInt();

        for (i = 0; i < npcCount; i++)
            npcColorBottom[i] = getUnsignedInt();

        for (i = 0; i < npcCount; i++)
            npcColourSkin[i] = getUnsignedInt();

        for (i = 0; i < npcCount; i++)
            npcWidth[i] = getUnsignedShort();

        for (i = 0; i < npcCount; i++)
            npcHeight[i] = getUnsignedShort();

        for (i = 0; i < npcCount; i++)
            npcWalkModel[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcCombatModel[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcCombatAnimation[i] = getUnsignedByte();

        for (i = 0; i < npcCount; i++)
            npcCommand[i] = getString();

        textureCount = getUnsignedShort();
        textureName = new String[textureCount];
        textureSubtypeName = new String[textureCount];
        for (i = 0; i < textureCount; i++)
            textureName[i] = getString();

        for (i = 0; i < textureCount; i++)
            textureSubtypeName[i] = getString();

        animationCount = getUnsignedShort();
        animationName = new String[animationCount];
        animationCharacterColour = new int[animationCount];
        animationSomething = new int[animationCount];
        animationHasA = new int[animationCount];
        animationHasF = new int[animationCount];
        animationNumber = new int[animationCount];
        for (i = 0; i < animationCount; i++)
            animationName[i] = getString();

        for (i = 0; i < animationCount; i++)
            animationCharacterColour[i] = getUnsignedInt();

        for (i = 0; i < animationCount; i++)
            animationSomething[i] = getUnsignedByte();

        for (i = 0; i < animationCount; i++)
            animationHasA[i] = getUnsignedByte();

        for (i = 0; i < animationCount; i++)
            animationHasF[i] = getUnsignedByte();

        for (i = 0; i < animationCount; i++)
            animationNumber[i] = getUnsignedByte();

        objectCount = getUnsignedShort();
        objectName = new String[objectCount];
        objectDescription = new String[objectCount];
        objectCommand1 = new String[objectCount];
        objectCommand2 = new String[objectCount];
        objectModelIndex = new int[objectCount];
        objectWidth = new int[objectCount];
        objectHeight = new int[objectCount];
        objectType = new int[objectCount];
        objectElevation = new int[objectCount];
        for (i = 0; i < objectCount; i++)
            objectName[i] = getString();

        for (i = 0; i < objectCount; i++)
            objectDescription[i] = getString();

        for (i = 0; i < objectCount; i++)
            objectCommand1[i] = getString();

        for (i = 0; i < objectCount; i++)
            objectCommand2[i] = getString();

        for (i = 0; i < objectCount; i++)
            objectModelIndex[i] = getModelIndex(getString());

        for (i = 0; i < objectCount; i++)
            objectWidth[i] = getUnsignedByte();

        for (i = 0; i < objectCount; i++)
            objectHeight[i] = getUnsignedByte();

        for (i = 0; i < objectCount; i++)
            objectType[i] = getUnsignedByte();

        for (i = 0; i < objectCount; i++)
            objectElevation[i] = getUnsignedByte();

        wallObjectCount = getUnsignedShort();
        wallObjectName = new String[wallObjectCount];
        wallObjectDescription = new String[wallObjectCount];
        wallObjectCommand1 = new String[wallObjectCount];
        wallObjectCommand2 = new String[wallObjectCount];
        wallObjectHeight = new int[wallObjectCount];
        wallObjectTextureFront = new int[wallObjectCount];
        wallObjectTextureBack = new int[wallObjectCount];
        wallObjectAdjacent = new int[wallObjectCount];
        wallObjectInvisible = new int[wallObjectCount];
        for (i = 0; i < wallObjectCount; i++)
            wallObjectName[i] = getString();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectDescription[i] = getString();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectCommand1[i] = getString();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectCommand2[i] = getString();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectHeight[i] = getUnsignedShort();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectTextureFront[i] = getUnsignedInt();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectTextureBack[i] = getUnsignedInt();

        for (i = 0; i < wallObjectCount; i++)
            wallObjectAdjacent[i] = getUnsignedByte();// what's this?

        for (i = 0; i < wallObjectCount; i++)
            wallObjectInvisible[i] = getUnsignedByte();// value is 0 if visible

        roofCount = getUnsignedShort();// the World class does something with these
        roofHeight = new int[roofCount];
        roofNumVertices = new int[roofCount];
        for (i = 0; i < roofCount; i++)
            roofHeight[i] = getUnsignedByte();

        for (i = 0; i < roofCount; i++)
            roofNumVertices[i] = getUnsignedByte();

        tileCount = getUnsignedShort();// and these
        tileDecoration = new int[tileCount];
        tileType = new int[tileCount];
        tileAdjacent = new int[tileCount];
        for (i = 0; i < tileCount; i++)
            tileDecoration[i] = getUnsignedInt();

        for (i = 0; i < tileCount; i++)
            tileType[i] = getUnsignedByte();

        for (i = 0; i < tileCount; i++)
            tileAdjacent[i] = getUnsignedByte();

        projectileSprite = getUnsignedShort();
        spellCount = getUnsignedShort();
        spellName = new String[spellCount];
        spellDescription = new String[spellCount];
        spellLevel = new int[spellCount];
        spellRunesRequired = new int[spellCount];
        spellType = new int[spellCount];
        spellRunesId = new int[spellCount][];
        spellRunesCount = new int[spellCount][];
        for (i = 0; i < spellCount; i++)
            spellName[i] = getString();

        for (i = 0; i < spellCount; i++)
            spellDescription[i] = getString();

        for (i = 0; i < spellCount; i++)
            spellLevel[i] = getUnsignedByte();

        for (i = 0; i < spellCount; i++)
            spellRunesRequired[i] = getUnsignedByte();

        for (i = 0; i < spellCount; i++)
            spellType[i] = getUnsignedByte();

        for (i = 0; i < spellCount; i++) {
            int j = getUnsignedByte();
            spellRunesId[i] = new int[j];
            for (int k = 0; k < j; k++)
                spellRunesId[i][k] = getUnsignedShort();

        }

        for (i = 0; i < spellCount; i++) {
            int j = getUnsignedByte();
            spellRunesCount[i] = new int[j];
            for (int k = 0; k < j; k++)
                spellRunesCount[i][k] = getUnsignedByte();

        }

        prayerCount = getUnsignedShort();
        prayerName = new String[prayerCount];
        prayerDescription = new String[prayerCount];
        prayerLevel = new int[prayerCount];
        prayerDrain = new int[prayerCount];
        for (i = 0; i < prayerCount; i++)
            prayerName[i] = getString();

        for (i = 0; i < prayerCount; i++)
            prayerDescription[i] = getString();

        for (i = 0; i < prayerCount; i++)
            prayerLevel[i] = getUnsignedByte();

        for (i = 0; i < prayerCount; i++)
            prayerDrain[i] = getUnsignedByte();

        dataString = null;
        dataInteger = null;
    }
    //public static int unused3;

}
