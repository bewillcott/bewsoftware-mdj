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



@@@[navbar]
- [Home]
- [@dropdown] [Code Blocks](#)
[@dropdown-content]
    - [Fenced Code Blocks]
- [@dropdown active] [Lists](#)
[@dropdown-content]
    - [Lists (Enhanced)]
- [@dropdown] [Text](#)
[@dropdown-content]
    - [Ampersands]
    - [Headings (Enhanced)]
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
