package com.ngw.fusion.common.util.api;

import java.io.Serializable;

public interface IResponseCode extends Serializable {
    public abstract String getMessage();

    public abstract int getCode();
}
