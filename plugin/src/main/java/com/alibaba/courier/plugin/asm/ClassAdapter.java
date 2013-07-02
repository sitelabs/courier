package com.alibaba.courier.plugin.asm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.alibaba.courier.asm.ClassVisitor;
import com.alibaba.courier.asm.ClassWriter;
import com.alibaba.courier.asm.FieldVisitor;
import com.alibaba.courier.asm.MethodVisitor;
import com.alibaba.courier.asm.Opcodes;
import com.alibaba.courier.asm.Type;

/**
 * <p>
 * ����class A����һ��class B extends A
 * </p>
 * <li>��дA��A��������з�����eg. public void xx() {super.xx();} <li>copy A�ඨ�����������
 */
public class ClassAdapter extends ClassVisitor implements Opcodes {

    public static final String INIT = "<init>";
    private ClassWriter        classWriter;
    private String             originalClassName;
    private String             enhancedClassName;
    private Class<?>           originalClass;

    public ClassAdapter(String enhancedClassName, Class<?> targetClass, ClassWriter writer){
        super(Opcodes.ASM4, writer);
        this.classWriter = writer;
        this.originalClassName = targetClass.getName();
        this.enhancedClassName = enhancedClassName;
        this.originalClass = targetClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        cv.visit(version, Opcodes.ACC_PUBLIC, toAsmCls(enhancedClassName), signature, name, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // ������������
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // ɾ�����з���
        return null;
    }

    /**
     * �������е�.�滻Ϊ/
     * 
     * @param className
     * @return
     */
    private static String toAsmCls(String className) {
        return className.replace('.', '/');
    }

    /**
     * <p>
     * ǰ�÷���
     * </p>
     * 
     * @see TxHandler
     * @param mWriter
     */
    private void doBefore(MethodVisitor mWriter, String methodInfo) {

        // mWriter.visitFieldInsn(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
        // ���þ�̬����
        // mWriter.visitLdcInsn(methodInfo);
        // mWriter.visitMethodInsn(INVOKESTATIC, toAsmCls(aopClassName), "before", "(Ljava/lang/String;)V");
        // mWriter.visitLocalVariable("this", "L" + toAsmCls(originalClassName) + ";", null, null, null, 0);
        mWriter.visitVarInsn(ALOAD, 0);
        mWriter.visitMethodInsn(INVOKESTATIC, toAsmCls(PluginChecker.class.getName()), "check", "(Ljava/lang/Object;)V");
    }

    /**
     * <p>
     * ���÷���
     * </p>
     * 
     * @see TxHandler
     * @param mWriter
     */
    private void doAfter(MethodVisitor mWriter, String methodInfo) {

    }

    @Override
    public void visitEnd() {
        // ���originalClass������˽�г�Ա��������ôֱ����visitMethod�и���originalClass��<init>�ᱨ��
        // ALOAD 0
        // INVOKESPECIAL cc/RoleService.<init>()V
        // RETURN
        // // ����originalClassName��<init>����������class����ʵ����
        MethodVisitor mvInit = classWriter.visitMethod(ACC_PUBLIC, INIT, "()V", null, null);
        mvInit.visitVarInsn(ALOAD, 0);
        mvInit.visitMethodInsn(INVOKESPECIAL, toAsmCls(originalClassName), INIT, "()V");
        mvInit.visitInsn(RETURN);
        mvInit.visitMaxs(0, 0);
        mvInit.visitEnd();

        // addCheckMethod();

        // ��ȡ���з���������д(main���� �� Object�ķ�������)
        Method[] methods = originalClass.getMethods();
        for (Method m : methods) {
            // �ж��Ƿ���Ҫ��д
            if (!needOverride(m)) {
                continue;
            }
            Type mt = Type.getType(m);

            // build methodinfo begin :className.methodName|parametertypes
            String methodInfo = buildMethodInfo(m);

            // �����Ǳ��ĸ��ඨ���
            String declaringCls = toAsmCls(m.getDeclaringClass().getName());

            // ���� description
            MethodVisitor mWriter = classWriter.visitMethod(ACC_PUBLIC, m.getName(), mt.toString(), null, null);

            // insert code here (before)
            doBefore(mWriter, methodInfo);

            int i = 0;
            // ������Ǿ�̬���� load this����
            if (!Modifier.isStatic(m.getModifiers())) {
                mWriter.visitVarInsn(ALOAD, i++);
            }
            StringBuilder sb = new StringBuilder(m.getName());
            // load �����������в���
            for (Class<?> tCls : m.getParameterTypes()) {
                Type t = Type.getType(tCls);
                sb.append(loadCode(t)).append(",");
                mWriter.visitVarInsn(loadCode(t), i++);
                if (t.getSort() == Type.LONG || t.getSort() == Type.DOUBLE) {
                    i++;
                }
            }

            // super.xxx();
            mWriter.visitMethodInsn(INVOKESPECIAL, declaringCls, m.getName(), mt.toString());

            // ������ֵ����
            Type rt = Type.getReturnType(m);
            // û�з���ֵ
            if (rt.toString().equals("V")) {
                doAfter(mWriter, methodInfo);
                mWriter.visitInsn(RETURN);
            }
            // ��return xxx() ת��� �� Object o = xxx(); return o;
            else {
                int storeCode = storeCode(rt);
                int loadCode = loadCode(rt);
                int returnCode = rtCode(rt);

                mWriter.visitVarInsn(storeCode, i);
                doAfter(mWriter, methodInfo);
                mWriter.visitVarInsn(loadCode, i);
                mWriter.visitInsn(returnCode);
            }

            // ���������Զ����㣬������Ҫ����һ�£���Ȼ�ᱨ��
            mWriter.visitMaxs(i, ++i);
            mWriter.visitEnd();
        }
        cv.visitEnd();
    }

    /**
     * <p>
     * object�౾��ķ���������д
     * </p>
     * <p>
     * "main" ����������д
     * </p>
     * 
     * @param m
     * @return
     */
    private boolean needOverride(Method m) {
        // object�౾��ķ���������д
        if (m.getDeclaringClass().getName().equals(Object.class.getName())) {
            return false;
        }
        // "main" ����������д
        if (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers())
            && m.getReturnType().getName().equals("void") && m.getName().equals("main")) {
            return false;
        }

        return true;
    }

