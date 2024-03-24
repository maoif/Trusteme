# Trusteme: Scheme on GraalVM

## Overview

Trusteme is a yet incomplete implementation of the Scheme programming language (R6RS)
on GraalVM using the Truffle API.

Trusteme supports hygienic macros as described in R6RS via the 
[psyntax](https://conservatory.scheme.org/psyntax/r6rs-libraries/) project,
with some changes.
All procedures required by psyntax are implemented so that  
psyntax itself in its expanded form can be run by the Trusteme interpreter.

This project is inspired by Mumbler.

## Usage

To build Trusteme, simply type the following in Trusteme's root directory 
in your favorite shell:

```shell
gradle distTar
```

In the first case, you will find the built tar ball at 
`launcher/build/distributions/launcher.tar`. 
You can just unpack it and find the launcher script under `bin/`.
For example, write a simple Scheme source and save it as `demo.ss`:

```scheme
(import (rnrs)
        (rnrs base))
        
(define str "Hello Trusteme!\n")
(display str)
```

Then invoke the launcher script with a source file:

```shell
./launcher demo.ss
```

You'll see the output!

## Test

To run some tests, run

```shell
gradle test
```

and wait for the result. 


# TODOs

Currently: no repl, no optimization, incomplete functionalities...

Now Trusteme just has enough builtins and primitives to run psyntax,
there are lots of work to do to make Trusteme fancier.
I put them here, though I may not have time to finish them.
Contributions are welcomed, of course.

- [] recognize comments in Reader
- [] a REPL
- [] record
- [] `eval`, `expand` and `load`
- [] optimizations like cp0 in ChezScheme
- [] `call/cc`, `dynamic-wind`, etc.
- [] interoperability with other Truffle languges
- [] native image
- [] better builtin node design

There are still some other issues, but I'll just keep them to myself.


# Implementation

In this section, I describe some implementation details of Trusteme,
just in case you want to learn something from it or want to contribute
some code, or both.

```
- what it does
- how it's implemented
- design decisions
```

## Reader

The Reader reads a subset of the Scheme syntax and outputs an `sExpr`.
It reads one Scheme datum at a time, so use `readAll()` if one has to
read all data in a file or string.

I plan to add more features to the Reader, as listed on the top in 
Reader's source file.

## Parser

The Parser converts the output `sExpr` from Reader to `TsmNode`s or `TsmExpr`s,
depending on whether the `sExpr` corresponds to a quoted form in the source.

If `sExpr` corresponds to a quoted form, then the Parser generates a `TsmQuoteNode`,
which when executed, will return the corresponding quoted datum.
Otherwise, the Parser generates other `TsmNode`s that can be executed.

For ease of implementation, the Parser only recognizes the core language, 
as defined below. Other Scheme constructs can be processed by the psyntax
macro expander and turned into the core form.

## Core Language

Since I wanted Trusteme to support hygienic macros through psyntax,
and the psyntax project has a script that can expand itself using itself
to a simple expanded core language, it's unsurprising that the core language
of Trusteme is just the core language defined by the output of psyntax.

Here is the core language definition:

```
<expr> ::= (begin <expr>+)
       |   (define <id> <expr>)                 ;; top-level definitions only
       |   (set! <id> <expr>)
       |   (lambda (<id>*) <expr>+)
       |   (lambda <id> <expr>+)
       |   (lambda (<id>+ . <id>) <expr>+)
       |   (if <expr> <expr> <expr>)
       |   (<expr> <expr>)                      ;; application
       |   (letrec ([<id> <expr>]+) <expr>+)
       |   (quote <datum>)
       |   <id>                                 ;; variable reference

<datum> ::= <fixnum> | <flonum> | <bignum>
        | <bool>   | <pair>   | <vector>
        | <symbol> | <string> | <char>
        | <eof>
```

## TsmNodes

All Trusteme executable nodes derive from `TsmNode`, which is child
of `Node` in the Truffle API.

Every right hand side in the `<expr>` non-terminal in the core language
has a respective `TsmNode` subtype. 
For example, the `begin` form has `TsmBeginNode`, the `quote` form has `TsmQuoteNode`.
The `lambda` form is a little special, however, because it permits different number
of parameters. Even though there are three types of `lambda` forms,
they all correspond to `TsmLambdaNode`. The difference of the number of parameters
is handled in the lambda case in `Parser.parseSpecialForm()`.

Take `TsmDefineNode` for example.
It introduces a new binding to the top-level environment, and returns void.
It has a field called `valueNode`, annotated by `@Child`, signifying
that it is a child node, and Truffle can do optimizations based on this.
Another field is `sym`, which is the name of the binding.

`TsmDefineNode` runs its `valueNode` first to obtain the value,
then it installs this value into the top-level environment.
The entire logic is written in `executeGeneric()`.

Now let's look at `TsmSymbolNode`. This node will return the value of the binding
when executed. The value can be bound either in some lexical scope (see sections below)
or in the top-level environment.

So in `TsmSymbolNode`'s `executeGeneric()` method, we search for the binding
starting from the current frame (lexical scope), and if not found, we continue with the previous
one, and so on up to the top frame. And finally we throw an exception if the binding
does not exist.

## Trusteme Data Types

Trusteme supports common Scheme data types, such as fixnum, flonum, bignum, 
boolean, char, string, pair, list, vector and so on.
They are represented by respective Java classes.
E.g, fixnum -> `TsmFixnum`, flonum -> `TsmFlonum`, pairs and lists are
represented by `TsmPair`, and the empty list is represented by the singleton `TsmNull`.
Procedures are also values, and they are represented by `TsmProcedure`.

All Trusteme data types are subclasses of `TsmExpr`, which implements the 
`TruffleObject` interface in conformance to the Truffle API.

`TsmExpr` defines several methods its subclasses must implement.
`write()` is for implementing the `write` procedure in Scheme, which
gives the textual representation of a datum, and this textual representation
can be further read back by the `read` procedure to obtain the same datum.
`isEq()`, `isEqv()` and `isEqual()` corresponds to the three equivalence predicates
Scheme has, is decreasing order of discriminating power.

One drawback of the current Trusteme type implementation is that 
in order to have a uniform type hierarchy, primitive types like fixnums, flonum,
bignums and booleans are boxed. This hurts performance.
Hopefully one day I'll improve it.

## Builtins

Builtins are primitive procedures implemented in Java, and registered in `TrustemeLanguage` class
in the `createTopFrame()` method.

`TsmBuiltinNode` is the base class of all builtin nodes. It currently does nothing but
has a `NAME` field that stores the name of the builtin procedure so that 
it can be registered into the top frame and looked up easily.

When a builtin instance is being registered in `TrustemeLanguage`, it is wrapped in 
a `TsmRootNode`, which is just how we create a piece of callable code/AST.
The same works for creating lambda expressions, see below.

Builtins are registered into the top frame as `(name . builtin)` pair.

Currently, the argument count check is duplicated in every builtin node.
This is a bad software engineering practice. I'll find time to fix this.

## Primitives

Primitives are Scheme procedures written in core-language Scheme,
it sits at `trusteme-lib/src/main/resources/prims.ss`, along with `psyntax.ss`.

Because primitives are written in the core language, all literals (strings, chars, booleans, etc.)
need to be quoted. There is no `let` form, since it is equivalent to lambda application.

`prims.ss` is loaded before `psyntax.ss`, because `psyntax.ss` uses some procedures 
that are defined in `prims.ss`. These procedures are more easily defined in Scheme
than in Java.

## The `psyntax` Macro Expander

by whom?

In order for psyntax to be usable in Trusteme, I made some changes to it.

The version downloaded from the website cannot be expanded directly by 
the ChezScheme (v9+) running on my machine.
Need to modify the bootstrap script


modifications: entry point, primitive reference uses identifier directly
library definition
the program loader used in launcher

If you want to learn more about the expander algorithm, see [...].



## Top Frame and Top-level Environment

Frames are used to pass procedure arguments.
When a procedure application occurs, a new frame is created and 
arguments are stored in it. A frame is either an instance of `VirtualFrame` 
or `MaterializedFrame`. The difference is explained in later text.
A frame has two mechanisms to store data. The first is the automatically
constructed argument array that can be accessed using `getArguments()`, as
you can see in almost every builtin node.
The second is local variable slots. The number of slots is fixed at parsing time.

The top frame is created as `VirtualFrame`. It is used to hold all the builtins
and the top-level environment, and is fixed once all builtins are added to it.

The first slot in the top frame is `null`, in order to distinguish it from other frames.
The second slot stores the top-level environment, which is essentially a Java map.

The top-level environment stores bindings introduced by `TsmDefineNode`, that is,
by `define` expressions in the core language.

When expanding the source, psyntax knows whether a `define` expression is top-level or not.
If it is some sort of internal definition in blocks like `let` or `letrec`, then
it will actually be transformed into `set!` expression that act on some auto-generated
dummy lexical variables. Only true top-level `define`s are preserved in the output.
That why in the core language semantics `define` only introduces bindings to the 
top-level environment.

There is no need to worry about name clashes, because psyntax will give each
binding a unique name (aka. alpha-conversion).

## Lexical Scope

Scheme is lexically scoped, and has closures that capture free variables.


use `MaterializedFrame` to represent the lexical scope
it carries free variables
it is similar to the linked closure method, as opposed to flat closure,
as used by most mature functional language implementations.

performance penalty

## Lambda Expressions and `TsmRootNode`s

Lambda expressions will be parsed as `TsmLambdaNode`s, which returns
a `TsmProcedure` instance when executed.

`TsmProcedure` has two important fields: `callTarget` and `lexicalScope`.
The former represents a callable Truffle AST, which is the code of the procedure;
the latter contains the lexical bindings accessible by the procedure.

Like the builtin nodes, the nodes generated from the body of a lambda expression
are wrapped in a `TsmRootNode`, so that it becomes callable AST.
Because `TsmRootNode` derives from `RootNode`, we can obtain a `RootCallTarget` by
calling `getCallTarget()`. To actually call the code hidden behind this `RootCallTarget`,
simply call its `call()` method, or, for better chances of optimization,
use `DirectCallNode`/`InDirectCallNode`. See `TsmAppNode` and `TsmAppDispatchNode` for 
more details/

When a lambda expression is being evaluated, or when a `TsmLambdaNode` is
executed, it materializes the frame and stores it into the procedure instance.
In this way it captures the value of those free variables in its body.

If you look at how `TsmRootNode`s are created in `TsmRootNode.create()`,
you would notice three types of nodes that are inserted before the body of
the lambda expression nodes: `TsmReadLexicalScopeNode`, `TsmReadArgNode`
and `TsmReadDotArgNode`.

In spirit of Mumbler, lexical scope is always passed as the first argument to every
procedure. Upon entry to a procedure, `TsmReadLexicalScopeNode` reads the lexical scope
and places it into the frame's first local variable slot for later use.
`TsmReadLexicalScopeNode` is always generated.

`TsmReadArgNode`s are generated after `TsmReadLexicalScopeNode`. It is
used to read positional arguments from the frame, and store them as `(name . value)` pairs 
into the frame slots, so `TsmSymbolNode` can find the value of a binding.

For example, for `(lambda (x y) (+ x y))`, the AST would roughly look like:

```
TsmRootNode
  TsmReadLexicalScopeNode
  TsmReadArgNode(x)
  TsmReadArgNode(y)
  TsmAppNode
    TsmSymbolNode(+)
    TsmSymbolNode(x)
    TsmSymbolNode(y)
```

`TsmReadDotArgNode` is used to implement variable-arity Scheme procedures.
They come in two flavors, one is dot argument, like `(lambda (x y . z) ...)`,
another is a single list argument: `(lambda args ...)`.

In the first case, the procedure expects at least two arguments.
If it receives exactly two, then `z` will be `'()`; otherwise
those extra arguments are packed into a list and bound to `z`.

In the second case, the procedure receives zero or more arguments.
If no arguments are passed, `args` is `'()`, otherwise all arguments
are packed into a list and bound to `args`.

`TsmReadDotArgNode` covers both cases.

AST of `(lambda (x y . z) ...)` would look like:

```
TsmRootNode
  TsmReadLexicalScopeNode
  TsmReadArgNode(x)
  TsmReadArgNode(y)
  TsmReadDotArgNode(z)
  ...
```

AST of `(lambda args ...)` would just look like:

```
TsmRootNode
  TsmReadLexicalScopeNode
  TsmReadDotArgNode(args)
  ...
```

# Tail Call Optimization: TODO

In order not to blow the stack when writing recursive functions,
we need some tail call optimization.

tail position

We can identify the following tail positions in the core language:

```
expr ::= (begin <expr>* <tail>)
         (lambda (<id>*) <expr>* <tail>)
         (lambda <id> <expr>* <tail>)
         (if <expr> <tail> <tail>)
         (letrec ([<id> <expr>]*) <expr>* <tail>)
```

`TailCallException`

`call()` method in places where a nodes is executed in non-tail position