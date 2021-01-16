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


@@@[#navbar]
- [Home]
- [Code Blocks](#)
    - [Fenced Code Blocks]
- [Lists]
    - [Lists (Enhanced)]
- [@subactive] [Text](#)
    - [Ampersands]
    - [@active] [Headings (Enhanced)](#)
    - [Paragraph (Enhanced)]
    - [Text Embellishments]
- [More...](#)
    - [HR (Enhanced)]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [Tables]
- [@right] [About]
    - [ToDo List]
    - [License]



[About]:About.html
[Ampersands]:Ampersands.html
[Fenced Code Blocks]:Fenced Code Blocks.html
[Headings (Enhanced)]:Headings (Enhanced).html
[Home]:index.html
[HR (Enhanced)]:HR (Enhanced).html
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
