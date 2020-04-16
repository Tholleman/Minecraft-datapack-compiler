# Road map
A list of things that can be done to further improve the compiler.

## Expand the help
Calling the program with `help` gives a very minimal manual of the compiler.
Expanding this to also aid in writing a datapack could be valuable.

## Specialized JSON parser
Importing a datapack will read and parse the json from the pack.mcmeta file.
The library that is used takes up 60kB.
Creating a specialized method of extracting the description could decrease the size of the jar file considerably.

## Move any remaining strings to the constants class
