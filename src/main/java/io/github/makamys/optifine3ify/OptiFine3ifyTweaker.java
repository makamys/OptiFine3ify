package io.github.makamys.optifine3ify;

import static io.github.makamys.optifine3ify.Constants.LOGGER;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class OptiFine3ifyTweaker implements ITweaker {
    
    private static final byte[] D6_OPTIFINE_CLASS_TRANSFORMER_HASH = new byte[] {-127, -48, -36, -115, 104, -9, 112, -126, 102, 105, -22, -124, -114, 29, -71, 77, -62, 53, -78, 45, 94, 18, -74, -73, -102, 72, 25, 14, 30, -77, -107, 76};
    
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if(!checkIfShouldRun()) {
            return;
        }
        
        try {
            // The exception is added right before instantiating OptiFineForgeTweaker in Launch,
            // and the transformer is added in OptiFineForgeTweaker#injectIntoClassLoader.
            // Our TweakOrder is lower than OF's, so we are now between those two events.
            // This is our chance to do our hackery.
            LOGGER.info("Removing optifine class loader exception");
            
            Field classLoaderExceptionsField = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            classLoaderExceptionsField.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>)classLoaderExceptionsField.get(Launch.classLoader);
            classLoaderExceptions.remove("optifine");
            
            Launch.classLoader.registerTransformer("io.github.makamys.optifine3ify.OptiFine3ifyTransformer");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkIfShouldRun() {
        byte[] bytes = Util.getClassBytesOrNull("optifine.OptiFineClassTransformer");
        if(bytes == null) {
            LOGGER.debug("OptiFine is not present, doing nothing.");
            return false;
        } else if(!Arrays.equals(D6_OPTIFINE_CLASS_TRANSFORMER_HASH, Util.hash(bytes))) {
            LOGGER.debug("Skipping transformation of OptiFineClassTransformer because it does not match the D6 version.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
    
}
