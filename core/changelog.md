# Change Log - Markdownj-Core

## What's New

This version is a fork of the main project.  I don't know if it will
be integrated back into that project, or continue as a separate one.

### Version: 0.5 Snapshot
#### 2000/02/04

##### Code review
All the code was reviewed and updated to latest Java features.

-   Where useful, anonymous classes were converted to the Lambda format.
-   To help with clarity, I also ran the Netbeans: Source->Organize Members menu
    option.
-   Cleaned up some code that was either unnecessary, or could be redone in a less verbose
    manner.

##### Extended markdown capability
###### Fenced Code Blocks
Start line with "~~~" or "\`\`\`". The fencing requires that the block ends with the  
same sequence as it starts with.

For example: 
```
~~~
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
~~~
```
would produce:
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
which displays as:
~~~
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
~~~

If you want to use classes, then:  
"~~~" or "\`\`\`" followed by a space then "[][]" where the first bracket  
contains the classes for the "`<pre >`" tag and the second one contains the  
classes for the "`<code >`" tag.  

For example: 
```
~~~ [prettyprint][language-java]
public String toString(){
    return "Hello World";
}
~~~
```
would produce:
~~~
<pre class="prettyprint">
    <code class="language-java">
public String toString(){
    return "Hello World";
}
    </code>
</pre>
~~~
which displays as:
~~~ [prettyprint][language-java]
public String toString(){
    return "Hello World";
}
~~~

An alternative would be to use the original idea of the `lang:` keyword:  
"~~~" or "\`\`\`" followed by a space then "lang:" then the language will produce  
the original result.

For example:
~~~
``` lang:java
public String toString(){
    return "Hello World";
}
```
~~~

would produce:<br>
```
<pre class="java">
    <code>
public String toString(){
    return "Hello World";
}
    </code>
</pre>
```
which displays as:
``` lang:java
public String toString(){
    return "Hello World";
}
```
###### Tables
