import java.io.IOException;

public class World {

    static final int colourTransparent = 12345678;// usewd by gamemodel.magic and diameter
    final int regionWidth = 96;// could be the other way
    final int regionHeight = 96;// around, im not sure
    final int anInt585 = 128;// two possibilities; either where the region data is loaded,
    boolean worldInitialised;
    int objectAdjacency[][];
    byte tileDirection[][];
    GameModel wallModels[][];
    int terrainColours[];
    byte wallsNorthsouth[][];
    GameModel parentModel;
    byte wallsRoof[][];
    byte terrainHeight[][];
    GameModel roofModels[][];
    // or in relation with models. im going with models
    byte terrainColour[][];
    int localY[];
    byte tileDecoration[][];
    int routeVia[][];
    int wallsDiagonal[][];
    byte wallsEastwest[][];
    boolean aBoolean592;
    boolean playerAlive;
    int terrainHeightLocal[][];
    byte landscapePack[];
    byte mapPack[];
    Surface surface;
    Scene scene;
    GameModel terrainModels[];
    int localX[];
    byte memberLandscapePack[];
    byte memberMapPack[];
    int baseMediaSprite;

    public World(Scene scene, Surface surface) {
        worldInitialised = true;
        objectAdjacency = new int[regionWidth][regionHeight];
        tileDirection = new byte[4][2304];
        wallModels = new GameModel[4][64];
        terrainColours = new int[256];
        wallsNorthsouth = new byte[4][2304];
        wallsRoof = new byte[4][2304];
        terrainHeight = new byte[4][2304];
        roofModels = new GameModel[4][64];
        terrainColour = new byte[4][2304];
        localY = new int[18432];
        tileDecoration = new byte[4][2304];
        routeVia = new int[regionWidth][regionHeight];
        wallsDiagonal = new int[4][2304];
        wallsEastwest = new byte[4][2304];
        aBoolean592 = false;
        playerAlive = false;
        terrainHeightLocal = new int[regionWidth][regionHeight];
        terrainModels = new GameModel[64];
        localX = new int[18432];
        baseMediaSprite = 750;
        this.scene = scene;
        this.surface = surface;
        for (int i = 0; i < 64; i++)
            terrainColours[i] = Scene.rgb(255 - i * 4, 255 - (int) ((double) i * 1.75D), 255 - i * 4);

        for (int j = 0; j < 64; j++)
            terrainColours[j + 64] = Scene.rgb(j * 3, 144, 0);

        for (int k = 0; k < 64; k++)
            terrainColours[k + 128] = Scene.rgb(192 - (int) ((double) k * 1.5D), 144 - (int) ((double) k * 1.5D), 0);

        for (int l = 0; l < 64; l++)
            terrainColours[l + 192] = Scene.rgb(96 - (int) ((double) l * 1.5D), 48 + (int) ((double) l * 1.5D), 0);

    }

    public int getWallEastwest(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return wallsEastwest[h][x * 48 + y] & 0xff;
    }

    public void setTerrainAmbience(int x, int y, int x2, int y2, int ambience) {
        GameModel gameModel = terrainModels[x + y * 8];
        for (int j1 = 0; j1 < gameModel.numVertices; j1++)
            if (gameModel.vertexX[j1] == x2 * anInt585 && gameModel.vertexZ[j1] == y2 * anInt585) {
                gameModel.setVertexAmbience(j1, ambience);
                return;
            }

    }

    public int getWallRoof(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return wallsRoof[h][x * 48 + y];
    }

    public int getElevation(int x, int y) {
        int sX = x >> 7;
        int sY = y >> 7;
        int aX = x & 0x7f;
        int aY = y & 0x7f;
        if (sX < 0 || sY < 0 || sX >= 95 || sY >= 95)
            return 0;
        int h;
        int hx;
        int hy;
        if (aX <= anInt585 - aY) {
            h = getTerrainHeight(sX, sY);
            hx = getTerrainHeight(sX + 1, sY) - h;
            hy = getTerrainHeight(sX, sY + 1) - h;
        } else {
            h = getTerrainHeight(sX + 1, sY + 1);
            hx = getTerrainHeight(sX, sY + 1) - h;
            hy = getTerrainHeight(sX + 1, sY) - h;
            aX = anInt585 - aX;
            aY = anInt585 - aY;
        }
        int elevation = h + (hx * aX) / anInt585 + (hy * aY) / anInt585;
        return elevation;
    }

    public int getWallDiagonal(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return wallsDiagonal[h][x * 48 + y];
    }

    public void removeObject2(int x, int y, int id) {// todo set object osmething something something
        if (x < 0 || y < 0 || x >= 95 || y >= 95)
            return;
        if (GameData.objectType[id] == 1 || GameData.objectType[id] == 2) {
            int tileDir = getTileDirection(x, y);
            int modelWidth;
            int modelHeight;
            if (tileDir == 0 || tileDir == 4) {
                modelWidth = GameData.objectWidth[id];
                modelHeight = GameData.objectHeight[id];
            } else {
                modelHeight = GameData.objectWidth[id];
                modelWidth = GameData.objectHeight[id];
            }
            for (int mx = x; mx < x + modelWidth; mx++) {
                for (int my = y; my < y + modelHeight; my++)
                    if (GameData.objectType[id] == 1)
                        objectAdjacency[mx][my] |= 0x40;
                    else if (tileDir == 0) {
                        objectAdjacency[mx][my] |= 2;
                        if (mx > 0)
                            setObjectAdjacency(mx - 1, my, 8);
                    } else if (tileDir == 2) {
                        objectAdjacency[mx][my] |= 4;
                        if (my < 95)
                            setObjectAdjacency(mx, my + 1, 1);
                    } else if (tileDir == 4) {
                        objectAdjacency[mx][my] |= 8;
                        if (mx < 95)
                            setObjectAdjacency(mx + 1, my, 2);
                    } else if (tileDir == 6) {
                        objectAdjacency[mx][my] |= 1;
                        if (my > 0)
                            setObjectAdjacency(mx, my - 1, 4);
                    }

            }

            method404(x, y, modelWidth, modelHeight);
        }
    }

    public void removeWallObject(int x, int y, int k, int id) {
        if (x < 0 || y < 0 || x >= 95 || y >= 95)
            return;
        if (GameData.wallObjectAdjacent[id] == 1) {
            if (k == 0) {
                objectAdjacency[x][y] &= 0xfffe;
                if (y > 0)
                    method407(x, y - 1, 4);
            } else if (k == 1) {
                objectAdjacency[x][y] &= 0xfffd;
                if (x > 0)
                    method407(x - 1, y, 8);
            } else if (k == 2)
                objectAdjacency[x][y] &= 0xffef;
            else if (k == 3)
                objectAdjacency[x][y] &= 0xffdf;
            method404(x, y, 1, 1);
        }
    }

    public void method402(int i, int j, int k, int l, int i1) {
        int j1 = i * 3;
        int k1 = j * 3;
        int l1 = scene.method302(l);
        int i2 = scene.method302(i1);
        l1 = l1 >> 1 & 0x7f7f7f;
        i2 = i2 >> 1 & 0x7f7f7f;
        if (k == 0) {
            surface.drawLineHoriz(j1, k1, 3, l1);
            surface.drawLineHoriz(j1, k1 + 1, 2, l1);
            surface.drawLineHoriz(j1, k1 + 2, 1, l1);
            surface.drawLineHoriz(j1 + 2, k1 + 1, 1, i2);
            surface.drawLineHoriz(j1 + 1, k1 + 2, 2, i2);
            return;
        }
        if (k == 1) {
            surface.drawLineHoriz(j1, k1, 3, i2);
            surface.drawLineHoriz(j1 + 1, k1 + 1, 2, i2);
            surface.drawLineHoriz(j1 + 2, k1 + 2, 1, i2);
            surface.drawLineHoriz(j1, k1 + 1, 1, l1);
            surface.drawLineHoriz(j1, k1 + 2, 2, l1);
        }
    }

