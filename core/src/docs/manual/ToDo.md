@@@

use: articles2
title: ${document.name} | ToDo List

@@@



## ToDo List

- [ ]! Update manual.
- [ ]! Add Kramdown features:
    - [ ]! Definition lists:<br>
    `term`  
    `\&nbsp;\&nbsp;\&nbsp;\&nbsp;: definition`  
    `\&nbsp;\&nbsp;\&nbsp;\&nbsp;: another definition`
    - [ ]! Abbreviations: `*[text]: Full text`
    - [x]! Footnote: `[^n]: Footnote text`
        - [ ]! More work to do: automate generation of page location code.
- [ ]! Add class attribute where useful: `[@<classname>]`.
    - [x]! [Links (Enhanced)]
    - [x]! [Paragraph (Enhanced)]
    - [x]! Lists - see [Navigation Bar]
- [x]! (14/03/2020) Added superscripted footnote references.  
-!`[d%]`!+![^d%]! - d% must be a valid decimal number.  
+!See _footnote_ entry above.!
- [x]! (16/03/2020) Added footnote links[1]
- [ ]! Add cell/column based "`class=`" attributes.  
`|Col1|Col2|Col3|\[First Cell]\[Middle Cells]\[Last Cell]`
- [ ]! Improve access to the manual.
- [ ]! Add bracketed option to tags where useful: `[]`.
    - [x]! (20/02/2020) Added to [Fenced Code Blocks]
    - [x]! (21/02/2020) Added to [Tables]
    - [x]! (21/02/2020) Added to [Headings (Enhanced)]
    - [x]! (21/02/2020) Added to [Lists]
- [ ]! Add `id=""` as option for all tags with bracketed option `[#_id_]`.
    - [x]! (20/02/2020) Added to [Fenced Code Blocks]
    - [x]! (21/02/2020) Added to [Tables]
    - [x]! (21/02/2020) Added to [Headings (Enhanced)]
    - [x]! (21/02/2020) Added to [Lists]
    - [x]! (16/03/2020) Added to paragraphs: `[#idtext]`
- [x]! (21/02/2020) Added separate link option to [Headings (Enhanced)].
- [ ]! Add `<del>` and `<ins>`: 
    - [x]! (14/03/2020) `-!old words!` == `<del>old words</del>`.
    - [x]! (14/03/2020) `+!new words!` == `<ins>new words</ins>`.
    - [ ]! Add `[]` for url and date.
- [x]! (14/03/2020) Added subscript `--` and superscript `++`:
    - [x]! `--subscript--` == `<sub>subscript</sub>`.
    - [x]! `++superscript++` == `<sup>superscript</sup>`.
- [x]! (10/02/2020) Fix links to accept filenames with spaces. See: [Links]
- [x]! (10/02/2020) Add option to set `target="_blank"` for links (Autolinks too?).
        See: [Links]
    -   `[link]!` or `[label][link]!`

---

1.[#1] Use a decimal number in brackets.  It will be superscripted, 
and keeps the brackets.

[1]:#1
[Fenced Code Blocks]:Fenced Code Blocks.html
[Headings (Enhanced)]:Headings (Enhanced).html
[Links (Enhanced)]:Links (Enhanced).html
[Lists]:Lists (Enhanced).html
[Tables]:Tables.html

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
- [@dropdown] [More...](#)
[@dropdown-content]
    - [Links (Enhanced)]
    - [Navigation Bar]
    - [Tables]
- [@right dropdown subactive] [About]
[@dropdown-content]
    - [@active] [ToDo List](#)
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
