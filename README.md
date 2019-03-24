# Rena-Java
Rena-Java is a library of parsing texts. Rena-Java makes parsing text easily.
Rena-Java can treat recursion of pattern, hence Rena-Java can parse languages which described top down parsing
like arithmetic expressions and so on.
Rena-Java can also treat synthesized and inherited attributes.
'Rena' is an acronym of REpetation (or REcursion) Notation API.

## Examples

### Parsing simple arithmetic expressions
```java
Rena<Integer> r = new Rena<Integer>();
PatternMatcher<Integer> expr = r.then(Rena.letrec(
  (t, f, e) -> r.then(f).thenZeroOrMore(r.or(
               r.string("+").then(f, (x, a, b) -> b + a),
               r.string("-").then(f, (x, a, b) -> b - a))),
  (t, f, e) -> r.then(e).thenZeroOrMore(r.or(
               r.string("*").then(e, (x, a, b) -> b * a),
			   r.string("/").then(e, (x, a, b) -> b / a))),
  (t, f, e) -> r.or(r.regex("[0-9]+", (x, a, b) -> Integer.parseInt(x)),
               r.string("(").then(t).then(r.string(")"))))).end();

// outputs 7
System.out.println(expr.parse("1+2*3", 0).getAttribute());

// outputs 1
System.out.println(expr.parse("4-6/2", 0).getAttribute());
```

### Document
[Document](http://rena.morilib.net/java/index.html) is available.
