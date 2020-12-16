@@@

use: articles2
title: MDj Core Library | Tables
stylesheet: darkstyle_no-table.css

@@@


[#contents]
- Tables
    - [Enhanced](#tableEnh)
    - [What's next you might ask?](#next)
    - [Now a look at classes](#now)

## Tables

A Table begins and ends with a blank line.
If present, the line of text above the table becomes the table's "caption".
Each row in the table begins and ends with a pipe character "`|`", with additional
"`|`" characters added to separate each column within each row.

Each row can have optional "`[]`" bracketed parameters. When an empty "`[]`" is added,
that row is given a border around it using default settings. If the brackets contain text, that text is 
inserted into a "`class=""`" attribute for that row, unless the first character is a "`#`".
In that case, the text minus the "`#`" is added as an "`id=""`" attribute for that row.

Special cases:

- The delimiter row sets the parameters for the "`<table>`" tag.
- Data rows:
  - If just the first one is set, then its attributes will be used for subsequent
    data rows.
  - Subsequent rows can be set to either the same class(es) as the
    first row or to different class(es).
  - Any following rows not set, will be configured using the row
    sequencing of the first set of rows, in rotation.

~~~
This would be the table's caption.
| Col1 Header | Col2 Header |Col3 Header|Col 4|[]
| :---- | -:- | ---:|  :---: |[#Id][]
| Left | Center |Right|Justified|[]
| Row 2 | More text || |[]
~~~

This would be the table's caption.
| Col1 Header | Col2 Header |Col3 Header|Col 4|[]
| :---- | -:- | ---:|  :---: |[#Id][]
| Left | Center |Right|Justified|[]
| Row 2 | More text || |[]

###[#tableEnh] Enhanced ### [&uarr;](#top)

Now you can add a caption. But wait, there's more.  It can have  the same type
of borders as your "`<table>`".

The single line on top of the table becomes the caption. However, there must
be a blank line before it.  Use any formatting on the text, that Markdownj offers.

To add bordering, enclose the text within square brackets: "`[ ]`"

Next, you can add bordering to any or all of the tags: "`<table>`", "`<th>`" or "`<td>`".
Do this by using the square brackets at the end of each line.  An empty bracket "`[]`"
means use the default settings: "`border width: 1px, padding: 5px`". Alternatively,
you can set one or both of them using that order [bw, p]: "`[3 7]`" or "`[3,7]`" or "`[3]`".

There is also another nice helpful feature, rolling format.  Set one or more of 
your "`<td>`" lines to include borders, then leave the rest to MDj.  It will 
use those settings in a round-robin way to format the following lines.  For example,
each of the tables below are using this way to format bracketed lines.

Now, if for some reason you wish to use a different format  for say the last line,
or you wish to turn off this auto formatting, then simply add a bracket option
to your line.  To reset/clear out the formatting list, use "`[0]`".  This will turn
off the formatting for that and subsequent unbracketed lines.  Notice the last line of
each table below has its own bracketed options.  Check out the results in your
browser.
```
[__This is junk__]
| abc | def |[3 7]
| -:-- | --- |[4]
| bar |[]
| oh    | _shoot_!|
| just for | fun |
| bar | baz | boo |[0]
||Boo hoo|
|My My |What a day|[2]
```

[__This is junk__]
| abc | def |[3 7]
| -:-- | --- |[4]
| bar |[]
| oh    | _shoot_!|
| just for | fun |
| bar | baz | boo |[0]
||Boo hoo|
|My My |What a day|[2]

~~~
___Bulk Purchases___
| Product | Qty | $Price | $Total |[3 7]
| :------ | -:- | -----: | -----: |[4]
| Coffee  | 10  | 10.50  | 105.00 |[]
| Tea     | 10  |  8.00  |  80.00 |
| Milk    | 5   | 10.00  |  50.00 |
| Totals  | 25  |        | 235.00 |[2,6]
~~~

___Bulk Purchases___
| Product | Qty | $Price | $Total |[3 7]
| :------ | -:- | -----: | -----: |[4]
| Coffee  | 10  | 10.50  | 105.00 |[]
| Tea     | 10  |  8.00  |  80.00 |
| Milk    | 5   | 10.00  |  50.00 |
| Totals  | 25  |        | 235.00 |[2,6]

###[#next] What's next you might ask? ###[*](#gt)  [&uarr;](#top)

Well how about adding an _id_ to the table, or even a row?  By adding the _id_
to the delimiter row, it gets added to the "`<table>`" tag.
```
This is a great table
| Left Foot | Right Foot |[2]
| :--- | ---: |[#gt][3]
| Up | down |[]
| Up | Up |
| Down | Up |
| Down | Down |
| Finished | Fini |[2]
```

This is a great table
| Left Foot | Right Foot |[2]
| :--- | ---: |[#gt][3]
| Up | down |[]
| Up | Up |
| Down | Up |
| Down | Down |
| Finished | Fini |[2]


###[#now] Now a look at classes ### [*](#great) [&uarr;](#top)

In true CSS format, you can use classes to set the styling of your table and it's
rows.
```
This is an even greater table
| Left Foot | Right Foot |[@tbHeader]
| :--- | ---: |[#great][@tbSetting]
| Up | down |[@tbRow]
| Up | Up |
| Down | Up |
| Down | Down |
| Finished | Fini |[#end][@tbTotal]
```

This is an even greater table
| Left Foot | Right Foot |[@tbHeader]
| :--- | ---: |[#great][@tbSetting]
| Up | down |[@tbRow]
| Up | Up |
| Down | Up |
| Down | Down |
| Finished | Fini |[#end][@tbTotal]

_Check out the source of this page in your browser._


@@@[navbar]
- [Home]
- [@dropdown] [Code Blocks][@dropbtn](#)
[@dropdown-content]
    - [Fenced Code Blocks]
- [@dropdown] [Lists][@dropbtn]
[@dropdown-content]
    - [Lists (Enhanced)]
- [@dropdown] [Text][@dropbtn](#)
[@dropdown-content]
    - [Ampersands]
    - [Headings (Enhanced)]
    - [Paragraph (Enhanced)]
    - [Text Embellishments]
- [@dropdown subactive] [More...][@dropbtn](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [@active] [Tables](#)
- [@right dropdown] [About][@dropbtn]
[@dropdown-content]
    - [ToDo List]
    - [License]


[About]:About.html
[Ampersands]:Ampersands.html
[Fenced Code Blocks]:Fenced Code Blocks.html
[Headings (Enhanced)]:Headings (Enhanced).html
[Home]:index.html
[License]:LICENSE.html
[Links (Enhanced)]:Links (Enhanced).html
[Lists]:Lists.html
[Lists (Enhanced)]:Lists (Enhanced).html
[Navigation Bar]:NavBar.html
[Paragraph (Enhanced)]:Paragraph (Enhanced).html
[Tables]:Tables.html
[Text Embellishments]:Text Embellishments.html
[ToDo List]:ToDo.html
@@@
