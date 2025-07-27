package proto;

import java.io.File;
import java.util.Set;
import lombok.SneakyThrows;
import proto.loader.v5.items.ProtoInstanceLoaderV5;
import proto.loader.v5.items.configuration.ConfigurationFileLoaderV5;
import proto.loader.v5.items.configuration.JsonConfigurationFileLoaderV5;
import proto.loader.v5.tests.AnotherClassWithConstructorV5;
import proto.loader.v5.tests.ClassLoaderWithValuesV5;
import proto.loader.v5.tests.ClassWithConstructorV5;
import proto.loader.v5.tests.ProtoConfigClassV5;

public class Main {
  @SneakyThrows
  public static void main(String[] args) {
    final File configFile = new File("src/main/java/proto/loader/v5/files/test1.json");
    final ConfigurationFileLoaderV5 loader = new JsonConfigurationFileLoaderV5(configFile);

    final ProtoInstanceLoaderV5 creator = new ProtoInstanceLoaderV5(loader);
    creator.create();

    final Set<String> objects = creator.getQualifiers();

    System.out.println("Qualifiers: " + objects);

    objects.forEach(qualifier -> {
      final Object object = creator.get(qualifier);
      System.out.println("Item : Qualifiers [" + qualifier + "] for { " + object + " }");
    });

    System.out.println("quantity: " + objects.size());

    System.out.println();

    System.out.println("Strings");
    creator.getMatching(String.class).forEach((pair) -> {
      System.out.println("Item : Qualifiers [" + pair.first() + "] for { " + pair.second() + " }");

      final String casted = (String) pair.second();

      System.out.println("Casted: " + casted.toUpperCase());
    });

    System.out.println();

    final ClassWithConstructorV5 myClass = creator.get("class-with-constructor");
    System.out.println("Parameters of ClassWithConstructorV5");
    System.out.println(myClass.getName());


    System.out.println();

    final AnotherClassWithConstructorV5 anotherClass = creator.get("AnotherClassWithConstructorV5");
    System.out.println("Parameters of AnotherClassWithConstructorV5");
    System.out.println("parameter one : " + anotherClass.getOne().getClass());
    System.out.println("parameter two : " + anotherClass.getTwo().getClass());
    System.out.println("parameter three : " + anotherClass.getThree().getClass());
    System.out.println("in parameter three -> " + anotherClass.getThree().getName());

    System.out.println();
    final ClassLoaderWithValuesV5 classLoaderWithValuesV5 = creator.get("loadedByValueQualifier");
    System.out.println(classLoaderWithValuesV5);

    System.out.println();
    final ProtoConfigClassV5 protoConfigClassV5 = creator.get("Config_class");
    System.out.println(protoConfigClassV5);
  }
}
