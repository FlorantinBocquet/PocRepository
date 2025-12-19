# POC Repository

Welcome reader, to this repository dedicated to proofs of concept of any kind. On this repository, I expose the results
of some of my experiments of code, coming from two question :

- *'How does it work ?'*
- *'How can I do it ?'*

Those two questions led me to create this repository, and the experiments inside.

## Pocs and Items

### Proto

Being used to Spring boot, I wanted to try to understand how Spring can check on the code and load everything by itself.
This poc has been done step by step to attain a simple bean loader.

A [changelog](src/main/java/proto/Changelog.md) is available here with the content and objectives of each steps.

### Single

After having to use WebFlux, I wanted to build an object based on the Monad pattern, but not linked to reactive
programming. So I made this object, and added multiple methods to manipulate the result.

### Tree

After discussing binary tree, I learned that database indexes use a tree like structure, so I wanted to try and see what
a multi layered binary tree would look like in java. This is my proposition.

- Binary is just a prototype of proposition of an 'ordinary' binary tree
- NBinary and NBinary 2 are first steps prototype of a multi leveled tree

The real Poc here is located in the [nbinary](src/main/java/tree/nbinary) source folder.

### Weighted selector

I just wanted to make a basic algorithm using rules to weight a list of item and returning the heaviest ones.

### Wrapper

You find the Stream library to heavy to read ? Me too. So I did that Poc. It covers the basic function of Stream, but as
fewer layers of obscurity, and is based on the way of thinking of Kotlin Sequence, except there is no extension function.

### Utils

In this folder there is everything and anything, short bit of code I wanted to make.