package io.katharsis.core.engine.error.handlers;

import io.katharsis.core.engine.error.ErrorResponse;
import io.katharsis.core.engine.error.ExceptionMapper;

/**
 * Created by yuval on 02/03/2017.
 */
public abstract class BaseExceptionMapper<E extends Throwable> implements ExceptionMapper<E>{

  @Override
  public E fromErrorResponse(ErrorResponse errorResponse) {
    return null;
  }

  @Override
  public boolean accepts(ErrorResponse errorResponse) {
    return false;
  }
}