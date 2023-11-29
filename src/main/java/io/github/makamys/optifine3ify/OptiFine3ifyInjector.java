package io.github.makamys.optifine3ify;

import net.minecraft.launchwrapper.Launch;
import static io.github.makamys.optifine3ify.Constants.LOGGER;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.POP;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

// TODO skip if OF is not D6
public class OptiFine3ifyInjector {
    public static void run() {
        LOGGER.debug("Running OptiFine3ifyInjector");
        
        try {
            ClassLoader parent = Launch.classLoader.getParent();
            
            String targetName = "optifine.OptiFineClassTransformer";
            byte[] bytes = transform(Launch.classLoader.getClassBytes(targetName));
            
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
            defineClass.setAccessible(true);
            defineClass.invoke(parent, targetName, bytes, 0, bytes.length, Class.forName("optifine.OptiFineForgeTweaker").getProtectionDomain());
        } catch(Exception e) {
            LOGGER.error("Failed to 3ify OptiFine");
            e.printStackTrace();
        }
    }

    private static byte[] transform(byte[] bytes) {
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            for(MethodNode m : classNode.methods) {
                if(m.name.equals("step") && m.desc.equals("<init>()V")) {
                    Iterator<AbstractInsnNode> it = m.instructions.iterator();
                    while(it.hasNext()) {
                        AbstractInsnNode i = it.next();
                        if(i.getOpcode() == INVOKEVIRTUAL) {
                            MethodInsnNode mi = (MethodInsnNode)i;
                            if(mi.owner.equals("java/lang/Class") && mi.name.equals("getClassLoader") && mi.desc.equals("()Ljava/lang/ClassLoader;")) {
                                m.instructions.insertBefore(mi, new InsnNode(POP));
                                m.instructions.insertBefore(mi, new InsnNode(ACONST_NULL));
                                it.remove();
                            } else if(mi.owner.equals("java/net/URLClassLoader") && mi.name.equals("getURLs") && mi.desc.equals("()[Ljava/net/URL;")) {
                                m.instructions.insertBefore(mi, new InsnNode(POP));
                                m.instructions.insertBefore(mi, new MethodInsnNode(INVOKESTATIC, "io/github/makamys/optifine3ify/Optifine3ifyInjector$Hooks", "getOptiFineJarUrl", "()[Ljava/net/URL;", false));
                                it.remove();
                            }
                        }
                    }
                }
            }
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            return writer.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return bytes;
    }
    
    public static class Hooks {
        public static URL[] getOptiFineJarUrl(Object optiFineTransformer) {
            try {
                return new URL[] {Class.forName("optifine.OptiFineClassTransformer").getProtectionDomain().getCodeSource().getLocation()};
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
