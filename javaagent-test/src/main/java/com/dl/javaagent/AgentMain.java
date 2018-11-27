package com.dl.javaagent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AgentMain {
	public static void premain(String agentArgs,Instrumentation inst){
		instrument(agentArgs, inst);
	}
	
	private static void instrument(String agentArgs, Instrumentation inst) {
        System.out.println(agentArgs);
        inst.addTransformer(new ClassFileTransformer() {
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if("com/dl/agent/AgentTest".equals(className)){
                	return transformClass(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
                }
                return null;
            }
        });
	}
	
    private static byte[] transformClass(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        System.out.println("className: " + className);
        ClassReader classReader = new ClassReader(classfileBuffer);
        ClassWriter classWriter = new ClassWriter(classReader,0);
        classReader.accept(new MyClassClassVisitor(classWriter), 0);
        byte[] bytes = classWriter.toByteArray();
        return bytes;
    }
    
    /**
     * 注意：测试时，classVistor在java8环境不好用
     *
     */
    public static class MyClassClassVisitor extends ClassVisitor {
        public MyClassClassVisitor(ClassWriter classWriter) {
            super(Opcodes.ASM4, classWriter);
        }
        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.println(name + " extends " + superName + " {");
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println(" " + name + desc);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        @Override
        public void visitEnd() {
            System.out.println("}");
            super.visitEnd();
        }
    }
}
