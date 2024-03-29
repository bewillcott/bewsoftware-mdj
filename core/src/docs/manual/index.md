@@@

use: articles2
title: ${document.name}

@@@


# ${document.name}

The code-base for this library is a fork off the [main]! code-base on GitHub.  As that 
project appears to have gone to sleep, I have decided to take up the reigns and continue
its development. 

You'll find my code-base here: <https://github.com/bewillcott/mdj>!

## What's changed

I am not going to try and catalogue everything I have done, as I have been more interested
in getting it to compile and work, than documenting the minutia.  Suffice it to say, I 
have put the code through the ringer. Here are a few general things that have been done:

- Updated the code to incorporate features of JDK 18.  Then compiled under JDK 18.
    - Where appropriate, converted anonymous classes to Lambda expressions or even an inner class.
    - Tidied up and normalized code all over, but mostly in the main file: `MarkdownProcessor.java`.

- Added new features:
    - Fenced Code Blocks
    - Tables
    - Links - "!" to set `target="_blank"` like this [link][Links]! will ...
    - Check Boxes
    - Bracketed Options `[]` to various tags, which adds `class=` and even `id=`.
    - Added a _class_ attribute to many features.

- Changes:
    - Links - `[ids]` are now case-sensitive.


## Active Development

I expect that this library will be in regular development, including the markdown spec that
it facilitates, for some time to come.

Check out the ToDo List page.


[Links]:Links (Enhanced).html
[main]:https://github.com/myabc/markdownj


@@@[#navbar]
- [@active] [Home](#)
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
