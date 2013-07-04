import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

public class Test {

    public static void main(String[] args) throws Exception {

        Hello hello = new Hello();
        hello.isHello = true;

        ClassPool cp = ClassPool.getDefault();
        // CtClass cc = cp.get("Hello");

        CtClass hc = cp.get("Hello");
        CtClass cc = cp.makeClass("Point");
        cc.setSuperclass(hc);
        CtField proxyField = new CtField(hc, "proxy", cc);
        proxyField.setModifiers(Modifier.PUBLIC);
        cc.addField(proxyField);

        CtMethod md = CtNewMethod.make(" public void say(String world){System.out.println(\"ffff\");proxy.say(world);}",
                                       cc);
        cc.addMethod(md);

        Hello newH = (Hello) cc.toClass().newInstance();
        newH.getClass().getDeclaredField("proxy").set(newH, hello);
        newH.say("joe test");
    }
}
