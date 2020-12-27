@@@

use: articles2
title: ${document.name} | TOC

@@@

## NavBar
The navigation bar.

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
