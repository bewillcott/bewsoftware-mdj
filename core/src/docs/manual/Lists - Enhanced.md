@@@

use: articles2
title: MDj Core Library | Lists - Enhanced

@@@
<style>
    article {max-width: 64em;}
</style>


## Lists - Enhanced
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

- [Home](index.html)
- [@right] [About](About.html)

@@@
