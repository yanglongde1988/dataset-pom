package com.ngw.fusion.common.exception;

public class XdException extends Exception {
  private static final long serialVersionUID = 1L;
  
  private Throwable rootCause;
  
  public XdException(String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
    if (paramThrowable != null)
      if (paramThrowable instanceof XdException) {
        XdException xdException = (XdException)paramThrowable;
        this.rootCause = xdException.getCause();
      } else {
        this.rootCause = paramThrowable;
      }  
  }
  
  public Throwable getRootCause() {
    return this.rootCause;
  }
  
  public void setRootCause(Throwable paramThrowable) {
    this.rootCause = paramThrowable;
  }
  
  public XdException(String paramString) {
    super(paramString);
  }
  
  public String getExceptionStackInfo() {
    StringBuffer stringBuffer = new StringBuffer();
    Throwable throwable = getCause();
    if (throwable == null)
      return null; 
    stringBuffer.append(throwable.getClass().getName()).append(":").append(throwable.getMessage()).append("\r\n");
    StackTraceElement[] arrayOfStackTraceElement = throwable.getStackTrace();
    for (byte b = 0; b < arrayOfStackTraceElement.length; b++)
      stringBuffer.append("\t").append(arrayOfStackTraceElement[b]).append("\r\n"); 
    return stringBuffer.toString();
  }
}
