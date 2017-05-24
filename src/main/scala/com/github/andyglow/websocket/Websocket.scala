package com.github.andyglow.websocket

import com.github.andyglow.websocket.util.NettyFuture
import io.netty.channel.Channel
import io.netty.handler.codec.http.websocketx._

import scala.concurrent.{ExecutionContext, Future}

trait Websocket {
  def ![T : MessageFormat](msg: T): Unit
  def close(implicit ec: ExecutionContext): Future[Unit]
}

private[websocket] class WebsocketImpl(ch: Channel) extends Websocket {

  override def ![T : MessageFormat](msg: T): Unit = {
    val format = implicitly[MessageFormat[T]]
    ch writeAndFlush {format format msg}
  }

  override def close(implicit ec: ExecutionContext): Future[Unit] = {
    ch writeAndFlush new CloseWebSocketFrame()
    val f = NettyFuture(ch.closeFuture())
    f map {_ => ()}
  }
}