    public void loadSection(int x, int y, int plane, int chunk) {
        String mapname = "m" + plane + x / 10 + x % 10 + y / 10 + y % 10;
        try {
            if (landscapePack != null) {
                byte mapData[] = Utility.loadData(mapname + ".hei", 0, landscapePack);
                if (mapData == null && memberLandscapePack != null)
                    mapData = Utility.loadData(mapname + ".hei", 0, memberLandscapePack);
                if (mapData != null && mapData.length > 0) {
                    int off = 0;
                    int lastVal = 0;
                    for (int tile = 0; tile < 2304; ) {
                        int val = mapData[off++] & 0xff;
                        if (val < 128) {
                            terrainHeight[chunk][tile++] = (byte) val;
                            lastVal = val;
                        }
                        if (val >= 128) {
                            for (int i = 0; i < val - 128; i++)
                                terrainHeight[chunk][tile++] = (byte) lastVal;

                        }
                    }

                    lastVal = 64;
                    for (int tileY = 0; tileY < 48; tileY++) {
                        for (int tileX = 0; tileX < 48; tileX++) {
                            lastVal = terrainHeight[chunk][tileX * 48 + tileY] + lastVal & 0x7f;
                            terrainHeight[chunk][tileX * 48 + tileY] = (byte) (lastVal * 2);
                        }

                    }

                    lastVal = 0;
                    for (int tile = 0; tile < 2304; ) {
                        int val = mapData[off++] & 0xff;
                        if (val < 128) {
                            terrainColour[chunk][tile++] = (byte) val;
                            lastVal = val;
                        }
                        if (val >= 128) {
                            for (int i = 0; i < val - 128; i++)
                                terrainColour[chunk][tile++] = (byte) lastVal;

                        }
                    }

                    lastVal = 35;
                    for (int tileY = 0; tileY < 48; tileY++) {
                        for (int tileX = 0; tileX < 48; tileX++) {
                            lastVal = terrainColour[chunk][tileX * 48 + tileY] + lastVal & 0x7f;// ??? wat
                            terrainColour[chunk][tileX * 48 + tileY] = (byte) (lastVal * 2);
                        }

                    }

                } else {
                    for (int tile = 0; tile < 2304; tile++) {
                        terrainHeight[chunk][tile] = 0;
                        terrainColour[chunk][tile] = 0;
                    }

                }
                mapData = Utility.loadData(mapname + ".dat", 0, mapPack);
                if (mapData == null && memberMapPack != null)
                    mapData = Utility.loadData(mapname + ".dat", 0, memberMapPack);
                if (mapData == null || mapData.length == 0)
                    throw new IOException();
                int off = 0;
                for (int tile = 0; tile < 2304; tile++)
                    wallsNorthsouth[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++)
                    wallsEastwest[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++)
                    wallsDiagonal[chunk][tile] = mapData[off++] & 0xff;

                for (int tile = 0; tile < 2304; tile++) {
                    int val = mapData[off++] & 0xff;
                    if (val > 0)
                        wallsDiagonal[chunk][tile] = val + 12000;// why??
                }

                for (int tile = 0; tile < 2304; ) {
                    int val = mapData[off++] & 0xff;
                    if (val < 128) {
                        wallsRoof[chunk][tile++] = (byte) val;
                    } else {
                        for (int i = 0; i < val - 128; i++)
                            wallsRoof[chunk][tile++] = 0;

                    }
                }

                int lastVal = 0;
                for (int tile = 0; tile < 2304; ) {
                    int val = mapData[off++] & 0xff;
                    if (val < 128) {
                        tileDecoration[chunk][tile++] = (byte) val;
                        lastVal = val;
                    } else {
                        for (int i = 0; i < val - 128; i++)
                            tileDecoration[chunk][tile++] = (byte) lastVal;

                    }
                }

                for (int tile = 0; tile < 2304; ) {
                    int val = mapData[off++] & 0xff;
                    if (val < 128) {
                        tileDirection[chunk][tile++] = (byte) val;
                    } else {
                        for (int i = 0; i < val - 128; i++)
                            tileDirection[chunk][tile++] = 0;

                    }
                }

                mapData = Utility.loadData(mapname + ".loc", 0, mapPack);
                if (mapData != null && mapData.length > 0) {
                    off = 0;
                    for (int tile = 0; tile < 2304; ) {
                        int val = mapData[off++] & 0xff;
                        if (val < 128)
                            wallsDiagonal[chunk][tile++] = val + 48000;
                        else
                            tile += val - 128;
                    }

                    return;
                }
            } else {
                byte mapData[] = new byte[20736];
                Utility.readFully("../gamedata/maps/" + mapname + ".jm", mapData, 20736);
                int val = 0;
                int off = 0;
                for (int tile = 0; tile < 2304; tile++) {
                    val = val + mapData[off++] & 0xff;
                    terrainHeight[chunk][tile] = (byte) val;
                }

                val = 0;
                for (int tile = 0; tile < 2304; tile++) {
                    val = val + mapData[off++] & 0xff;
                    terrainColour[chunk][tile] = (byte) val;
                }

                for (int tile = 0; tile < 2304; tile++)
                    wallsNorthsouth[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++)
                    wallsEastwest[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++) {
                    wallsDiagonal[chunk][tile] = (mapData[off] & 0xff) * 256 + (mapData[off + 1] & 0xff);
                    off += 2;
                }

                for (int tile = 0; tile < 2304; tile++)
                    wallsRoof[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++)
                    tileDecoration[chunk][tile] = mapData[off++];

                for (int tile = 0; tile < 2304; tile++)
                    tileDirection[chunk][tile] = mapData[off++];

            }
            return;
        } catch (IOException ex) {
        }
        for (int tile = 0; tile < 2304; tile++) {
            terrainHeight[chunk][tile] = 0;
            terrainColour[chunk][tile] = 0;
            wallsNorthsouth[chunk][tile] = 0;
            wallsEastwest[chunk][tile] = 0;
            wallsDiagonal[chunk][tile] = 0;
            wallsRoof[chunk][tile] = 0;
            tileDecoration[chunk][tile] = 0;
            if (plane == 0)
                tileDecoration[chunk][tile] = -6;
            if (plane == 3)
                tileDecoration[chunk][tile] = 8;
            tileDirection[chunk][tile] = 0;
        }

    }

    public void method404(int x, int y, int k, int l) {
        if (x < 1 || y < 1 || x + k >= regionWidth || y + l >= regionHeight)
            return;
        for (int xx = x; xx <= x + k; xx++) {
            for (int yy = y; yy <= y + l; yy++)
                if ((getObjectAdjacency(xx, yy) & 0x63) != 0 || (getObjectAdjacency(xx - 1, yy) & 0x59) != 0 || (getObjectAdjacency(xx, yy - 1) & 0x56) != 0 || (getObjectAdjacency(xx - 1, yy - 1) & 0x6c) != 0)
                    method425(xx, yy, 35);
                else
                    method425(xx, yy, 0);

        }

    }

