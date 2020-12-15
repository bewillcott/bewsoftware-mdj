@@@

use: articles2
title: MDj Core Library | ToDo List

@@@


## ToDo List

- [ ]! Update manual.
- [x]! (14/03/2020) Added superscripted footnote references.  
`[d%]` - d% must be a valid decimal number.
- [x]! (16/03/2020) Added footnote links[1]
- [ ]! Add cell/column based "`class=`" attributes.  
`|Col1|Col2|Col3|\[First Cell]\[Middle Cells]\[Last Cell]`
- [ ]! Improve access to the manual.
- [ ]! Add bracketed option to tags where useful: `[]`.
    - [x]! (20/02/2020) Added to [Fenced Code Blocks][fcb]
    - [x]! (21/02/2020) Added to [Tables]
    - [x]! (21/02/2020) Added to [Headings]
    - [x]! (21/02/2020) Added to [Lists]
- [ ]! Add `id=""` as option for all tags with bracketed option `[#_id_]`.
    - [x]! (20/02/2020) Added to [Fenced Code Blocks][fcb]
    - [x]! (21/02/2020) Added to [Tables]
    - [x]! (21/02/2020) Added to [Headings]
    - [x]! (21/02/2020) Added to [Lists]
    - [x]! (16/03/2020) Added to paragraphs: `[#idtext]`
- [x]! (21/02/2020) Added separate link option to [Headings].
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
[fcb]:Fenced Code Blocks.html
[Headings]:Headings.html
[Home]:index.html
[Links]:Links.html
[Lists]:lists.html
[Tables]:Tables.html

@@@[navbar]

- [Home](index.html)
- [@right] [About](About.html)

@@@
