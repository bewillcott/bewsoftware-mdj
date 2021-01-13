@@@

use: articles2
title: ${document.name} | Fenced Code Blocks

@@@


## Fenced Code Blocks

The idea was taken from the [GitHub Flavored Markdown Spec][gfms]!.  
However, I have made this spec somewhat tighter than the GFMS.

### The Specification

Fenced Code Blocks start with a fence of either triple back-ticks: "```", 
or triple tildes: "~~~", and ends with its duplicate.  The fence must start
at the beginning of the line.  Every line in between the fences is wrapped 
inside "`<pre><code> ... </code></pre>`" tags.

Unlike the GFMS version, you *must* have both fences or the code block
will not be recognized.  Also, the [info-string]! has been replaced with
bracketed options.

You can add classes to either both the "`<pre>`" and "`<code>`" tags, 
or either one of them, or neither one:
~~~
```[pre tag classes][code tag classes]
Code
```
~~~
Example 1:
~~~
```[prettyprint][language-java]
Code
```
~~~
Produces this:
```
<pre class="prettyprint">
    <code class="language-java">
Code
    </code>
</pre>
```
Results in this:
<pre class="prettyprint">
    <code class="language-java">
Code
    </code>
</pre>

You can also add an "`id="newId"`" attribute to the "`<pre>`" tag.  
By inserting an option box just after the fence, and putting a __#__ at the
beginning.  The rest of the text in the box becomes the _id_.

~~~
```[#newId][pre tag classes][code tag classes]
```
~~~

Example 2:
~~~
```[#example2]
Some code
```
~~~
Produces this:
~~~
<pre id="example2">
    <code>
Some code
    </code>
</pre>
~~~
Results in this:
<pre id="example2">
    <code>
Some code
    </code>
</pre>

or

Example 3:
~~~
```[#example3][prettyprint][language-java]
Some more code
```
~~~
Produces this:
~~~
<pre id="example3" class="prettyprint">
    <code class="language-java">
Some more code
    </code>
</pre>
~~~
Results in this:
<pre id="example3" class="prettyprint">
    <code class="language-java">
Some more code
    </code>
</pre>


Check out these examples: [][@upArrow](#top "Goto Top") [][ex][@next]

[gfms]:https://github.github.com/gfm/#fenced-code-blocks
[info-string]:https://github.github.com/gfm/#info-string
[ex]:examples/Fenced Code Blocks (examples).html "More examples..."


@@@[navbar]
- [Home]
- [@dropdown subactive] [Code Blocks](#)
[@dropdown-content]
    - [@active] [Fenced Code Blocks](#)
- [@dropdown] [Lists]
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
