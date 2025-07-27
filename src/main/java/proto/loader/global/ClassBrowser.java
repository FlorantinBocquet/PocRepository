package proto.loader.global;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ClassBrowser {
  private final String rootFolder;

  public ClassBrowser(String rootFolder) {
    this.rootFolder = rootFolder;
  }

  private final List<Class<?>> classes = new ArrayList<>();

  private record ClassPath(String path, File file) {
  }

  public void load() {
    if (!classes.isEmpty()) {
      return;
    }

    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    final List<ClassPath> items = new ArrayList<>();

    Optional
        .ofNullable(classLoader.getResource(rootFolder))
        .map(URL::getFile)
        .map(File::new)
        .map(File::listFiles)
        .ifPresent(files ->
            Arrays.stream(files)
                .map(it -> new ClassPath("", it))
                .forEach(items::add)
        );

    do {
      final ClassPath classPath = items.remove(items.size() - 1);
      final File file = classPath.file();

      if (file.exists()) {
        if (file.isDirectory()) {
          final List<ClassPath> subItems = Optional.ofNullable(file.listFiles())
              .map(Arrays::asList)
              .orElse(Collections.emptyList())
              .stream()
              .map(it -> new ClassPath(classPath.path() + file.getName() + ".", it))
              .toList();

          items.addAll(subItems);
        }

        if (file.isFile()) {
          loadClass(classPath.file().getName(), classPath).ifPresent(classes::add);
        }
      }

    } while (!items.isEmpty());
  }

  public void browseClasses(final Consumer<Class<?>> operation) {
    classes.forEach(operation);
  }

  private Optional<Class<?>> loadClass(final String fileName, final ClassPath classPath) {
    return Optional.of(fileName)
        .filter(it -> it.endsWith(".class"))
        .map(it -> classPath.path() + it.substring(0, it.length() - 6))
        .map(it -> {
          try {
            return Class.forName(it);
          } catch (Exception e) {
            System.out.println("failed for class " + it);
            return null;
          }
        });
  }
}
