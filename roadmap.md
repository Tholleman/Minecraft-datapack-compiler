# Road map
A list of things that can be done to further improve the compiler

## If statements in the rfc

    [b] "the variable b exists"
    [<<a>> = 17] "a = 17"
    [b and [a = 17]] "b exists and a = 17"
    [not a] "a does not exist"
    [not [a = 7]] "a != 7"
    [else b] "a = 7 and b exists"
    [else [c > 6]] "a = 7, b does not exist, c > 6"
    [else] "a = 7, b does not exist, c <= 6"
    [true]
    [false]
    
    [<<clevel>> >= 2] "the new alternative to \clevel 2"
    
    number to boolean operators
    >
    <
    >=, =>
    <=, <=
    
    variable operators
    <<name here>> to check if a variable exists
    =

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

## Move any remaining strings to the constants class

## Custom blacklist and parsing whitelist
The blacklist for files and whitelist for parsing files is currently hard coded. Allowing the user to override this list would give them more freedom to code how they want.