    public int getObjectAdjacency(int x, int y) {
        if (x < 0 || y < 0 || x >= regionWidth || y >= regionHeight)
            return 0;
        else
            return objectAdjacency[x][y];
    }

    public boolean hasRoof(int x, int y) {
        return getWallRoof(x, y) > 0 && getWallRoof(x - 1, y) > 0 && getWallRoof(x - 1, y - 1) > 0 && getWallRoof(x, y - 1) > 0;
    }

    public void method407(int i, int j, int k) {
        objectAdjacency[i][j] &= 0xffff - k;
    }

    public int getTerrainColour(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte byte0 = 0;
        if (x >= 48 && y < 48) {
            byte0 = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            byte0 = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            byte0 = 3;
            x -= 48;
            y -= 48;
        }
        return terrainColour[byte0][x * 48 + y] & 0xff;
    }

    public void reset() {
        if (worldInitialised)
            scene.dispose();
        for (int i = 0; i < 64; i++) {
            terrainModels[i] = null;
            for (int j = 0; j < 4; j++)
                wallModels[j][i] = null;

            for (int k = 0; k < 4; k++)
                roofModels[k][i] = null;

        }

        System.gc();
    }

    public void setTiles() {
        for (int x = 0; x < regionWidth; x++) {
            for (int y = 0; y < regionHeight; y++)
                if (getTileDecoration(x, y, 0) == 250)
                    if (x == 47 && getTileDecoration(x + 1, y, 0) != 250 && getTileDecoration(x + 1, y, 0) != 2)
                        setTileDecoration(x, y, 9);
                    else if (y == 47 && getTileDecoration(x, y + 1, 0) != 250 && getTileDecoration(x, y + 1, 0) != 2)
                        setTileDecoration(x, y, 9);
                    else
                        setTileDecoration(x, y, 2);

        }

    }

    public int getWallNorthsouth(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return wallsNorthsouth[h][x * 48 + y] & 0xff;
    }

    public int getTileDirection(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return tileDirection[h][x * 48 + y];
    }

    public int getTileDecoration(int x, int y, int unused, int def) {
        int deco = getTileDecoration(x, y, unused);
        if (deco == 0)
            return def;
        else
            return GameData.tileDecoration[deco - 1];
    }

