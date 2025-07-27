package proto.loader.v1;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import proto.loader.v1.annotations.ProtoLoadableV1;
import proto.loader.v1.annotations.ProtoQualifierV1;

public class ProtoLoaderV1 {
  public ProtoLoaderV1() {
  }

  private record ClassPath(String path, File file) {
  }

  @SneakyThrows
  public Map<String, Class<?>> retrieveLoadable() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    final Map<String, Class<?>> founds = new HashMap<>();

    final List<ClassPath> items = new ArrayList<>();

    Arrays.stream(getFromRoot(classLoader))
        .map(it -> new ClassPath("", it))
        .forEach(items::add);

    do {
      final ClassPath classPath = items.remove(items.size() - 1);
      final File file = classPath.file;

      if (file.exists()) {
        final String fileName = file.getName();

        if (file.isDirectory()) {
          items.addAll(findSubItems(file, classPath));
        }

        if (file.isFile()) {
          loadClass(fileName, classPath)
              .filter(ProtoLoaderV1::hasAnnotation)
              .filter(clazz -> !clazz.isInterface())
              .ifPresent(clazz -> {
                final String qualifier = getQualifier(clazz);

                if (founds.containsKey(qualifier)) {
                  throw new IllegalStateException("Duplicated qualifier : " + qualifier);
                }

                founds.put(qualifier, clazz);
              });
        }
      }
    } while (!items.isEmpty());

    return founds;
  }

  private static boolean hasAnnotation(final Class<?> clazz) {
    return clazz.isAnnotationPresent(ProtoLoadableV1.class);
  }

  private static File[] getFromRoot(final ClassLoader classLoader) {
    return Optional
        .ofNullable(classLoader.getResource("."))
        .map(URL::getFile)
        .map(File::new)
        .map(File::listFiles)
        .orElse(new File[] {});
  }

  private static Optional<Class<?>> loadClass(final String fileName, final ClassPath classPath) {
    return Optional.of(fileName)
        .filter(it -> it.endsWith(".class"))
        .map(it -> classPath.path + removeExtension(it))
        .flatMap(it -> {
          try {
            return Optional.of(Class.forName(it));
          } catch (Exception e) {
            System.out.println("failed for class " + it);
            return Optional.empty();
          }
        });
  }

  private static List<ClassPath> findSubItems(final File file, final ClassPath classPath) {
    return Optional.ofNullable(file.listFiles())
        .map(Arrays::asList)
        .orElse(Collections.emptyList())
        .stream()
        .map(it -> new ClassPath(classPath.path + file.getName() + ".", it))
        .toList();
  }

  private static String getQualifier(Class<?> clazz) {
    return Optional.ofNullable(clazz.getAnnotation(ProtoQualifierV1.class))
        .map(ProtoQualifierV1::value)
        .orElse(removeKtSuffix(clazz.getSimpleName()));
  }

  private static String removeKtSuffix(String s) {
    return s.endsWith("Kt") ? s.substring(0, s.length() - 2) : s;
  }

  private static String removeExtension(String fileName) {
    return fileName.endsWith(".class") ? fileName.substring(0, fileName.length() - 6) : fileName;
  }
}
