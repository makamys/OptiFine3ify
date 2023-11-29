package io.github.makamys.optifine3ify;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class OptiFine3ifyTweaker implements ITweaker {

    public OptiFine3ifyTweaker() {
        //OptiFine3ifyInjector.run();
        
    }
    
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        System.out.println("injecting");
        //classLoader.registerTransformer("io.github.makamys.optifine3ify.OptiFine3ifyTransformer");
        try {
            Field classLoaderExceptionsField = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            classLoaderExceptionsField.setAccessible(true);
            Set<String> classLoaderExceptions = (Set<String>)classLoaderExceptionsField.get(Launch.classLoader);
            classLoaderExceptions.remove("optifine");
            
            Launch.classLoader.registerTransformer("io.github.makamys.optifine3ify.OptiFine3ifyTransformer");
        } catch(Exception e) {
            e.printStackTrace();
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
