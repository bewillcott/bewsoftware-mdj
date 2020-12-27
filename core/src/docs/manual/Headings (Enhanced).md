@@@

use: articles2
title: ${document.name} | Headings (Enhanced)

@@@


## Headings (Enhanced)

Headings now have the following additional options:

- You can set an "`id=""`" for each as needed.
- You can add a link to the end of the heading text.

**Note:** This is not available with the dashed "`----`" and equals "`====`"
versions of headings.

~~~
#[#main] The main heading
~~~
Produces this:
~~~
<h1 id="main">The main heading</h1>
~~~
Results in this:
#[#main] The main heading

~~~
## Sub heading ## [&uarr;](#main)
~~~
Produces this:
~~~
<h2>Sub heading <a href="#main">&uarr;</a></h2>
~~~
Results in this:
## Sub heading ## [&uarr;](#main)


@@@[navbar]
- [Home]
- [@dropdown] [Code Blocks](#)
[@dropdown-content]
    - [Fenced Code Blocks]
- [@dropdown] [Lists]
[@dropdown-content]
    - [Lists (Enhanced)]
- [@dropdown subactive] [Text](#)
[@dropdown-content]
    - [Ampersands]
    - [@active] [Headings (Enhanced)](#)
    - [Paragraph (Enhanced)]
    - [Text Embellishments]
- [@dropdown] [More...](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [Tables]
- [@right dropdown] [About]
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
