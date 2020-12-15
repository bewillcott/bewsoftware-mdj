@@@

use: articles2
title: MDj Core Library | Headings

@@@


## Headings

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

- [Home](index.html)
- [@right] [About](About.html)

@@@
