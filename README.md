Memcached plugin for Playframework 2 (play2memcached)
=============


This is a plugin for Play2 which implements default play Cache interface for the Memcached. 
Also plugin has enhanced async interface.

# Usage

To use memcached plugin simply add it in file `project/play.plugins` the following line:


	0:eu.inn.play2memcached.MemcachedPlugin

And in build.sbt like this:

	libraryDependencies ++= Seq(
		...
		"eu.inn" %% "play2memcached" % "0.1"
	) 

After that you can use regular Play2 Cache API:

	val value = Cache.get(key)
	Cache.set(data.key, data.value.get)
	Cache.remove(data.key)

or asynchronous versions:

	Memcached.getAsync(key)
	Memcached.setAsync(data.key, data.value.get)
	Memcached.removeAsync(data.key)

You can find example app in examples/hello

# Restrictions
Currently this testd and works with string data using play 2.2
