# Road map
A list of things that can be done to further improve the compiler

## If statements in the rfc

## Passing arguments to a file or inline meta

## Relative file paths
`..` doesn't currently work.

## Expand the help
Calling the program with `help` gives a very minimal manual of the compiler.
Expanding this to also aid in writing a datapack could be valuable

## Specialized JSON parser
Importing a datapack will read and parse the json from the pack.mcmeta file.
The library that is used takes up 60kB.
Creating a specialized method of extracting the description could decrease the size of the jar file considerably.

## Error when giving the incorrect amount of arguments
Currently all options only require 1 argument.
Adding a simple check for this could temporarily help the user.
Adding a check with every option could allow for expansion later.

## Move any remaining strings to the constants class

## Upgrade between compiler versions
Adding a compiler version in the properties file would allow for upgrades between versions.

## Custom blacklist and parsing whitelist
The blacklist for files and whitelist for parsing files is currently hard coded. Allowing the user to override this list would give them more freedom to code how they want.