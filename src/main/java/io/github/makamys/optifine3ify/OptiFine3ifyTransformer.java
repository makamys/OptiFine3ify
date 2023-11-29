package io.github.makamys.optifine3ify;

import static org.objectweb.asm.Opcodes.*;
import static io.github.makamys.optifine3ify.Constants.LOGGER;

import java.net.URL;
import java.util.Iterator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

// TODO don't do anything if hash is different from D6's
public class OptiFine3ifyTransformer implements IClassTransformer {
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.equals("optifine.OptiFineClassTransformer")) {
            basicClass = doTransform(basicClass);
        }
        return basicClass;
    }
 
    private static byte[] doTransform(byte[] bytes) {
        LOGGER.info("FMLFastSplashTransformer: Transforming ProgressManager$ProgressBar");
        
        InsnList callHook = new InsnList();
        callHook.add(new VarInsnNode(ALOAD, 0));
        callHook.add(new MethodInsnNode(INVOKESTATIC, "io/github/makamys/optifine3ify/Optifine3ifyTransformer$Hooks", "initOptiFineJar", "(Ljava/lang/Object;)V", false));
        callHook.add(new InsnNode(RETURN));
        
        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytes);
            classReader.accept(classNode, 0);
            for(MethodNode m : classNode.methods) {
                if(m.name.equals("<init>") && m.desc.equals("()V")) {
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
                                mi.owner = "io/github/makamys/optifine3ify/OptiFine3ifyTransformer$Hooks";
                                m.instructions.insertBefore(mi, new InsnNode(POP));
                                m.instructions.insertBefore(mi, new MethodInsnNode(INVOKESTATIC, "io/github/makamys/optifine3ify/OptiFine3ifyTransformer$Hooks", "getOptiFineJarUrl", "()[Ljava/net/URL;", false));
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
        public static URL[] getOptiFineJarUrl() {
            try {
                return new URL[] {Class.forName("optifine.OptiFineClassTransformer").getProtectionDomain().getCodeSource().getLocation()};
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}