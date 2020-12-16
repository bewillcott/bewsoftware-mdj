@@@

use: articles2
title: MDj Core Library | Links and Auto Links (Examples)

@@@


## Links and Auto Links

First some basic links that will cause the new page to be opened in the current tab.

This:
~~~
An [example][link]

An [example](http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>

\\[link]: http://www.example.com/link.html  "This is a title"
~~~

Becomes this:

An [example][link]

An [example](http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>

---

Now tha same links, but these will open in a new tab.

This:
~~~
An [example][link]!

An [example]!(http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>!

And another [example]!

\\[example]: http://www.example.com/example.html  "This is a title"
\\[link]: http://www.example.com/link.html  "This is a title"
~~~

Becomes this:

An [example][link]!

An [example]!(http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>!

And another [example]!

[example]: http://www.example.com/example.html  "This is a title"
[link]: http://www.example.com/link.html  "This is a title"



@@@[navbar]
- [![Back]][Links (Enhanced)][@icon]
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
- [@dropdown] [More...][@dropbtn](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [Tables]
- [@right dropdown] [About][@dropbtn]
[@dropdown-content]
    - [ToDo List]
    - [License]


[Back]:etc/32px-Go-previous.png
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
