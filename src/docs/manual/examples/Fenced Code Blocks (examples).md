@@@

use: articles2
title: ${document.name} | Fenced Code Blocks (Examples)

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
Now we will continue with the narrative. [][@upArrow](#top)

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
Now we will continue with the narrative. [][@upArrow](#top)

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

Now we will continue with the narrative. [][@upArrow](#top)

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
Now we will continue with the narrative. [][@upArrow](#top)


@@@[#navbar]
- [][Fenced Code Blocks][@previous]
- [Home]
- [Code Blocks](#)
    - [Fenced Code Blocks]
- [Lists]
    - [Lists (Enhanced)]
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
