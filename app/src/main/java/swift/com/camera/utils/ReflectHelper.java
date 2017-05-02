package swift.com.camera.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by junnikokuki on 2017/4/28.
 */

public class ReflectHelper {
    public static void setProperty(Object owner, String fieldName, Object value) throws Exception {
        Class ownerClass = owner.getClass();
        Field field = ownerClass.getField(fieldName);
        field.set(owner, value);
    }

    public static void setStaticProperty(String className, String fieldName, Object value) throws Exception {
        Class ownerClass = Class.forName(className);
        Field field = ownerClass.getField(fieldName);
        field.set(ownerClass, value);
    }

    public static Object getProperty(Object owner, String fieldName) throws Exception {
        Class ownerClass = owner.getClass();
        Field field = ownerClass.getField(fieldName);
        return field.get(owner);
    }

    public static Object getStaticProperty(String className, String fieldName) throws Exception {
        Class ownerClass = Class.forName(className);
        Field field = ownerClass.getField(fieldName);
        return field.get(ownerClass);
    }

    public static Object invokeMethod(Object owner, String methodName, Object[] args) throws Exception {
        Class ownerClass = owner.getClass();
        Class[] argsClass = null;
        if (args != null && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        if (method != null) {
            return method.invoke(owner, args);
        } else {
            return null;
        }
    }

    public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
        Class ownerClass = Class.forName(className);
        Class[] argsClass = null;
        if (args != null && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        if (method != null) {
            return method.invoke(null, args);
        } else {
            return null;
        }
    }

    public static Object newInstance(String className, Object[] args) throws Exception {
        Class newOneClass = Class.forName(className);
        Class[] argsClass = null;
        if (args != null && args.length > 0) {
            argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        Constructor cons = newOneClass.getConstructor(argsClass);
        if (cons != null) {
            return cons.newInstance(args);
        } else {
            return null;
        }
    }
}
