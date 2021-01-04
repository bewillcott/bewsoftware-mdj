@@@

use: articles2
title: ${document.name} | Navigation Bar

@@@

## Navigation Bar (NavBar)
The NavBar is an extended version of the normal list feature.  As of version 0.6.7,
Lists can now have a &lt; class=""&gt; attribute as well as an &lt; id=""&gt; attribute.

The syntax is:

- \[#id]\[@classname(s)] (As the first line of the list) Class for the whole list.
- \[x]\[@classname(s)] Class for a checkbox item.
- \[@classname(s)] Class for an item.
- \[@classname(s)] \[mypage]\[mp] Class for an item with a link.

The use of this feature in creating a navigation bar, requires the CSS to be 
properly setup with the classes.  If you use the default "navstyle.css" stylesheet,
as is setup in the templates: "article\_2" and "article\_3", all you need to do to change
the look is to modify the stylesheet settings.

As you can imagine, the NavBar is only _one_ way of utilizing this feature.  Have fun :-)


```
\@@@[navbar]
- [Home]
- [@active] [Navigation Bar](#)
- [@dropdown] [Lists]
[@dropdown-content]
    - [Link 1](#)
    - [Link 2](#)
    - [Link 3](#)
- [@right] [About]

\[Home]:index.html
\[Lists]:Lists.html
\[Navigation Bar]:NavBar.html
\[About]:About.html
@@@
```


@@@[navbar]
- [Home]
- [@dropdown] [Code Blocks](#)
[@dropdown-content]
    - [Fenced Code Blocks]
- [@dropdown] [Lists]
[@dropdown-content]
    - [Lists (Enhanced)]
- [@dropdown] [Text](#)
[@dropdown-content]
    - [Ampersands]
    - [Headings (Enhanced)]
    - [Paragraph (Enhanced)]
    - [Text Embellishments]
- [@dropdown subactive] [More...](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [@active] [Navigation Bar](#)
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
