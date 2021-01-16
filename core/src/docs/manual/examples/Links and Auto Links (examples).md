@@@

use: articles2
title: ${document.name} | Links and Auto Links (Examples)

@@@


## Links and Auto Links

### Basic Links

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

### Open in new Tab

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

---

### Links with _class_

Now with a class attribute:

This:
~~~
An [example][link]![@externalLink]

An [example]![@externalLink](http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>![@externalLink]

And another [example]![@externalLink]

\\[example]: http://www.example.com/example.html  "This is a title"
\\[link]: http://www.example.com/link.html  "This is a title"
~~~

Becomes this:

An [example][link]![@externalLink]

An [example]![@externalLink](http://www.example.com/some_page.html "This is a title")

Go to <http://www.example.com/some_page.html>![@externalLink]

And another [example]![@externalLink]

[example]: http://www.example.com/example.html  "This is a title"
[link]: http://www.example.com/link.html  "This is a title"

Note the little graphic at the end of each link.



@@@[navbar]
- [][Links (Enhanced)][@previous]
- [Home]
- [Code Blocks](#)
    - [Fenced Code Blocks]
- [Lists]
    - [Lists (Enhanced)]
- [Text](#)
    - [Ampersands]
    - [Headings (Enhanced)]
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
