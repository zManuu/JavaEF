# JavaEF

## Usage
1. Create your Entity classes.
   1. All the properties that should be saved and read from the Database have to be public.
   2. Extend the <a href="https://github.com/zManuu/JavaEF/blob/master/src/main/java/javaEF/Entity.java">Entity</a> super class.
   3. The class has to have a _public int id_, in the _getId_ method you have to return that id.
   4. The class has to have a constructor that matches the mapped properties.
   5. Example: <a href="https://github.com/zManuu/JavaEF/blob/master/src/main/java/example/Player.java">Player</a>
2. Create a Database Root Object using the Database Constructor.
3. Create a Table Object using the Table Constructor with the following Paramenters:
   1. As T & clazz use your created Entity class.
   2. As name use the tables name.
4. Create your cached _List<? extends Entity>_ versions of the table.
5. Load the tables content into the cached List by using _Table#getAll()_

## Warnings
- Your Code has to follow the Design Pattern issued in the example dir, other mappings might not work and are not tested.
- Entities aren't compared by equals but by _equalsId_, which will check if the ids match.