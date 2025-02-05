@@@

use: articles2
title: ${document.name} | Links and Auto Links

@@@


## Links and Auto Links (Enhanced)

### What's New

- Link "`[id]`s" are now case-sensitive.  Having them as case-insensitive just left 
    it open to all sorts of problems with no real benefit.
- You can now add a new option: "`!`" - to set the link target to "`_blank`".  This will
    open the link's location page in a new tab, and should make *it* the current
    window.  This leaves your original page still open where you left it, making
    it easy to go back and forth between them, or just getting back to where
    you left off.
- Links can now have a class attribute with one or more classes: `[@<classname>]`

Check out these examples: [][ex][@next]

### Footnote Links

A footnote link is a special handle reference link.  It uses just the one bracket option 
"`[^d%]`" with a caret `^` and a decimal label/id.  The main difference is that the label/id will keep its 
brackets, and it will be superscripted[^1][#fn1].  Although you could create your own version
on a need-by-need basis: `++\\[[1]\\]++`, this will make it much easier[^2].

The reference can be any type you need.  For instance, the one used in the source for this
page looks like:
```
\[1]:Text Embellishments.html "You can find more on Superscripting here"
```
The footnote link was: `[^1]`.  That's it!  By using decimal numbers, just like your
favorite word processor does when you insert a _footnote_.

**NOTE:**  
The caret `^` is used only in the link, not in the reference.  The reference id is just
the number.

As of v1.0.38 you can now add an 'id' attribute to the footnote link: `[^1][#fn1]`  
This will give something like: `<a href="..." id="fn1" ...>`.  The ellipses are in-place
of other text that would be included as appropriate.

Then you could set up your footnote as: `1. [^](#fn1) Some text.`  
Just remember, the 'id' must be unique across the entire page.  So if you refer to the same
footnote multiple times, you can only use the id once.

---

1. [^](#fn1) Some text.  
2. This a just a footnote in history.

[1]:Text Embellishments.html "You can find more on Superscripting here"
[2]:#fn2
[ex]:examples/Links and Auto Links (examples).html "Examples... "


@@@[#navbar]
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
- [@subactive] [More...](#)
    - [HR (Enhanced)]
    - [@active] [Links (Enhanced)](#)
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
