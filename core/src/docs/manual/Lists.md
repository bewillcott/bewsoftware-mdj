@@@

use: articles2
title: MDj Core Library | Lists

@@@


## Lists

I have added bracketed options `[]` to both ordered and unordered lists.

As of this writing, only the `id=""` option `[#myid]` has been configured.

To add an `id` to a list, place the bracketed option just before the first list
item:

~~~
[#myList]
+ This is my list
+ Nobody is allowed to touch
+ However you can read it.
~~~
Produces this code:
~~~
<ul id="myList">
<li>This is my list</li>
<li>Nobody is allowed to touch</li>
<li>However you can read it.</li>
</ul>
~~~
And this list:

[#myList]
+ This is my list
+ Nobody is allowed to touch
+ However you can read it.



[Home]:index.html

@@@[navbar]

- [Home](index.html)
- [@right] [About](About.html)

@@@
