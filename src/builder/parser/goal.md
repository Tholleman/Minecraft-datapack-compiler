## Behaviour
A directory will be copied with all of the files inside of it.
- Empty lines are removed.
- a line spread over multiple lines are combined (continues until terminated with a `;`)
- Meta tags are acted on

## Lines
There are 3 kinds of lines in a file
- An output line which ends with a ;
- A meta tag which describes how to deal with following output line(s)
- A comment which starts with a #

## Acceptable meta tags
### Inline meta tags
Inline meta tags can be used anywhere, including inside meta tags.

#### `<<name>>`
For variables

#### `<<# operator #>>`
To perform a operation integer number operation.

Valid operators are: `+`, `-`, `*`, and `/`

### Line meta tags
#### `${file path}`
Will read a file as if the lines are just written in the current file.
Can be used to set up default variables or go the other way and fill a template.

#### `${repeat #}`
Repeat the next line `#` times.
Has to be an integer.
0 or negative is allowed but the line will be ignored.

#### `${var name value}`
Set a variable to be used later

# Escape character
Block meta tags can be multilined by ending a line with a `\ `