    public int getTileDecoration(int x, int y, int unused) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        return tileDecoration[h][x * 48 + y] & 0xff;
    }

    public void setTileDecoration(int x, int y, int v) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return;
        byte h = 0;
        if (x >= 48 && y < 48) {
            h = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            h = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            h = 3;
            x -= 48;
            y -= 48;
        }
        tileDecoration[h][x * 48 + y] = (byte) v;
    }

    public int route(int startX, int startY, int endX1, int endY1, int endX2, int endY2, int routeX[], int routeY[], boolean objects) {
        for (int x = 0; x < regionWidth; x++) {
            for (int y = 0; y < regionHeight; y++)
                routeVia[x][y] = 0;

        }

        int writePtr = 0;
        int readPtr = 0;
        int x = startX;
        int y = startY;
        routeVia[startX][startY] = 99;
        routeX[writePtr] = startX;
        routeY[writePtr++] = startY;
        int size = routeX.length;
        boolean reached = false;
        while (readPtr != writePtr) {
            x = routeX[readPtr];
            y = routeY[readPtr];
            readPtr = (readPtr + 1) % size;
            if (x >= endX1 && x <= endX2 && y >= endY1 && y <= endY2) {
                reached = true;
                break;
            }
            if (objects) {
                if (x > 0 && x - 1 >= endX1 && x - 1 <= endX2 && y >= endY1 && y <= endY2 && (objectAdjacency[x - 1][y] & 8) == 0) {
                    reached = true;
                    break;
                }
                if (x < 95 && x + 1 >= endX1 && x + 1 <= endX2 && y >= endY1 && y <= endY2 && (objectAdjacency[x + 1][y] & 2) == 0) {
                    reached = true;
                    break;
                }
                if (y > 0 && x >= endX1 && x <= endX2 && y - 1 >= endY1 && y - 1 <= endY2 && (objectAdjacency[x][y - 1] & 4) == 0) {
                    reached = true;
                    break;
                }
                if (y < 95 && x >= endX1 && x <= endX2 && y + 1 >= endY1 && y + 1 <= endY2 && (objectAdjacency[x][y + 1] & 1) == 0) {
                    reached = true;
                    break;
                }
            }
            if (x > 0 && routeVia[x - 1][y] == 0 && (objectAdjacency[x - 1][y] & 0x78) == 0) {
                routeX[writePtr] = x - 1;
                routeY[writePtr] = y;
                writePtr = (writePtr + 1) % size;
                routeVia[x - 1][y] = 2;
            }
            if (x < 95 && routeVia[x + 1][y] == 0 && (objectAdjacency[x + 1][y] & 0x72) == 0) {
                routeX[writePtr] = x + 1;
                routeY[writePtr] = y;
                writePtr = (writePtr + 1) % size;
                routeVia[x + 1][y] = 8;
            }
            if (y > 0 && routeVia[x][y - 1] == 0 && (objectAdjacency[x][y - 1] & 0x74) == 0) {
                routeX[writePtr] = x;
                routeY[writePtr] = y - 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x][y - 1] = 1;
            }
            if (y < 95 && routeVia[x][y + 1] == 0 && (objectAdjacency[x][y + 1] & 0x71) == 0) {
                routeX[writePtr] = x;
                routeY[writePtr] = y + 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x][y + 1] = 4;
            }
            if (x > 0 && y > 0 && (objectAdjacency[x][y - 1] & 0x74) == 0 && (objectAdjacency[x - 1][y] & 0x78) == 0 && (objectAdjacency[x - 1][y - 1] & 0x7c) == 0 && routeVia[x - 1][y - 1] == 0) {
                routeX[writePtr] = x - 1;
                routeY[writePtr] = y - 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x - 1][y - 1] = 3;
            }
            if (x < 95 && y > 0 && (objectAdjacency[x][y - 1] & 0x74) == 0 && (objectAdjacency[x + 1][y] & 0x72) == 0 && (objectAdjacency[x + 1][y - 1] & 0x76) == 0 && routeVia[x + 1][y - 1] == 0) {
                routeX[writePtr] = x + 1;
                routeY[writePtr] = y - 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x + 1][y - 1] = 9;
            }
            if (x > 0 && y < 95 && (objectAdjacency[x][y + 1] & 0x71) == 0 && (objectAdjacency[x - 1][y] & 0x78) == 0 && (objectAdjacency[x - 1][y + 1] & 0x79) == 0 && routeVia[x - 1][y + 1] == 0) {
                routeX[writePtr] = x - 1;
                routeY[writePtr] = y + 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x - 1][y + 1] = 6;
            }
            if (x < 95 && y < 95 && (objectAdjacency[x][y + 1] & 0x71) == 0 && (objectAdjacency[x + 1][y] & 0x72) == 0 && (objectAdjacency[x + 1][y + 1] & 0x73) == 0 && routeVia[x + 1][y + 1] == 0) {
                routeX[writePtr] = x + 1;
                routeY[writePtr] = y + 1;
                writePtr = (writePtr + 1) % size;
                routeVia[x + 1][y + 1] = 12;
            }
        }
        if (!reached)
            return -1;
        readPtr = 0;
        routeX[readPtr] = x;
        routeY[readPtr++] = y;
        int stride;
        for (int step = stride = routeVia[x][y]; x != startX || y != startY; step = routeVia[x][y]) {
            if (step != stride) {
                stride = step;
                routeX[readPtr] = x;
                routeY[readPtr++] = y;
            }
            if ((step & 2) != 0)
                x++;
            else if ((step & 8) != 0)
                x--;
            if ((step & 1) != 0)
                y++;
            else if ((step & 4) != 0)
                y--;
        }

        return readPtr;
    }

    public void setObjectAdjacency(int x, int y, int dir, int id) {
        if (x < 0 || y < 0 || x >= 95 || y >= 95)
            return;
        if (GameData.wallObjectAdjacent[id] == 1) {
            if (dir == 0) {
                objectAdjacency[x][y] |= 1;
                if (y > 0)
                    setObjectAdjacency(x, y - 1, 4);
            } else if (dir == 1) {
                objectAdjacency[x][y] |= 2;
                if (x > 0)
                    setObjectAdjacency(x - 1, y, 8);
            } else if (dir == 2)
                objectAdjacency[x][y] |= 0x10;
            else if (dir == 3)
                objectAdjacency[x][y] |= 0x20;
            method404(x, y, 1, 1);
        }
    }

    public void loadSection(int x, int y, int plane, boolean flag) {
        int l = (x + 24) / 48;
        int i1 = (y + 24) / 48;
        loadSection(l - 1, i1 - 1, plane, 0);
        loadSection(l, i1 - 1, plane, 1);
        loadSection(l - 1, i1, plane, 2);
        loadSection(l, i1, plane, 3);
        setTiles();
        if (parentModel == null)
            parentModel = new GameModel(18688, 18688, true, true, false, false, true);
        if (flag) {
            surface.blackScreen();
            for (int j1 = 0; j1 < regionWidth; j1++) {
                for (int l1 = 0; l1 < regionHeight; l1++)
                    objectAdjacency[j1][l1] = 0;

            }

            GameModel gameModel = parentModel;
            gameModel.clear();
            for (int j2 = 0; j2 < regionWidth; j2++) {
                for (int i3 = 0; i3 < regionHeight; i3++) {
                    int i4 = -getTerrainHeight(j2, i3);
                    if (getTileDecoration(j2, i3, plane) > 0 && GameData.tileType[getTileDecoration(j2, i3, plane) - 1] == 4)
                        i4 = 0;
                    if (getTileDecoration(j2 - 1, i3, plane) > 0 && GameData.tileType[getTileDecoration(j2 - 1, i3, plane) - 1] == 4)
                        i4 = 0;
                    if (getTileDecoration(j2, i3 - 1, plane) > 0 && GameData.tileType[getTileDecoration(j2, i3 - 1, plane) - 1] == 4)
                        i4 = 0;
                    if (getTileDecoration(j2 - 1, i3 - 1, plane) > 0 && GameData.tileType[getTileDecoration(j2 - 1, i3 - 1, plane) - 1] == 4)
                        i4 = 0;
                    int j5 = gameModel.vertexAt(j2 * anInt585, i4, i3 * anInt585);
                    int j7 = (int) (Math.random() * 10D) - 5;
                    gameModel.setVertexAmbience(j5, j7);
                }

            }

            for (int lx = 0; lx < 95; lx++) {
                for (int ly = 0; ly < 95; ly++) {
                    int colourindex = getTerrainColour(lx, ly);
                    int colour = terrainColours[colourindex];
                    int colour_1 = colour;
                    int colour_2 = colour;
                    int l14 = 0;
                    if (plane == 1 || plane == 2) {
                        colour = colourTransparent;
                        colour_1 = colourTransparent;
                        colour_2 = colourTransparent;
                    }
                    if (getTileDecoration(lx, ly, plane) > 0) {
                        int decoration_type = getTileDecoration(lx, ly, plane);
                        int decoration_tile_type = GameData.tileType[decoration_type - 1];
                        int tile_type = getTileType(lx, ly, plane);
                        colour = colour_1 = GameData.tileDecoration[decoration_type - 1];
                        if (decoration_tile_type == 4) {
                            colour = 1;
                            colour_1 = 1;
                            if (decoration_type == 12) {
                                colour = 31;
                                colour_1 = 31;
                            }
                        }
                        if (decoration_tile_type == 5) {
                            if (getWallDiagonal(lx, ly) > 0 && getWallDiagonal(lx, ly) < 24000)
                                if (getTileDecoration(lx - 1, ly, plane, colour_2) != colourTransparent && getTileDecoration(lx, ly - 1, plane, colour_2) != colourTransparent) {
                                    colour = getTileDecoration(lx - 1, ly, plane, colour_2);
                                    l14 = 0;
                                } else if (getTileDecoration(lx + 1, ly, plane, colour_2) != colourTransparent && getTileDecoration(lx, ly + 1, plane, colour_2) != colourTransparent) {
                                    colour_1 = getTileDecoration(lx + 1, ly, plane, colour_2);
                                    l14 = 0;
                                } else if (getTileDecoration(lx + 1, ly, plane, colour_2) != colourTransparent && getTileDecoration(lx, ly - 1, plane, colour_2) != colourTransparent) {
                                    colour_1 = getTileDecoration(lx + 1, ly, plane, colour_2);
                                    l14 = 1;
                                } else if (getTileDecoration(lx - 1, ly, plane, colour_2) != colourTransparent && getTileDecoration(lx, ly + 1, plane, colour_2) != colourTransparent) {
                                    colour = getTileDecoration(lx - 1, ly, plane, colour_2);
                                    l14 = 1;
                                }
                        } else if (decoration_tile_type != 2 || getWallDiagonal(lx, ly) > 0 && getWallDiagonal(lx, ly) < 24000)
                            if (getTileType(lx - 1, ly, plane) != tile_type && getTileType(lx, ly - 1, plane) != tile_type) {
                                colour = colour_2;
                                l14 = 0;
                            } else if (getTileType(lx + 1, ly, plane) != tile_type && getTileType(lx, ly + 1, plane) != tile_type) {
                                colour_1 = colour_2;
                                l14 = 0;
                            } else if (getTileType(lx + 1, ly, plane) != tile_type && getTileType(lx, ly - 1, plane) != tile_type) {
                                colour_1 = colour_2;
                                l14 = 1;
                            } else if (getTileType(lx - 1, ly, plane) != tile_type && getTileType(lx, ly + 1, plane) != tile_type) {
                                colour = colour_2;
                                l14 = 1;
                            }
                        if (GameData.tileAdjacent[decoration_type - 1] != 0)
                            objectAdjacency[lx][ly] |= 0x40;
                        if (GameData.tileType[decoration_type - 1] == 2)
                            objectAdjacency[lx][ly] |= 0x80;
                    }
                    method402(lx, ly, l14, colour, colour_1);
                    int i17 = ((getTerrainHeight(lx + 1, ly + 1) - getTerrainHeight(lx + 1, ly)) + getTerrainHeight(lx, ly + 1)) - getTerrainHeight(lx, ly);
                    if (colour != colour_1 || i17 != 0) {
                        int ai[] = new int[3];
                        int ai7[] = new int[3];
                        if (l14 == 0) {
                            if (colour != colourTransparent) {
                                ai[0] = ly + lx * 96 + 96;
                                ai[1] = ly + lx * 96;
                                ai[2] = ly + lx * 96 + 1;
                                int l21 = gameModel.createFace(3, ai, colourTransparent, colour);
                                localX[l21] = lx;
                                localY[l21] = ly;
                                gameModel.faceTag[l21] = 0x30d40 + l21;
                            }
                            if (colour_1 != colourTransparent) {
                                ai7[0] = ly + lx * 96 + 1;
                                ai7[1] = ly + lx * 96 + 96 + 1;
                                ai7[2] = ly + lx * 96 + 96;
                                int i22 = gameModel.createFace(3, ai7, colourTransparent, colour_1);
                                localX[i22] = lx;
                                localY[i22] = ly;
                                gameModel.faceTag[i22] = 0x30d40 + i22;
                            }
                        } else {
                            if (colour != colourTransparent) {
                                ai[0] = ly + lx * 96 + 1;
                                ai[1] = ly + lx * 96 + 96 + 1;
                                ai[2] = ly + lx * 96;
                                int j22 = gameModel.createFace(3, ai, colourTransparent, colour);
                                localX[j22] = lx;
                                localY[j22] = ly;
                                gameModel.faceTag[j22] = 0x30d40 + j22;
                            }
                            if (colour_1 != colourTransparent) {
                                ai7[0] = ly + lx * 96 + 96;
                                ai7[1] = ly + lx * 96;
                                ai7[2] = ly + lx * 96 + 96 + 1;
                                int k22 = gameModel.createFace(3, ai7, colourTransparent, colour_1);
                                localX[k22] = lx;
                                localY[k22] = ly;
                                gameModel.faceTag[k22] = 0x30d40 + k22;
                            }
                        }
                    } else if (colour != colourTransparent) {
                        int ai1[] = new int[4];
                        ai1[0] = ly + lx * 96 + 96;
                        ai1[1] = ly + lx * 96;
                        ai1[2] = ly + lx * 96 + 1;
                        ai1[3] = ly + lx * 96 + 96 + 1;
                        int l19 = gameModel.createFace(4, ai1, colourTransparent, colour);
                        localX[l19] = lx;
                        localY[l19] = ly;
                        gameModel.faceTag[l19] = 0x30d40 + l19;
                    }
                }

            }

            for (int k4 = 1; k4 < 95; k4++) {
                for (int i6 = 1; i6 < 95; i6++)
                    if (getTileDecoration(k4, i6, plane) > 0 && GameData.tileType[getTileDecoration(k4, i6, plane) - 1] == 4) {
                        int l7 = GameData.tileDecoration[getTileDecoration(k4, i6, plane) - 1];
                        int j10 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6), i6 * anInt585);
                        int l12 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6), i6 * anInt585);
                        int i15 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6 + 1), (i6 + 1) * anInt585);
                        int j17 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6 + 1), (i6 + 1) * anInt585);
                        int ai2[] = {
                                j10, l12, i15, j17
                        };
                        int i20 = gameModel.createFace(4, ai2, l7, colourTransparent);
                        localX[i20] = k4;
                        localY[i20] = i6;
                        gameModel.faceTag[i20] = 0x30d40 + i20;
                        method402(k4, i6, 0, l7, l7);
                    } else if (getTileDecoration(k4, i6, plane) == 0 || GameData.tileType[getTileDecoration(k4, i6, plane) - 1] != 3) {
                        if (getTileDecoration(k4, i6 + 1, plane) > 0 && GameData.tileType[getTileDecoration(k4, i6 + 1, plane) - 1] == 4) {
                            int i8 = GameData.tileDecoration[getTileDecoration(k4, i6 + 1, plane) - 1];
                            int k10 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6), i6 * anInt585);
                            int i13 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6), i6 * anInt585);
                            int j15 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6 + 1), (i6 + 1) * anInt585);
                            int k17 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6 + 1), (i6 + 1) * anInt585);
                            int ai3[] = {
                                    k10, i13, j15, k17
                            };
                            int j20 = gameModel.createFace(4, ai3, i8, colourTransparent);
                            localX[j20] = k4;
                            localY[j20] = i6;
                            gameModel.faceTag[j20] = 0x30d40 + j20;
                            method402(k4, i6, 0, i8, i8);
                        }
                        if (getTileDecoration(k4, i6 - 1, plane) > 0 && GameData.tileType[getTileDecoration(k4, i6 - 1, plane) - 1] == 4) {
                            int j8 = GameData.tileDecoration[getTileDecoration(k4, i6 - 1, plane) - 1];
                            int l10 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6), i6 * anInt585);
                            int j13 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6), i6 * anInt585);
                            int k15 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6 + 1), (i6 + 1) * anInt585);
                            int l17 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6 + 1), (i6 + 1) * anInt585);
                            int ai4[] = {
                                    l10, j13, k15, l17
                            };
                            int k20 = gameModel.createFace(4, ai4, j8, colourTransparent);
                            localX[k20] = k4;
                            localY[k20] = i6;
                            gameModel.faceTag[k20] = 0x30d40 + k20;
                            method402(k4, i6, 0, j8, j8);
                        }
                        if (getTileDecoration(k4 + 1, i6, plane) > 0 && GameData.tileType[getTileDecoration(k4 + 1, i6, plane) - 1] == 4) {
                            int k8 = GameData.tileDecoration[getTileDecoration(k4 + 1, i6, plane) - 1];
                            int i11 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6), i6 * anInt585);
                            int k13 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6), i6 * anInt585);
                            int l15 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6 + 1), (i6 + 1) * anInt585);
                            int i18 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6 + 1), (i6 + 1) * anInt585);
                            int ai5[] = {
                                    i11, k13, l15, i18
                            };
                            int l20 = gameModel.createFace(4, ai5, k8, colourTransparent);
                            localX[l20] = k4;
                            localY[l20] = i6;
                            gameModel.faceTag[l20] = 0x30d40 + l20;
                            method402(k4, i6, 0, k8, k8);
                        }
                        if (getTileDecoration(k4 - 1, i6, plane) > 0 && GameData.tileType[getTileDecoration(k4 - 1, i6, plane) - 1] == 4) {
                            int l8 = GameData.tileDecoration[getTileDecoration(k4 - 1, i6, plane) - 1];
                            int j11 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6), i6 * anInt585);
                            int l13 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6), i6 * anInt585);
                            int i16 = gameModel.vertexAt((k4 + 1) * anInt585, -getTerrainHeight(k4 + 1, i6 + 1), (i6 + 1) * anInt585);
                            int j18 = gameModel.vertexAt(k4 * anInt585, -getTerrainHeight(k4, i6 + 1), (i6 + 1) * anInt585);
                            int ai6[] = {
                                    j11, l13, i16, j18
                            };
                            int i21 = gameModel.createFace(4, ai6, l8, colourTransparent);
                            localX[i21] = k4;
                            localY[i21] = i6;
                            gameModel.faceTag[i21] = 0x30d40 + i21;
                            method402(k4, i6, 0, l8, l8);
                        }
                    }

            }

            gameModel.setLight(true, 40, 48, -50, -10, -50);
            terrainModels = parentModel.split(0, 0, 1536, 1536, 8, 64, 233, false);
            for (int j6 = 0; j6 < 64; j6++)
                scene.addModel(terrainModels[j6]);

            for (int X = 0; X < regionWidth; X++) {
                for (int Y = 0; Y < regionHeight; Y++)
                    terrainHeightLocal[X][Y] = getTerrainHeight(X, Y);

            }

        }
        parentModel.clear();
        int k1 = 0x606060;
        for (int i2 = 0; i2 < 95; i2++) {
            for (int k2 = 0; k2 < 95; k2++) {
                int k3 = getWallEastwest(i2, k2);
                if (k3 > 0 && (GameData.wallObjectInvisible[k3 - 1] == 0 || aBoolean592)) {
                    method422(parentModel, k3 - 1, i2, k2, i2 + 1, k2);
                    if (flag && GameData.wallObjectAdjacent[k3 - 1] != 0) {
                        objectAdjacency[i2][k2] |= 1;
                        if (k2 > 0)
                            setObjectAdjacency(i2, k2 - 1, 4);
                    }
                    if (flag)
                        surface.drawLineHoriz(i2 * 3, k2 * 3, 3, k1);
                }
                k3 = getWallNorthsouth(i2, k2);
                if (k3 > 0 && (GameData.wallObjectInvisible[k3 - 1] == 0 || aBoolean592)) {
                    method422(parentModel, k3 - 1, i2, k2, i2, k2 + 1);
                    if (flag && GameData.wallObjectAdjacent[k3 - 1] != 0) {
                        objectAdjacency[i2][k2] |= 2;
                        if (i2 > 0)
                            setObjectAdjacency(i2 - 1, k2, 8);
                    }
                    if (flag)
                        surface.drawLineVert(i2 * 3, k2 * 3, 3, k1);
                }
                k3 = getWallDiagonal(i2, k2);
                if (k3 > 0 && k3 < 12000 && (GameData.wallObjectInvisible[k3 - 1] == 0 || aBoolean592)) {
                    method422(parentModel, k3 - 1, i2, k2, i2 + 1, k2 + 1);
                    if (flag && GameData.wallObjectAdjacent[k3 - 1] != 0)
                        objectAdjacency[i2][k2] |= 0x20;
                    if (flag) {
                        surface.setPixel(i2 * 3, k2 * 3, k1);
                        surface.setPixel(i2 * 3 + 1, k2 * 3 + 1, k1);
                        surface.setPixel(i2 * 3 + 2, k2 * 3 + 2, k1);
                    }
                }
                if (k3 > 12000 && k3 < 24000 && (GameData.wallObjectInvisible[k3 - 12001] == 0 || aBoolean592)) {
                    method422(parentModel, k3 - 12001, i2 + 1, k2, i2, k2 + 1);
                    if (flag && GameData.wallObjectAdjacent[k3 - 12001] != 0)
                        objectAdjacency[i2][k2] |= 0x10;
                    if (flag) {
                        surface.setPixel(i2 * 3 + 2, k2 * 3, k1);
                        surface.setPixel(i2 * 3 + 1, k2 * 3 + 1, k1);
                        surface.setPixel(i2 * 3, k2 * 3 + 2, k1);
                    }
                }
            }

        }

        if (flag)
            surface.drawSpriteMinimap(baseMediaSprite - 1, 0, 0, 285, 285);
        parentModel.setLight(false, 60, 24, -50, -10, -50);
        wallModels[plane] = parentModel.split(0, 0, 1536, 1536, 8, 64, 338, true);
        for (int l2 = 0; l2 < 64; l2++)
            scene.addModel(wallModels[plane][l2]);

        for (int l3 = 0; l3 < 95; l3++) {
            for (int l4 = 0; l4 < 95; l4++) {
                int k6 = getWallEastwest(l3, l4);
                if (k6 > 0)
                    method428(k6 - 1, l3, l4, l3 + 1, l4);
                k6 = getWallNorthsouth(l3, l4);
                if (k6 > 0)
                    method428(k6 - 1, l3, l4, l3, l4 + 1);
                k6 = getWallDiagonal(l3, l4);
                if (k6 > 0 && k6 < 12000)
                    method428(k6 - 1, l3, l4, l3 + 1, l4 + 1);
                if (k6 > 12000 && k6 < 24000)
                    method428(k6 - 12001, l3 + 1, l4, l3, l4 + 1);
            }

        }

        for (int i5 = 1; i5 < 95; i5++) {
            for (int l6 = 1; l6 < 95; l6++) {
                int j9 = getWallRoof(i5, l6);
                if (j9 > 0) {
                    int l11 = i5;
                    int i14 = l6;
                    int j16 = i5 + 1;
                    int k18 = l6;
                    int j19 = i5 + 1;
                    int j21 = l6 + 1;
                    int l22 = i5;
                    int j23 = l6 + 1;
                    int l23 = 0;
                    int j24 = terrainHeightLocal[l11][i14];
                    int l24 = terrainHeightLocal[j16][k18];
                    int j25 = terrainHeightLocal[j19][j21];
                    int l25 = terrainHeightLocal[l22][j23];
                    if (j24 > 0x13880)
                        j24 -= 0x13880;
                    if (l24 > 0x13880)
                        l24 -= 0x13880;
                    if (j25 > 0x13880)
                        j25 -= 0x13880;
                    if (l25 > 0x13880)
                        l25 -= 0x13880;
                    if (j24 > l23)
                        l23 = j24;
                    if (l24 > l23)
                        l23 = l24;
                    if (j25 > l23)
                        l23 = j25;
                    if (l25 > l23)
                        l23 = l25;
                    if (l23 >= 0x13880)
                        l23 -= 0x13880;
                    if (j24 < 0x13880)
                        terrainHeightLocal[l11][i14] = l23;
                    else
                        terrainHeightLocal[l11][i14] -= 0x13880;
                    if (l24 < 0x13880)
                        terrainHeightLocal[j16][k18] = l23;
                    else
                        terrainHeightLocal[j16][k18] -= 0x13880;
                    if (j25 < 0x13880)
                        terrainHeightLocal[j19][j21] = l23;
                    else
                        terrainHeightLocal[j19][j21] -= 0x13880;
                    if (l25 < 0x13880)
                        terrainHeightLocal[l22][j23] = l23;
                    else
                        terrainHeightLocal[l22][j23] -= 0x13880;
                }
            }

        }

        parentModel.clear();
        for (int i7 = 1; i7 < 95; i7++) {
            for (int k9 = 1; k9 < 95; k9++) {
                int roof_nvs = getWallRoof(i7, k9);
                if (roof_nvs > 0) {
                    int j14 = i7;
                    int k16 = k9;
                    int l18 = i7 + 1;
                    int k19 = k9;
                    int k21 = i7 + 1;
                    int i23 = k9 + 1;
                    int k23 = i7;
                    int i24 = k9 + 1;
                    int k24 = i7 * anInt585;
                    int i25 = k9 * anInt585;
                    int k25 = k24 + anInt585;
                    int i26 = i25 + anInt585;
                    int j26 = k24;
                    int k26 = i25;
                    int l26 = k25;
                    int i27 = i26;
                    int j27 = terrainHeightLocal[j14][k16];
                    int k27 = terrainHeightLocal[l18][k19];
                    int l27 = terrainHeightLocal[k21][i23];
                    int i28 = terrainHeightLocal[k23][i24];
                    int unknown = GameData.roofHeight[roof_nvs - 1];
                    if (hasRoof(j14, k16) && j27 < 0x13880) {
                        j27 += unknown + 0x13880;
                        terrainHeightLocal[j14][k16] = j27;
                    }
                    if (hasRoof(l18, k19) && k27 < 0x13880) {
                        k27 += unknown + 0x13880;
                        terrainHeightLocal[l18][k19] = k27;
                    }
                    if (hasRoof(k21, i23) && l27 < 0x13880) {
                        l27 += unknown + 0x13880;
                        terrainHeightLocal[k21][i23] = l27;
                    }
                    if (hasRoof(k23, i24) && i28 < 0x13880) {
                        i28 += unknown + 0x13880;
                        terrainHeightLocal[k23][i24] = i28;
                    }
                    if (j27 >= 0x13880)
                        j27 -= 0x13880;
                    if (k27 >= 0x13880)
                        k27 -= 0x13880;
                    if (l27 >= 0x13880)
                        l27 -= 0x13880;
                    if (i28 >= 0x13880)
                        i28 -= 0x13880;
                    byte byte0 = 16;
                    if (!method427(j14 - 1, k16))
                        k24 -= byte0;
                    if (!method427(j14 + 1, k16))
                        k24 += byte0;
                    if (!method427(j14, k16 - 1))
                        i25 -= byte0;
                    if (!method427(j14, k16 + 1))
                        i25 += byte0;
                    if (!method427(l18 - 1, k19))
                        k25 -= byte0;
                    if (!method427(l18 + 1, k19))
                        k25 += byte0;
                    if (!method427(l18, k19 - 1))
                        k26 -= byte0;
                    if (!method427(l18, k19 + 1))
                        k26 += byte0;
                    if (!method427(k21 - 1, i23))
                        l26 -= byte0;
                    if (!method427(k21 + 1, i23))
                        l26 += byte0;
                    if (!method427(k21, i23 - 1))
                        i26 -= byte0;
                    if (!method427(k21, i23 + 1))
                        i26 += byte0;
                    if (!method427(k23 - 1, i24))
                        j26 -= byte0;
                    if (!method427(k23 + 1, i24))
                        j26 += byte0;
                    if (!method427(k23, i24 - 1))
                        i27 -= byte0;
                    if (!method427(k23, i24 + 1))
                        i27 += byte0;
                    roof_nvs = GameData.roofNumVertices[roof_nvs - 1];
                    j27 = -j27;
                    k27 = -k27;
                    l27 = -l27;
                    i28 = -i28;
                    if (getWallDiagonal(i7, k9) > 12000 && getWallDiagonal(i7, k9) < 24000 && getWallRoof(i7 - 1, k9 - 1) == 0) {
                        int ai8[] = new int[3];
                        ai8[0] = parentModel.vertexAt(l26, l27, i26);
                        ai8[1] = parentModel.vertexAt(j26, i28, i27);
                        ai8[2] = parentModel.vertexAt(k25, k27, k26);
                        parentModel.createFace(3, ai8, roof_nvs, colourTransparent);
                    } else if (getWallDiagonal(i7, k9) > 12000 && getWallDiagonal(i7, k9) < 24000 && getWallRoof(i7 + 1, k9 + 1) == 0) {
                        int ai9[] = new int[3];
                        ai9[0] = parentModel.vertexAt(k24, j27, i25);
                        ai9[1] = parentModel.vertexAt(k25, k27, k26);
                        ai9[2] = parentModel.vertexAt(j26, i28, i27);
                        parentModel.createFace(3, ai9, roof_nvs, colourTransparent);
                    } else if (getWallDiagonal(i7, k9) > 0 && getWallDiagonal(i7, k9) < 12000 && getWallRoof(i7 + 1, k9 - 1) == 0) {
                        int ai10[] = new int[3];
                        ai10[0] = parentModel.vertexAt(j26, i28, i27);
                        ai10[1] = parentModel.vertexAt(k24, j27, i25);
                        ai10[2] = parentModel.vertexAt(l26, l27, i26);
                        parentModel.createFace(3, ai10, roof_nvs, colourTransparent);
                    } else if (getWallDiagonal(i7, k9) > 0 && getWallDiagonal(i7, k9) < 12000 && getWallRoof(i7 - 1, k9 + 1) == 0) {
                        int ai11[] = new int[3];
                        ai11[0] = parentModel.vertexAt(k25, k27, k26);
                        ai11[1] = parentModel.vertexAt(l26, l27, i26);
                        ai11[2] = parentModel.vertexAt(k24, j27, i25);
                        parentModel.createFace(3, ai11, roof_nvs, colourTransparent);
                    } else if (j27 == k27 && l27 == i28) {
                        int ai12[] = new int[4];
                        ai12[0] = parentModel.vertexAt(k24, j27, i25);
                        ai12[1] = parentModel.vertexAt(k25, k27, k26);
                        ai12[2] = parentModel.vertexAt(l26, l27, i26);
                        ai12[3] = parentModel.vertexAt(j26, i28, i27);
                        parentModel.createFace(4, ai12, roof_nvs, colourTransparent);
                    } else if (j27 == i28 && k27 == l27) {
                        int ai13[] = new int[4];
                        ai13[0] = parentModel.vertexAt(j26, i28, i27);
                        ai13[1] = parentModel.vertexAt(k24, j27, i25);
                        ai13[2] = parentModel.vertexAt(k25, k27, k26);
                        ai13[3] = parentModel.vertexAt(l26, l27, i26);
                        parentModel.createFace(4, ai13, roof_nvs, colourTransparent);
                    } else {
                        boolean flag1 = true;
                        if (getWallRoof(i7 - 1, k9 - 1) > 0)
                            flag1 = false;
                        if (getWallRoof(i7 + 1, k9 + 1) > 0)
                            flag1 = false;
                        if (!flag1) {
                            int ai14[] = new int[3];
                            ai14[0] = parentModel.vertexAt(k25, k27, k26);
                            ai14[1] = parentModel.vertexAt(l26, l27, i26);
                            ai14[2] = parentModel.vertexAt(k24, j27, i25);
                            parentModel.createFace(3, ai14, roof_nvs, colourTransparent);
                            int ai16[] = new int[3];
                            ai16[0] = parentModel.vertexAt(j26, i28, i27);
                            ai16[1] = parentModel.vertexAt(k24, j27, i25);
                            ai16[2] = parentModel.vertexAt(l26, l27, i26);
                            parentModel.createFace(3, ai16, roof_nvs, colourTransparent);
                        } else {
                            int ai15[] = new int[3];
                            ai15[0] = parentModel.vertexAt(k24, j27, i25);
                            ai15[1] = parentModel.vertexAt(k25, k27, k26);
                            ai15[2] = parentModel.vertexAt(j26, i28, i27);
                            parentModel.createFace(3, ai15, roof_nvs, colourTransparent);
                            int ai17[] = new int[3];
                            ai17[0] = parentModel.vertexAt(l26, l27, i26);
                            ai17[1] = parentModel.vertexAt(j26, i28, i27);
                            ai17[2] = parentModel.vertexAt(k25, k27, k26);
                            parentModel.createFace(3, ai17, roof_nvs, colourTransparent);
                        }
                    }
                }
            }

        }

        parentModel.setLight(true, 50, 50, -50, -10, -50);
        roofModels[plane] = parentModel.split(0, 0, 1536, 1536, 8, 64, 169, true);
        for (int l9 = 0; l9 < 64; l9++)
            scene.addModel(roofModels[plane][l9]);

        if (roofModels[plane][0] == null)
            throw new RuntimeException("null roof!");
        for (int j12 = 0; j12 < regionWidth; j12++) {
            for (int k14 = 0; k14 < regionHeight; k14++)
                if (terrainHeightLocal[j12][k14] >= 0x13880)
                    terrainHeightLocal[j12][k14] -= 0x13880;

        }

    }

    public void setObjectAdjacency(int i, int j, int k) {
        objectAdjacency[i][j] |= k;
    }

    public int getTileType(int i, int j, int k) {
        int l = getTileDecoration(i, j, k);
        if (l == 0)
            return -1;
        int i1 = GameData.tileType[l - 1];
        return i1 != 2 ? 0 : 1;
    }

    public void addModels(GameModel aclass5[]) {
        for (int i = 0; i < 94; i++) {
            for (int j = 0; j < 94; j++)
                if (getWallDiagonal(i, j) > 48000 && getWallDiagonal(i, j) < 60000) {
                    int k = getWallDiagonal(i, j) - 48001;
                    int l = getTileDirection(i, j);
                    int i1;
                    int j1;
                    if (l == 0 || l == 4) {
                        i1 = GameData.objectWidth[k];
                        j1 = GameData.objectHeight[k];
                    } else {
                        j1 = GameData.objectWidth[k];
                        i1 = GameData.objectHeight[k];
                    }
                    removeObject2(i, j, k);
                    GameModel gameModel = aclass5[GameData.objectModelIndex[k]].copy(false, true, false, false);
                    int k1 = ((i + i + i1) * anInt585) / 2;
                    int i2 = ((j + j + j1) * anInt585) / 2;
                    gameModel.translate(k1, -getElevation(k1, i2), i2);
                    gameModel.orient(0, getTileDirection(i, j) * 32, 0);
                    scene.addModel(gameModel);
                    gameModel.setLight(48, 48, -50, -10, -50);
                    if (i1 > 1 || j1 > 1) {
                        for (int k2 = i; k2 < i + i1; k2++) {
                            for (int l2 = j; l2 < j + j1; l2++)
                                if ((k2 > i || l2 > j) && getWallDiagonal(k2, l2) - 48001 == k) {
                                    int l1 = k2;
                                    int j2 = l2;
                                    byte byte0 = 0;
                                    if (l1 >= 48 && j2 < 48) {
                                        byte0 = 1;
                                        l1 -= 48;
                                    } else if (l1 < 48 && j2 >= 48) {
                                        byte0 = 2;
                                        j2 -= 48;
                                    } else if (l1 >= 48 && j2 >= 48) {
                                        byte0 = 3;
                                        l1 -= 48;
                                        j2 -= 48;
                                    }
                                    wallsDiagonal[byte0][l1 * 48 + j2] = 0;
                                }

                        }

                    }
                }

        }

    }

    public void method422(GameModel gameModel, int i, int j, int k, int l, int i1) {
        method425(j, k, 40);
        method425(l, i1, 40);
        int h = GameData.wallObjectHeight[i];
        int front = GameData.wallObjectTextureFront[i];
        int back = GameData.wallObjectTextureBack[i];
        int i2 = j * anInt585;
        int j2 = k * anInt585;
        int k2 = l * anInt585;
        int l2 = i1 * anInt585;
        int i3 = gameModel.vertexAt(i2, -terrainHeightLocal[j][k], j2);
        int j3 = gameModel.vertexAt(i2, -terrainHeightLocal[j][k] - h, j2);
        int k3 = gameModel.vertexAt(k2, -terrainHeightLocal[l][i1] - h, l2);
        int l3 = gameModel.vertexAt(k2, -terrainHeightLocal[l][i1], l2);
        int ai[] = {
                i3, j3, k3, l3
        };
        int i4 = gameModel.createFace(4, ai, front, back);
        if (GameData.wallObjectInvisible[i] == 5) {
            gameModel.faceTag[i4] = 30000 + i;
            return;
        } else {
            gameModel.faceTag[i4] = 0;
            return;
        }
    }

    public int getTerrainHeight(int x, int y) {
        if (x < 0 || x >= regionWidth || y < 0 || y >= regionHeight)
            return 0;
        byte d = 0;
        if (x >= 48 && y < 48) {
            d = 1;
            x -= 48;
        } else if (x < 48 && y >= 48) {
            d = 2;
            y -= 48;
        } else if (x >= 48 && y >= 48) {
            d = 3;
            x -= 48;
            y -= 48;
        }
        return (terrainHeight[d][x * 48 + y] & 0xff) * 3;
    }

    public void loadSection(int x, int y, int plane) {
        reset();
        int l = (x + 24) / 48;
        int i1 = (y + 24) / 48;
        loadSection(x, y, plane, true);
        if (plane == 0) {
            loadSection(x, y, 1, false);
            loadSection(x, y, 2, false);
            loadSection(l - 1, i1 - 1, plane, 0);
            loadSection(l, i1 - 1, plane, 1);
            loadSection(l - 1, i1, plane, 2);
            loadSection(l, i1, plane, 3);
            setTiles();
        }
    }

    public void method425(int i, int j, int k) {
        int l = i / 12;
        int i1 = j / 12;
        int j1 = (i - 1) / 12;
        int k1 = (j - 1) / 12;
        setTerrainAmbience(l, i1, i, j, k);
        if (l != j1)
            setTerrainAmbience(j1, i1, i, j, k);
        if (i1 != k1)
            setTerrainAmbience(l, k1, i, j, k);
        if (l != j1 && i1 != k1)
            setTerrainAmbience(j1, k1, i, j, k);
    }

    public void removeObject(int x, int y, int id) {
        if (x < 0 || y < 0 || x >= 95 || y >= 95)
            return;
        if (GameData.objectType[id] == 1 || GameData.objectType[id] == 2) {
            int l = getTileDirection(x, y);
            int i1;
            int j1;
            if (l == 0 || l == 4) {
                i1 = GameData.objectWidth[id];
                j1 = GameData.objectHeight[id];
            } else {
                j1 = GameData.objectWidth[id];
                i1 = GameData.objectHeight[id];
            }
            for (int k1 = x; k1 < x + i1; k1++) {
                for (int l1 = y; l1 < y + j1; l1++)
                    if (GameData.objectType[id] == 1)
                        objectAdjacency[k1][l1] &= 0xffbf;
                    else if (l == 0) {
                        objectAdjacency[k1][l1] &= 0xfffd;
                        if (k1 > 0)
                            method407(k1 - 1, l1, 8);
                    } else if (l == 2) {
                        objectAdjacency[k1][l1] &= 0xfffb;
                        if (l1 < 95)
                            method407(k1, l1 + 1, 1);
                    } else if (l == 4) {
                        objectAdjacency[k1][l1] &= 0xfff7;
                        if (k1 < 95)
                            method407(k1 + 1, l1, 2);
                    } else if (l == 6) {
                        objectAdjacency[k1][l1] &= 0xfffe;
                        if (l1 > 0)
                            method407(k1, l1 - 1, 4);
                    }

            }

            method404(x, y, i1, j1);
        }
    }

    public boolean method427(int i, int j) {
        return getWallRoof(i, j) > 0 || getWallRoof(i - 1, j) > 0 || getWallRoof(i - 1, j - 1) > 0 || getWallRoof(i, j - 1) > 0;
    }

    public void method428(int i, int j, int k, int l, int i1) {
        int j1 = GameData.wallObjectHeight[i];
        if (terrainHeightLocal[j][k] < 0x13880)
            terrainHeightLocal[j][k] += 0x13880 + j1;
        if (terrainHeightLocal[l][i1] < 0x13880)
            terrainHeightLocal[l][i1] += 0x13880 + j1;
    }
}
