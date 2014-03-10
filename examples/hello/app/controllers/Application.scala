package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.cache.Cache
import eu.inn.play2memcached.Memcached
import scala.concurrent.ExecutionContext


object Application extends Controller {
	import play.api.Play.current

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  case class CacheFormData(key: String, value: Option[String])
  val cacheForm = Form(mapping(
		"key" -> text,
		"value" -> optional(text)
  	)(CacheFormData.apply)(CacheFormData.unapply)
	)

	def get(key: String) = Action { implicit request =>
		val value = Cache.get(key)
		if (value.isDefined)
			Ok(value.get.asInstanceOf[String])
		else
			Ok("NO VALUE CACHED for " + key)
	}

  def put = Action { implicit request =>
		val data = cacheForm.bindFromRequest.get
		Cache.set(data.key, data.value.get)
		Ok
  }

	def delete = Action { implicit request =>
		val data = cacheForm.bindFromRequest.get
		Cache.remove(data.key)
		Ok
	}

	import ExecutionContext.Implicits.global
	def getAsync(key: String) = Action.async { implicit request =>
		Memcached.getAsync(key) map { result =>
			if (result.isDefined)
				Ok(result.get.asInstanceOf[String])
			else
				Ok("NO VALUE CACHED for " + key)
		}
	}

	def putAsync = Action.async { implicit request =>
		val data = cacheForm.bindFromRequest.get
		Memcached.setAsync(data.key, data.value.get) map { result =>
			Ok
		}
	}

	def deleteAsync = Action.async { implicit request =>
		val data = cacheForm.bindFromRequest.get
		Memcached.removeAsync(data.key) map { result =>
			Ok
		}
	}
}