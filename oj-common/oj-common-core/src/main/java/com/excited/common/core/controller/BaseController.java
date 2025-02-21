package com.excited.common.core.controller;

import com.excited.common.core.domain.R;

public class BaseController {

    public R<Void> toR(int raws) {
        return raws > 0 ? R.ok() : R.fail();
    }

    public R<Void> toR(boolean result) {
        return result ? R.ok() : R.fail();
    }
}
