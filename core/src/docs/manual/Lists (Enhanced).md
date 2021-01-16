@@@

use: articles2
title: ${document.name} | Lists - Enhanced

@@@


## Lists (Enhanced)

### Check Boxes
To add check boxes to list entries, you add a bracketed option, with an optional
flag to disable the check box.

~~~
- [ ] This is a check box that is unchecked.
- [x] This one is checked.
    - [ ]! This one is unchecked and disabled.
        - [x]! Yes you got it, checked and disabled.
~~~
The above text produces this output:

~~~
<ul>
<li><input type="checkbox"> This is a check box that is unchecked.</li>
<li><input type="checkbox" checked> This one is checked.
<ul>
<li><input type="checkbox" disabled> This one is unchecked and disabled.
<ul>
<li><input type="checkbox" checked disabled> Yes you got it, checked and disabled.</li></ul></li></ul></li>
</ul>
~~~

Which produces this result:

- [ ] This is a check box that is unchecked.
- [x] This one is checked.
    - [ ]! This one is unchecked and disabled.
        - [x]! Yes you got it, checked and disabled.



@@@[navbar]
- [Home]
- [Code Blocks](#)
    - [Fenced Code Blocks]
- [@subactive] [Lists]
    - [@active] [Lists (Enhanced)](#)
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
