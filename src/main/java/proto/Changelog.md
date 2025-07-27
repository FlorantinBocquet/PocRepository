# v1 - Find classes

- Capable of finding classes annotated with Loadable annotation
- Refer to classes as their annotated Qualifier annotation if they have any, else to their class name

# v2 - Find methods

- Capable of the same as v1
- Capable of finding methods annotated with Generable annotation contained in class annotated with Generator annotation
- Refer to methods as their annotated Qualifier annotation if they have any, else to their method name
- Make the returned map of found elements contains Element(qualifier, annotation, clazz, method?)
- Generify the selection of elements based on the annotation using a map of Annotation to Function
- Extract the browsing of the files to a separate class

# v3 - Load classes and methods by themselves

- Being capable of the same as v2
- Being capable of loading objects instances from their given retrieved classes and methods from their public empty
  constructors

# v4 - Load classes and methods with arguments

- Being capable of the same as v3
- Being capable of loading objects instances from their given retrieved classes and methods from their constructors with
  other classes as arguments

# v5 - Load classes and methods with configuration

- Being capable of the same as v4
- Being capable of loading basic type values (int, String, double, ...) from a configuration file
- Being able to associate a part of a configuration file to a class
- Supported configuration file format : JSON

# Stable - Creation of a stable version

- Create a stable version of the poc based on the v5

# v6 - TODO

- Being capable of the same as v5

# Improvements

- Can link constructor / method arguments to qualifiers annotation
- Add support for other configuration file formats (YAML, properties, ...)
- Ability to read arrays, maps and other iterables from configuration file (properties)
- Being able to detect circular generation and throw an exception // Create a post autowiring ?
- Transform the 'create' function into an observable / observer pattern, not loaded objects watching for their params,
  and a final check to see if all objects are loaded