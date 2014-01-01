package eu.inn.play2memcached

import play.api.cache._
import play.api.Application
import net.spy.memcached._
import net.spy.memcached.ConnectionFactoryBuilder.{Locator, Protocol}
import scala.concurrent.{Promise, Future}
import net.spy.memcached.internal.{GetCompletionListener, GetFuture, OperationFuture, OperationCompletionListener}
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import org.apache.commons.lang3.reflect.TypeUtils

/**
 * Extended Cache API for a Cache plugin.
 * Includes async methods.
 *
 * TODO: add cas and other operations
 */

trait CacheAPIEx {

  /**
   * Set a value into the cache.
   *
   * @param key Item key.
   * @param value Item value.
   * @param expiration Expiration time in seconds (0 second means eternity).
   */
  def setAsync(key: String, value: Any, expiration: Int): Future[Unit]

  /**
   * Retrieve a value from the cache.
   *
   * @param key Item key.
   */
  def getAsync(key: String): Future[Option[Any]]

  /**
   * Remove a value from the cache
   */
  def removeAsync(key: String): Future[Boolean]
}

class Memcached(memcached: MemcachedClient) extends CacheAPI with CacheAPIEx {
  def set(key: String, value: Any, expiration: Int): Unit = memcached.set(key, expiration, value).get()

  def get(key: String): Option[Any] = {
    val r = memcached.get(key)
    if (r == null)
      None
    else
      Some(r)
  }

  def remove(key: String): Unit = memcached.delete(key).get()

  def setAsync(key: String, value: Any, expiration: Int): Future[Unit] = {
    asFutureOp(memcached.set(key, expiration, value), (result: java.lang.Boolean) => {})
  }

  def getAsync(key: String): Future[Option[Any]] = {
    asFutureGet(memcached.asyncGet(key), (r: Any) => {
      if (r == null)
        None
      else
        Some(r)
    })
  }

  def removeAsync(key: String): Future[Boolean] = {
    asFutureOp(memcached.delete(key), (result: java.lang.Boolean) => {
      result
    })
  }

  private def asFutureOp[A, B](of: OperationFuture[A], f: A => B): Future[B] = {
    val p = Promise[B]
    of.addListener(new OperationCompletionListener {
      def onComplete(future: OperationFuture[_]) = {
        try {
          val result = future.get()
          p.success(f(result.asInstanceOf[A]))
        }
        catch {
          case t: Throwable =>
            p.failure(t)
        }
      }
    })
    p.future
  }

  private def asFutureGet[A, B](of: GetFuture[A], f: A => B): Future[B] = {
    val p = Promise[B]
    of.addListener(new GetCompletionListener {
      def onComplete(future: GetFuture[_]) = {
        try {
          val result = future.get()
          p.success(f(result.asInstanceOf[A]))
        }
        catch {
          case t: Throwable =>
            p.failure(t)
        }
      }
    })
    p.future
  }
}

class MemcachedPlugin(app: Application) extends CachePlugin {

  @volatile var loaded = false

  lazy val memcached = {
    loaded = true
    createMemcachedClient
  }

  override lazy val enabled = {
    !app.configuration.getString("memcached.plugin").filter(_ == "disabled").isDefined
  }

  override def onStart() {
    memcached
  }

  override def onStop() {
    if (loaded) {
      memcached.shutdown()
    }
  }

  lazy val api = new Memcached(memcached)

  private def createMemcachedClient: MemcachedClient = {
    val binaryProtocol = app.configuration.getString("memcached.protocol").filter(_ == "binary").isDefined
    val nodes = app.configuration.getString("memcached.hosts").getOrElse("127.0.0.1:11211")
    val timeout = app.configuration.getLong("memcached.timeout").getOrElse(DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT)
    val reconnectDelay = app.configuration.getLong("memcached.reconnectDelay").getOrElse(DefaultConnectionFactory.DEFAULT_MAX_RECONNECT_DELAY)

    val builder = new ConnectionFactoryBuilder()
    builder.setFailureMode(FailureMode.Redistribute)
    builder.setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
    builder.setLocatorType(Locator.CONSISTENT)
    builder.setOpTimeout(timeout)
    if (binaryProtocol)
      builder.setProtocol(Protocol.BINARY)
    else
      builder.setProtocol(Protocol.TEXT)
    builder.setMaxReconnectDelay(reconnectDelay)

    new MemcachedClient(builder.build(), AddrUtil.getAddresses(nodes))
  }
}

object Memcached {
  import scala.concurrent.ExecutionContext.Implicits.global

  private def cacheAPI(implicit app: Application): Memcached = {
    app.plugin[MemcachedPlugin] match {
      case Some(plugin) => plugin.api
      case None => throw new Exception("There is no MemcachedPlugin registered.")
    }
  }

  def set(key: String, value: Any, expiration: Int = 0)(implicit app: Application): Unit = {
    cacheAPI.set(key, value, expiration)
  }

  def set(key: String, value: Any, expiration: Duration)(implicit app: Application): Unit = {
    set(key, value, expiration.toSeconds.toInt)
  }

  def get(key: String)(implicit app: Application): Option[Any] = {
    cacheAPI.get(key)
  }

  def getAs[T](key: String)(implicit app: Application, ct: ClassTag[T]): Option[T] = {
    get(key)(app).flatMap { item =>
      if (TypeUtils.isInstance(item, ct.runtimeClass)) Some(item.asInstanceOf[T]) else None
    }
  }

  def remove(key: String)(implicit app: Application) {
    cacheAPI.remove(key)
  }

  def setAsync(key: String, value: Any, expiration: Int = 0)(implicit app: Application): Future[Unit] = {
    cacheAPI.setAsync(key, value, expiration)
  }

  def setAsync(key: String, value: Any, expiration: Duration)(implicit app: Application): Future[Unit] = {
    setAsync(key, value, expiration.toSeconds.toInt)
  }

  def getAsync(key: String)(implicit app: Application): Future[Option[Any]] = {
    cacheAPI.getAsync(key)
  }

  /*
  def getAsAsync[T](key: String)(implicit app: Application, ct: ClassTag[T]): Future[Option[T]] = {
    getAsync(key)(app).map { item =>
      if (TypeUtils.isInstance(item, ct.runtimeClass))
        Some(item.asInstanceOf[T])
      else
        None
    }
  }
  */
  def removeAsync(key: String)(implicit app: Application): Future[Boolean] = {
    cacheAPI.removeAsync(key)
  }
}
