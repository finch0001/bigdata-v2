package com.yee.bigdata.common.thread

/**
  * SPARK-24294: To bypass scala bug: https://github.com/scala/bug/issues/9554, we catch
  * fatal throwable in {@link scala.concurrent.Future}'s body, and re-throw
  * SparkFatalException, which wraps the fatal throwable inside.
  * Note that SparkFatalException should only be thrown from a {@link scala.concurrent.Future},
  * which is run by using ThreadUtils.awaitResult. ThreadUtils.awaitResult will catch
  * it and re-throw the original exception/error.
  */
//private[spark] final class SparkFatalException(val throwable: Throwable) extends Exception

final class SparkFatalException(val throwable: Throwable) extends Exception