    /**
     * <p>
     * build methodinfo :className.methodName|parametertypes
     * </p>
     * 
     * @param m
     * @return
     */
    private String buildMethodInfo(Method m) {
        StringBuilder sb = new StringBuilder(originalClassName);
        sb.append(".").append(m.getName());
        sb.append("|");

        Class<?>[] paramTypes = m.getParameterTypes();
        for (Class<?> t : paramTypes) {
            sb.append(t.getName()).append(",");
        }
        if (paramTypes.length > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * <p>
     * get StoreCode(Opcodes#xStore)
     * </p>
     * 
     * @param type
     * @return
     */
    public static int storeCode(Type type) {

        int sort = type.getSort();
        switch (sort) {
            case Type.ARRAY:
                sort = ASTORE;
                break;
            case Type.BOOLEAN:
                sort = ISTORE;
                break;
            case Type.BYTE:
                sort = ISTORE;
                break;
            case Type.CHAR:
                sort = ISTORE;
                break;
            case Type.DOUBLE:
                sort = DSTORE;
                break;
            case Type.FLOAT:
                sort = FSTORE;
                break;
            case Type.INT:
                sort = ISTORE;
                break;
            case Type.LONG:
                sort = LSTORE;
                break;
            case Type.OBJECT:
                sort = ASTORE;
                break;
            case Type.SHORT:
                sort = ISTORE;
                break;
            default:
                break;
        }
        return sort;
    }

    /**
     * <p>
     * get StoreCode(Opcodes#xLOAD)
     * </p>
     * 
     * @param type
     * @return
     */
    public static int loadCode(Type type) {
        int sort = type.getSort();
        switch (sort) {
            case Type.ARRAY:
                sort = ALOAD;
                break;
            case Type.BOOLEAN:
                sort = ILOAD;
                break;
            case Type.BYTE:
                sort = ILOAD;
                break;
            case Type.CHAR:
                sort = ILOAD;
                break;
            case Type.DOUBLE:
                sort = DLOAD;
                break;
            case Type.FLOAT:
                sort = FLOAD;
                break;
            case Type.INT:
                sort = ILOAD;
                break;
            case Type.LONG:
                sort = LLOAD;
                break;
            case Type.OBJECT:
                sort = ALOAD;
                break;
            case Type.SHORT:
                sort = ILOAD;
                break;
            default:
                break;
        }
        return sort;
    }

    /**
     * <p>
     * get StoreCode(Opcodes#xRETURN)
     * </p>
     * 
     * @param type
     * @return
     */
    public static int rtCode(Type type) {
        int sort = type.getSort();
        switch (sort) {
            case Type.ARRAY:
                sort = ARETURN;
                break;
            case Type.BOOLEAN:
                sort = IRETURN;
                break;
            case Type.BYTE:
                sort = IRETURN;
                break;
            case Type.CHAR:
                sort = IRETURN;
                break;
            case Type.DOUBLE:
                sort = DRETURN;
                break;
            case Type.FLOAT:
                sort = FRETURN;
                break;
            case Type.INT:
                sort = IRETURN;
                break;
            case Type.LONG:
                sort = LRETURN;
                break;
            case Type.OBJECT:
                sort = ARETURN;
                break;
            case Type.SHORT:
                sort = IRETURN;
                break;
            default:
                break;
        }
        return sort;
    }

}
