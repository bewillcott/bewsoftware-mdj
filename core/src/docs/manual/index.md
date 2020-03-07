
# Markdownj Core Library

The code-base for this library is a fork off the [main]! code-base on GitHub.  As that 
project appears to have gone to sleep, I have decided to take up the reigns and continue
its development.

## What's changed

I am not going to try and catalogue everything I have done, as I have been more interested
in getting it to compile and work, than documenting the minutia.  Suffice it to say, I 
have put the code through the ringer. Here are a few general things that have been done:

- Updated the code to incorporate features of JDK 13.0.2.
    - Where appropriate, converted anonymous classes to Lambda expressions or even an inner class.
    - Tidied up and normalized code all over, but mostly in the main file: MarkdownProcessor.java.
- Added new features:
    - [Fenced Code Blocks][fcb]
    - [Tables]
    - [Links] - "!" to set `target="_blank"` like this [link][Links]! will ...

- Changes:
    - [Links] - `[ids]` are now case-sensitive.


## Active Development

Check out the [ToDo List][todo] page.


[main]:https://github.com/myabc/markdownj
[Tables]:Tables.html
[fcb]:Fenced Code Blocks.html
[todo]:todo.html
[Links]:Links.html


