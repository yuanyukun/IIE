package com.horem.parachute.util;


import java.io.*;

public class IOUtils {

    public static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeBytesToFile(byte[] data, File file) {
        OutputStream os = null;

        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            safeClose(os);
        }
    }

    public static byte[] readFileAsBytes(File file) {
        if (!file.exists() || file.length() == 0)
            return null;

        int fileLength = (int)file.length();
        byte[] bytes = new byte[fileLength];
        InputStream is = null;

        try {
            is = new FileInputStream(file);
            int lenRead = 0;

            while (lenRead < fileLength) {
                lenRead += is.read(bytes, lenRead, fileLength - lenRead);
            }

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            safeClose(is);
        }

        return bytes;
    }

    public static boolean copyStream(InputStream is, OutputStream os) {
        try {
            byte buffer[] = new byte[4196];
            int lenRead;

            while ((lenRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, lenRead);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static int readUnsignedByte(DataInput di) throws IOException {
        return di.readByte() & ((int)Byte.MAX_VALUE * 2) + 1;
    }

    public static int readUnsignedShort(DataInput di) throws IOException {
        return di.readShort() & ((int)Short.MAX_VALUE * 2) + 1;
    }

    public static long readUnsignedInt(DataInput di) throws IOException {
        return di.readInt() & ((long)Integer.MAX_VALUE * 2) + 1;
    }

    public static void writeByte(int data, byte[] buffer, int offset) {
        buffer[offset] = (byte)data;
    }

    public static void writeBytes(byte[] data, byte[] buffer, int destOffset) {
        System.arraycopy(data, 0, buffer, destOffset, data.length);
    }

    public static void writeBytes(byte[] data, int srcOffset, int count, byte[] buffer, int destOffset) {
        System.arraycopy(data, srcOffset, buffer, destOffset, count);
    }

    public static void writeShort(short data, byte[] buffer, int offset) {
        buffer[offset] = (byte)(data >>> 8);
        buffer[offset + 1] = (byte)data;
    }

    public static void writeInt(int data, byte[] buffer, int offset) {
        buffer[offset] = (byte)(data >>> 24);
        buffer[offset + 1] = (byte)(data >>> 16);
        buffer[offset + 2] = (byte)(data >>> 8);
        buffer[offset + 3] = (byte)data;
    }

    public static void writeInt(int data, OutputStream os) throws IOException {
        os.write(data >>> 24);
        os.write(data >>> 16);
        os.write(data >>> 8);
        os.write(data);
    }

    public static void writeLong(long data, byte[] buffer, int offset) {
        buffer[offset] = (byte)(data >>> 56);
        buffer[offset + 1] = (byte)(data >>> 48);
        buffer[offset + 2] = (byte)(data >>> 40);
        buffer[offset + 3] = (byte)(data >>> 32);
        buffer[offset + 4] = (byte)(data >>> 24);
        buffer[offset + 5] = (byte)(data >>> 16);
        buffer[offset + 6] = (byte)(data >>> 8);
        buffer[offset + 7] = (byte)data;
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static int readUnsignedByte(byte[] buffer, int offset) throws IOException {
        return buffer[offset] & ((int)Byte.MAX_VALUE * 2) + 1;
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static int readUnsignedShort(byte[] buffer, int offset) {
        short shortValue = readShort(buffer, offset);
        return shortValue & ((int)Short.MAX_VALUE * 2) + 1;
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static long readUnsignedInt(byte[] buffer, int offset) {
        int intValue = readInt(buffer, offset);
        return intValue & ((long)Integer.MAX_VALUE * 2) + 1;
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static int readByte(byte[] buffer, int offset) {
        return buffer[offset];
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static short readShort(byte[] buffer, int offset) {
        return (short)(((buffer[offset] & 0xff) << 8)
                | (buffer[offset + 1] & 0xff));
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static int readInt(byte[] buffer, int offset) {
        return ((buffer[offset] & 0xff) << 24
                | ((buffer[offset + 1] & 0xff) << 16)
                | ((buffer[offset + 2] & 0xff) << 8)
                | ((buffer[offset + 3] & 0xff)));
    }

    // negative numbers are not allowed in this InputStream
    public static int readInt(InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        int ch3 = is.read();
        int ch4 = is.read();

        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            return -1;
        }

        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static long readLong(byte[] buffer, int offset) {
        return ((long)(readInt(buffer, offset)) << 32) + (readInt(buffer, offset + 4) & 0xFFFFFFFFL);
    }

    // NOTE: this method does not check bounds, the caller ensures that
    public static String readUTF(byte[] buffer, int offset, int length) throws UnsupportedEncodingException {
        return new String(buffer, offset, length, "utf-8");
    }
}