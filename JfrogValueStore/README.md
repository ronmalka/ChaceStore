# JFrog Value Store Question:

We have an interface called [ValueStore](ValueStore.java), which have 3 operations - read/put/delete. see javadoc for each method.

We have multiple implementation of this interface, which is used to get key/value in a given server, serving many requests very rapidly.

You can check the [ValueStoreExamples](ValueStoreExamples.java) to understand what valueStore implementations can be.

You can check the [CachedValueStoreTest](CachedValueStoreTest.java) for basic test, to show functionality

### What we need to do:

We want to create an abstraction on top of many providers, to allow a seamless, fast interaction with those providers at one place.

We ask you to implement [CachedValueStore](CachedValueStore.java), with the following considerations:

- Values should be cached.
- Cache should be bounded, based on LRU evictions, 
- Cache should be efficient, to be able to serve this busy server.

See <a href="https://en.wikipedia.org/wiki/Cache_replacement_policies#Least_recently_used_(LRU)">LRU on wikipedia</a>.

