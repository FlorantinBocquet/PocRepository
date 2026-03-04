# Tree

When looking at the way postgres was managing indexes, I saw that one of its ways was to set a kind of multi layered
binary tree to sort the data. So I try to build something that would work in the same kind of way.

The goal was to make a multi layered binary tree, able to store a give input data, kind of lke a multi key map.

Note : for this poc, there is no auto rebalancing of the tree. However, most of the trees include a rebalancing method
to do it manually.

## Binary

The first step was to build a simple test binary tree. The [Binary](Binary.java) object is only connected to the root
node, and delegate the inputting of data to the nodes. The data are stored in a [StoredData](data/StoredData.java)
implementation, that can be either implemented by the user, or use a [DefaultStoredData](data/DefaultStoredData.java)
when calling the default binary method of the class.

## NBinary

The second step was to make it a multi layered tree, and to attain this purpose the changes were applied to the node
object, transforming it from a simple object to an interface with simple signature.

```
private interface BinaryNode<D, T extends Comparable<T>> {
    void add(final D data, final T[] keys, final int depthIndex);
    List<D> getData(final T[] keys, final int depthIndex);
}
```

On this node two implementation were made : IntermediateNode and DeepEndNode. The main different between the two is the
type of data they would store. The DeepEndNode would act like the node of the previous Binary Tree, where the
IntermediateNode contains another BinaryNode, that could be of either types.

This tree was kept simple and used only comparable object as storable objects.

## NBinary2

The second version of the multi layered tree switch from a Comparable object to any type, but would now require
comparators to ba passed in constructor. Each comparator passed will add a layer, and layers will have the same order as
the comparators. (i.e. : the first comparator will apply to the top layer, the last comparator to the depper layer)

The BinaryNode interface complexify a little, as a rebalancing method was added, and it needed a bit more element to
process the rebalancing (getters and setters)

## NBinaryTree

Similar to the NBinary2, the [NBinaryTree](nbinary/NBinaryTree.java) include a given comparator based layer generation.
The main difference is the addition of operations. In the first step, of this poc, the first Binary object had search
functions, but since there was only one layer, it could easily be define. But with a multilayered tree, how can we do
it ?

That were the operations came in. Operations are used when trying to retrieve data. For each layer of the tree,
one [Operation](nbinary/operation/Operation.java) is required.

`tree.search(Operation.equalz("Hello"), Operation.inferiorStrict(2));`
`tree.search(Operation.notEquals("Hello"), Operation.all())`

In those example of search, we assume we have a two layer tree with a first layer having String, and the second having
Integer. When using the search function, two Operation are expected to be provided.

- The first search means : I want all values having first layer key equals to Hello, and having second key strictly less
  than 2.
- The second search means : I want all values having first layer key not equals to Hello, and not filtering on second
  key.

As seen, the search model is somewhat limited as it is not possible to cumulate multiple operation on a single layer.