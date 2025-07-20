// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import java.io.*;

final class Decompressor {

    public Decompressor(RandomAccessFile dataFile, RandomAccessFile indexFile, int archiveId) {
        this.anInt311 = archiveId;
        this.dataFile = dataFile;
        this.indexFile = indexFile;
    }

    public synchronized byte[] decompress(int fileId) {
        try {
            seekTo(indexFile, fileId * 6);
            int bytesRead;
            for (int j = 0; j < 6; j += bytesRead) {
                bytesRead = indexFile.read(buffer, j, 6 - j);
                if (bytesRead == -1)
                    return null;
            }

            int fileSize = ((buffer[0] & 0xff) << 16) | ((buffer[1] & 0xff) << 8) | (buffer[2] & 0xff);
            int sector = ((buffer[3] & 0xff) << 16) | ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);

            // if(fileSize < 0 || fileSize > 0xffffff)
            //     return null;

            if (sector <= 0 || (long) sector > dataFile.length() / 520L)
                return null;

            byte[] result = new byte[fileSize];
            int offset = 0;

            for (int part = 0; offset < fileSize; part++) {
                if (sector == 0)
                    return null;

                seekTo(dataFile, sector * 520);

                int blockSize = fileSize - offset;
                if (blockSize > 512) blockSize = 512;

                int readLen;
                for (int k = 0; k < blockSize + 8; k += readLen) {
                    readLen = dataFile.read(buffer, k, (blockSize + 8) - k);
                    if (readLen == -1)
                        return null;
                }

                int readFileId = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);
                int partId = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
                int nextSector = ((buffer[4] & 0xff) << 16) | ((buffer[5] & 0xff) << 8) | (buffer[6] & 0xff);
                int archive = buffer[7] & 0xff;

                if (readFileId != fileId || partId != part || archive != anInt311)
                    return null;

                if (nextSector < 0 || (long) nextSector > dataFile.length() / 520L)
                    return null;

                System.arraycopy(buffer, 8, result, offset, blockSize);
                offset += blockSize;
                sector = nextSector;
            }

            return result;

        } catch (IOException e) {
            return null;
        }
    }

    public synchronized boolean write(int length, byte[] data, int fileId) {
        boolean success = writeInternal(true, length, fileId, data);
        if (!success)
            success = writeInternal(false, length, fileId, data);
        return success;
    }

    private synchronized boolean writeInternal(boolean overwrite, int length, int fileId, byte[] data) {
        try {
            int sector;

            if (overwrite) {
                seekTo(indexFile, length * 6);
                int readBytes;
                for (int i = 0; i < 6; i += readBytes) {
                    readBytes = indexFile.read(buffer, i, 6 - i);
                    if (readBytes == -1)
                        return false;
                }
                sector = ((buffer[3] & 0xff) << 16) | ((buffer[4] & 0xff) << 8) | (buffer[5] & 0xff);
                if (sector <= 0 || (long) sector > dataFile.length() / 520L)
                    return false;
            } else {
                sector = (int) ((dataFile.length() + 519L) / 520L);
                if (sector == 0) sector = 1;
            }

            buffer[0] = (byte) (fileId >> 16);
            buffer[1] = (byte) (fileId >> 8);
            buffer[2] = (byte) fileId;
            buffer[3] = (byte) (sector >> 16);
            buffer[4] = (byte) (sector >> 8);
            buffer[5] = (byte) sector;
            seekTo(indexFile, length * 6);
            indexFile.write(buffer, 0, 6);

            int offset = 0;

            for (int part = 0; offset < fileId; part++) {
                int nextSector = 0;

                if (overwrite) {
                    seekTo(dataFile, sector * 520);
                    int readBytes = 0;
                    for (int j = 0; j < 8; j += readBytes) {
                        readBytes = dataFile.read(buffer, j, 8 - j);
                        if (readBytes == -1) break;
                    }

                    if (readBytes == 8) {
                        int readFileId = ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff);
                        int partId = ((buffer[2] & 0xff) << 8) | (buffer[3] & 0xff);
                        nextSector = ((buffer[4] & 0xff) << 16) | ((buffer[5] & 0xff) << 8) | (buffer[6] & 0xff);
                        int archive = buffer[7] & 0xff;

                        if (readFileId != length || partId != part || archive != anInt311)
                            return false;

                        if (nextSector < 0 || (long) nextSector > dataFile.length() / 520L)
                            return false;
                    }
                }

                if (nextSector == 0) {
                    overwrite = false;
                    nextSector = (int) ((dataFile.length() + 519L) / 520L);
                    if (nextSector == 0) nextSector++;
                    if (nextSector == sector) nextSector++;
                }

                if (fileId - offset <= 512) {
                    nextSector = 0;
                }

                buffer[0] = (byte) (length >> 8);
                buffer[1] = (byte) length;
                buffer[2] = (byte) (part >> 8);
                buffer[3] = (byte) part;
                buffer[4] = (byte) (nextSector >> 16);
                buffer[5] = (byte) (nextSector >> 8);
                buffer[6] = (byte) nextSector;
                buffer[7] = (byte) anInt311;

                seekTo(dataFile, sector * 520);
                dataFile.write(buffer, 0, 8);

                int blockSize = fileId - offset;
                if (blockSize > 512) blockSize = 512;
                dataFile.write(data, offset, blockSize);
                offset += blockSize;
                sector = nextSector;
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }

    private synchronized void seekTo(RandomAccessFile file, int pos) throws IOException {
        if (pos < 0 || pos > 0x3c00000) {
            System.out.println("Badseek - pos:" + pos + " len:" + file.length());
            pos = 0x3c00000;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        }
        file.seek(pos);
    }

    private static final byte[] buffer = new byte[520];
    private final RandomAccessFile dataFile;
    private final RandomAccessFile indexFile;
    private final int anInt311;
}
