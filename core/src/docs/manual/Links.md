@@@

use: articles
title: MDj Core Library | Links and Auto Links

@@@

[Home]

---

## Links and Auto Links

### What's New

- Link "`[id]`s" are now case-sensitive.  Having them as case-insensitive just left 
    it open to all sorts of problems with no real benefit.
- You can now add a new option: "`!`" - to set the link target to "`_blank`".  This will
    open the link's location page in a new tab, and should make *it* the current
    window.  This leaves your original page still open where you left it, making
    it easy to go back and forth between them, or just getting back to where
    you left off.

Check out these examples: [&rarr;][ex]

### Footnote Links

A footnote link is a special handle reference link.  It uses just the one bracket option 
"`[d%]`" with a decimal label/id.  The main difference is that the label/id will keep its 
brackets, and it will be superscripted[1].  Although you could create your own version
on a need-by-need basis: `++\\[[1]\\]++`, this will make it much easier[2].

The reference can be any type you need.  For instance, the one used in the source for this
page looks like:
```
\[1]:Text Embellishments.html "You can find more on Superscripting here"
```
The footnote link was: `[1]`.  That's it!  By using decimal numbers, just like your
favorite word processor does when you insert a _footnote_.

---

2.[#fn1] This a just a footnote in history.

[1]:Text Embellishments.html "You can find more on Superscripting here"
[2]:#fn1
[ex]:examples/Links and Auto Links (examples).html
[Home]:index.html
