package io.github.makamys.optifine3ify;

import java.security.MessageDigest;

import net.minecraft.launchwrapper.Launch;

public class Util {
    public static byte[] hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return hash;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getClassBytesOrNull(String className) {
        try {
            return Launch.classLoader.getClassBytes(className);
        } catch(Exception e) {
            return null;
        }
    }
}
