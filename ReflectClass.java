import java.lang.reflect.*;
public class ReflectClass {
  public static void main(String[] args) throws Exception {
    Class<?> c = Class.forName(args[0]);
    System.out.println("CLASS " + c.getName());
    for (Method m : c.getDeclaredMethods()) System.out.println(m.toGenericString());
    for (Field f : c.getDeclaredFields()) System.out.println("FIELD " + f.toGenericString());
  }
}
