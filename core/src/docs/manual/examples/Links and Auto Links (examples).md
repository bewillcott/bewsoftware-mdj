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

- [Home](../index.html)
- [Links](../Links.html)
- [@right] [About](../About.html)

@@@
