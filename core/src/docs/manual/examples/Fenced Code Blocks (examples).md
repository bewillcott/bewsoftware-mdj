@@@

use: articles2
title: MDj Core Library | Fenced Code Blocks (Examples)

@@@


## Fenced Code Block Examples

This will be converted to a code block (using: ``` - 3 back-ticks):
~~~
```
/**
 * Sample class with Javadoc comment.
 */
public class Fred {
    public final String text;

    /*
     * Good place to start
     */
    public Fred(String text){
        this.text = text
    }

    public String toString(){
        return text;
    }
}
```
~~~

Produces this:
~~~
<pre>
    <code>
/**
 * Sample class with Javadoc comment.
 */
public class Fred {
    public final String text;

    /*
     * Good place to start
     */
    public Fred(String text){
        this.text = text
    }

    public String toString(){
        return text;
    }
}
    </code>
</pre>
~~~

Results in this:
```
/**
 * Sample class with Javadoc comment.
 */
public class Fred {
    public final String text;

    /*
     * Good place to start
     */
    public Fred(String text){
        this.text = text
    }

    public String toString(){
        return text;
    }
}
```
Now we will continue with the narrative. [&uarr;](#top)

This should be converted to a code block (using: ``` - 3 back-ticks):
~~~
```lang: java
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
```
~~~

Produces this:
~~~
<pre class="java">
    <code>
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
    </code>
</pre>
~~~
Results in this:
```lang: java
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
```
Now we will continue with the narrative. [&uarr;](#top)

This should be converted to a code block (using: ~~~ - 3 tildes):
```
~~~[prettyprint][language-java] 
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
~~~
```

Produces this:
```
<pre class="prettyprint">
    <code class="language-java">
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
    </code>
</pre>
```

Results in this:
~~~[prettyprint][language-java] 
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
~~~

Now we will continue with the narrative. [&uarr;](#top)

This should be converted to a code block (using: ~~~ - 3 tildes):
```
~~~[][language-java] 
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
~~~
```
Produces this:
```
<pre>
    <code class="language-java">
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
    </code>
</pre>
```

Results in this:
~~~[][language-java] 
public class Fred {
    public final String text;

    public Fred(String text){
        this.text = text
    }
}
~~~
Now we will continue with the narrative. [&uarr;](#top)


@@@[navbar]
- [![Back]][Fenced Code Blocks][@icon]
- [Home]
- [@dropdown] [Code Blocks][@dropbtn](#)
[@dropdown-content]
    - [Fenced Code Blocks]
- [@dropdown] [Lists][@dropbtn]
[@dropdown-content]
    - [Lists (Enhanced)]
- [@dropdown] [Text][@dropbtn](#)
[@dropdown-content]
    - [Ampersands]
    - [Headings (Enhanced)]
    - [Paragraph (Enhanced)]
    - [Text Embellishments]
- [@dropdown] [More...][@dropbtn](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [Tables]
- [@right dropdown] [About][@dropbtn]
[@dropdown-content]
    - [ToDo List]
    - [License]


[Back]:etc/32px-Go-previous.png
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
