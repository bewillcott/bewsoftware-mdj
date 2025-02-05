
# Change Log - Markdownj-Core

## What's New

This version is a fork of the [main]! project.  I don't know if it will
be integrated back into that project, or continue as a separate one.

## Version: 0.5.x Snapshot

### Code review
All the code was reviewed and updated to latest Java features.

-   Where useful, anonymous classes were converted to the Lambda format.
-   To help with clarity, I also ran the Netbeans: Source->Organize Members menu
    option.
-   Cleaned up some code that was either unnecessary, or could be redone in a less verbose
    manner.

### Extended markdown capability

#### Fenced Code Blocks

Start line with "~~~" or "\`\`\`". The fencing requires that the block ends with the  
same sequence as it starts with.  Wraps the text between the fences in:

```
<pre>
    <code>
The text
    </code>
</pre>
```

#### Tables

~~~
This would be the table's caption
| Col1 Header | Col2 Header |Col3 Header|Col 4    |[]
| :----       | -:-         | ---:      |  :---:  |[#Id][]
| Left        | Center      |Right      |Justified|[]
| Row 2       | More text   |           |         |
~~~

This would be the table's caption
| Col1 Header | Col2 Header |Col3 Header|Col 4    |[]
| :----       | -:-         | ---:      |  :---:  |[#Id][]
| Left        | Center      |Right      |Justified|[]
| Row 2       | More text   |           |         |

A Table begins and ends with a blank line.

If present, the first row above the table becomes the table's "caption". If the
_caption_ text is bracketed thus: `[This would be the table's caption]`, then it would
be given the same borders as are set for the `<table>` element.  See table below [&darr;](#Id2)

Each line begins and ends with a pipe character '|', with additional such
to separate columns.

Each line can have an optional __[]__ bracketed parameter or two.
If just one is provided, then when empty `[]`, adds a border around that row.
If it contains text, the text is inserted into a 'class=' attribute
for that row.

Special cases:

- The delimiter row (contains the `-` and `:` characters) sets the parameters for the `<table>` tag.
- Data rows:
  - If just the first one is set with a bracketed parameter, then this will be used for all
    data rows.
  - Subsequent rows can be set to either the same class(es) as the
    first row or to different class(es).
  - Any following rows not set, will be configured using the row
    sequencing of the first set of rows, in rotation.


[This would be the table's caption]
| Col1 Header | Col2 Header |Col3 Header|Col 4    |[]
| :----       | -:-         | ---:      |  :---:  |[#Id2][]
| Left        | Center      |Right      |Justified|[]
| Row 2       | More text   |           |         |

### Further Reading

You can find a lot more information in the manual.

[main]:https://github.com/myabc/markdownj
