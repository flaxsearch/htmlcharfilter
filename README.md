Flax HTMLCharFilter
-------------------

Unlike the standard Lucene [HTMLStripCharFilter](http://lucene.apache.org/core/4_8_0/analyzers-common/org/apache/lucene/analysis/charfilter/HTMLStripCharFilter.html)
this HTMLCharFilter will remove all tags and translate all XML entities.  It will also report offsets 'intuitively',
ignoring tags and preserving the length of entity references.

For example, the following text:

```
<a>this &amp; th&aacute;t</b> &gt; 3 H&amp;
^.........^.........^........^.........^...
```

will output the following tokens:


| Token | Position | Start Offset | End Offset |
|-------|----------|--------------|------------|
| this  | 0        | 3            | 7 |
| & | 1 | 8 | 13 |
| thát | 2 | 14 | 25 |
| > | 3 | 30 | 34 |
| 3 | 4 | 35 | 36 |
| H& | 5 | 37 | 43 |