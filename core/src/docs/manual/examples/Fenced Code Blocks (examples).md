@@@

use: articles
title: MDj Core Library | Fenced Code Blocks (Examples)

@@@

[Back] [Home]

---

##[#top] Fenced Code Block Examples

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

[Back]:../Fenced Code Blocks.html
[Home]:../index.html
