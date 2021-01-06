package io.whileaway.code;

import java.io.UnsupportedEncodingException;

public class CheckStringCode {

    public static int byteToUnsignedInt(byte data) {
        return data & 0xff;
    }

    public static boolean isUTF8(byte[] pBuffer) {
        boolean IsUTF8 = true;
        boolean IsASCII = true;
        int size = pBuffer.length;
        int i = 0;
        while (i < size) {
            int value = byteToUnsignedInt(pBuffer[i]);
            if (value < 0x80) {
                // (10000000): 值小于 0x80 的为 ASCII 字符
                if (i >= size - 1) {
                    if (IsASCII) {
                        // 假设纯 ASCII 字符不是 UTF 格式
                        IsUTF8 = false;
                    }
                    break;
                }
                i++;
            } else if (value < 0xC0) {
                // (11000000): 值介于 0x80 与 0xC0 之间的为无效 UTF-8 字符
                IsASCII = false;
                IsUTF8 = false;
                break;
            } else if (value < 0xE0) {
                // (11100000): 此范围内为 2 字节 UTF-8 字符
                IsASCII = false;
                if (i >= size - 1) {
                    break;
                }

                int value1 = byteToUnsignedInt(pBuffer[i + 1]);
                if ((value1 & (0xC0)) != 0x80) {
                    IsUTF8 = false;
                    break;
                }

                i += 2;
            } else if (value < 0xF0) {
                IsASCII = false;
                // (11110000): 此范围内为 3 字节 UTF-8 字符
                if (i >= size - 2) {
                    break;
                }

                int value1 = byteToUnsignedInt(pBuffer[i + 1]);
                int value2 = byteToUnsignedInt(pBuffer[i + 2]);
                if ((value1 & (0xC0)) != 0x80 || (value2 & (0xC0)) != 0x80) {
                    IsUTF8 = false;
                    break;
                }

                i += 3;
            }  else if (value < 0xF8) {
                IsASCII = false;
                // (11111000): 此范围内为 4 字节 UTF-8 字符
                if (i >= size - 3) {
                    break;
                }

                int value1 = byteToUnsignedInt(pBuffer[i + 1]);
                int value2 = byteToUnsignedInt(pBuffer[i + 2]);
                int value3 = byteToUnsignedInt(pBuffer[i + 3]);
                if ((value1 & (0xC0)) != 0x80
                        || (value2 & (0xC0)) != 0x80
                        || (value3 & (0xC0)) != 0x80) {
                    IsUTF8 = false;
                    break;
                }

                i += 3;
            } else {
                IsUTF8 = false;
                IsASCII = false;
                break;
            }
        }
        return IsUTF8;
    }

    public static boolean isUtf8(byte[] bytes) {
        boolean flag = false;
        if (bytes != null && bytes.length > 0) {
            boolean foundStartByte = false;
            int requireByte = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte current = bytes[i];
                //当前字节小于128，标准ASCII码范围
                if ((current & 0x80) == 0x00) {
                    if (foundStartByte) {
                        foundStartByte = false;
                        requireByte = 0;
                    }
                    continue;
                    //当前以0x110开头，标记2字节编码开始，后面需紧跟1个0x10开头字节
                }else if ((current & 0xC0) == 0xC0) {
                    foundStartByte = true;
                    requireByte = 1;
                    //当前以0x1110开头，标记3字节编码开始，后面需紧跟2个0x10开头字节
                }else if ((current & 0xE0) == 0xE0) {
                    foundStartByte = true;
                    requireByte = 2;
                    //当前以0x11110开头，标记4字节编码开始，后面需紧跟3个0x10开头字节
                }else if ((current & 0xF0) == 0xF0) {
                    foundStartByte = true;
                    requireByte = 3;
                    //当前以0x111110开头，标记5字节编码开始，后面需紧跟4个0x10开头字节
                }else if ((current & 0xE8) == 0xE8) {
                    foundStartByte = true;
                    requireByte = 4;
                    //当前以0x1111110开头，标记6字节编码开始，后面需紧跟5个0x10开头字节
                }else if ((current & 0xEC) == 0xEC) {
                    foundStartByte = true;
                    requireByte = 5;
                    //当前以0x10开头，判断是否满足utf8编码规则
                }else if ((current & 0x80) == 0x80) {
                    if (foundStartByte) {
                        requireByte--;
                        //出现多个0x10开头字节，个数满足，发现utf8编码字符，直接返回
                        if (requireByte == 0) {
                            return true;
                        }
                        //虽然经当前以0x10开头，但前一字节不是以0x110|0x1110|0x11110肯定不是utf8编码，直接返回
                    }else {
                        return false;
                    }
                    //发现0x8000~0xC000之间字节，肯定不是utf8编码
                }else {
                    return false;
                }
            }
        }
        return false;
    }

    public void example_utf8_to_gbk() throws Exception{
        // Unicode
        String unicodeString = "张三";
        // 获取 UTF8 编码
        byte[] nameUTF8 = unicodeString.getBytes("utf-8");
        // UTF8 编码转 str
        String str = new String(nameUTF8, "utf-8");
        // 获取 GBK 编码
        byte[] nameGBK = str.getBytes("gbk");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        String unicodeString = "aaa鎿嶄綔鎴愬姛";
        String unicodeString = "操作成功";
        byte[] nameUTF8 = unicodeString.getBytes("utf-8");
        byte[] nameUTF82 = unicodeString.getBytes("gbk");

        byte[] bytes1 = {nameUTF8[0], nameUTF8[1], nameUTF8[2], nameUTF8[3]};
        byte[] bytes2= {nameUTF82[0], nameUTF82[1], nameUTF82[2], nameUTF82[3]};
        System.out.println(isUtf8(nameUTF8));
        System.out.println(isUtf8(nameUTF82));
        System.out.println(new String(nameUTF8));
    }
}
