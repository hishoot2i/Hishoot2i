package com.common.stream;

public class RandomAccessStreamer {

    private byte[] mData = new byte[8];
    private int cursor = 0;

    public RandomAccessStreamer() {
    }

    public void use(byte[] data) {
        mData = data;
        cursor = 0;
    }

//    public int length() {
//        return mData != null ? mData.length : 0;
//    }
//
//    public int skipBytes(int len) {
//        int newCursor = cursor + len;
//        if (newCursor > mData.length) {
//            newCursor = mData.length;
//        }
//        int skipNum = newCursor - cursor;
//        cursor = newCursor;
//        return skipNum;
//    }
//
//    public int getCursor() {
//        return cursor;
//    }
//
//    public void seek(long pos) {
//        if (pos < 0) {
//            cursor = 0;
//        } else if (pos > mData.length) {
//            cursor = mData.length;
//        } else {
//            cursor = (int) pos;
//        }
//    }

    /**
     * If len > 0 read len bytes else if len < 0 read to the end.
     *
     * @param len length of bytes to read
     * @return byte array
     */
    public byte[] read(int len) {
        if (cursor >= mData.length) {
            // LogUtil.e("Stream has finished!!");
            return null;
        }
        if (cursor + len > mData.length) {
            // LogUtil.e(String.format(Locale.ROOT, "Cannot read %d bytes with only %d remains. Return %bytes!",
            //       len, mData.length - cursor, mData.length - cursor));
            len = mData.length - cursor;
        } else if (len < 0) {
            len = mData.length - cursor;
        }
        byte[] ret = new byte[len];
        System.arraycopy(mData, cursor, ret, 0, len);
        cursor += len;
        return ret;
    }

    protected long readUnsignedInt(@SuppressWarnings("SameParameterValue") Endian endian) {
        byte[] buf = read(4);
        long ret = 0;
        if (endian == Endian.Little) {
            for (int i = 3; i >= 0; --i) {
                ret <<= 8;
                ret |= (buf[i] & 0xff);
            }
        } else {
            for (int i = 0; i <= 3; ++i) {
                ret <<= 8;
                ret |= (buf[i] & 0xff);
            }
        }
        return ret;
    }

//    protected int readSignedInt(Endian endian) {
//        byte[] buf = read(4);
//        int ret = 0;
//        if (endian == Endian.Little) {
//            for (int i = 3; i >= 0; --i) {
//                ret <<= 8;
//                ret |= (buf[i] & 0xff);
//            }
//        } else {
//            for (int i = 0; i <= 3; ++i) {
//                ret <<= 8;
//                ret |= (buf[i] & 0xff);
//            }
//        }
//        return ret;
//    }
//
//    public byte[] readUleb128BytesFrom(RandomAccessFile racFile) throws IOException {
//
//        byte[] buf = new byte[1];
//        long len = 0;
//        long remain = racFile.length() - racFile.getFilePointer();
//        for (int i = 0; i < remain; ++i) {
//            byte b = racFile.readByte();
//            buf[i] = b;
//            ++len;
//            if ((b & 0x80) == 0) {
//                break;
//            }
//            if (len == buf.length) {
//                byte[] expand = new byte[buf.length * 2];
//                System.arraycopy(buf, 0, expand, 0, buf.length);
//                buf = expand;
//            }
//        }
//        if (len < buf.length) {
//            byte[] ret = new byte[(int) len];
//            System.arraycopy(buf, 0, ret, 0, (int) len);
//            return ret;
//        } else {
//            return buf;
//        }
//    }
//
//    public byte[] readUleb128Bytes() {
//        byte[] buf = new byte[5];
//        int len = 0;
//        boolean hitEnd;
//        do {
//            byte[] b = read(1);
//            buf[len] = b[0];
//            hitEnd = (b[0] & 0x80) == 0;
//            ++len;
//        } while (!hitEnd);
//        byte[] ret = new byte[len];
//        System.arraycopy(buf, 0, ret, 0, ret.length);
//        return ret;
//    }
//
//    protected long parseUleb128Int(byte[] bytes, Endian endian) {
//        int len = bytes.length;
//        long res = 0;
//        if (endian == Endian.Little) {
//            for (int i = 0; i < len; ++i) {
//                res |= (bytes[i] & 0x7f) << (7 * i);
//            }
//        } else {
//            for (int i = 0; i < len; ++i) {
//                res |= (bytes[i] & 0x7f) << (7 * (len - i - 1));
//            }
//        }
//        return res;
//    }

    protected int readUnsignedShort(@SuppressWarnings("SameParameterValue") Endian endian) {
        byte[] buf = read(2);
        if (endian == Endian.Little) {
            return (buf[1] & 0xff) << 8 | (buf[0] & 0xff);
        } else {
            return (buf[0] & 0xff) << 8 | (buf[1] & 0xff);
        }
    }

//    protected int readSignedShort(Endian endian) {
//        byte[] buf = read(2);
//        if (endian == Endian.Little) {
//            return (buf[1] & 0xff) << 8 | (buf[0] & 0xff);
//        } else {
//            return (buf[0] & 0xff) << 8 | (buf[1] & 0xff);
//        }
//    }
//
//    public int readUInt8() {
//        byte[] buf = read(1);
//        return 0xff & buf[0];
//    }

    protected char readChar8(
            @SuppressWarnings({"SameParameterValue", "unused", "RedundantSuppression"}) Endian endian
    ) {
        byte[] buf = read(1);
        return (char) buf[0];
    }

    protected char readChar16(@SuppressWarnings("SameParameterValue") Endian endian) {
        byte[] buf = read(2);
        if (endian == Endian.Little) {
            return (char) ((buf[1] & 0xff) << 8 | (buf[0] & 0xff));
        } else {
            return (char) ((buf[0] & 0xff) << 8 | (buf[1] & 0xff));
        }
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Endian {
        Little, Big
    }
}
