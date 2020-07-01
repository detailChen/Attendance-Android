package com.attendance.bk.http;

import java.util.Map;

/**
 * Created by CXJ
 * Created date 2019/12/3/003
 */
public interface BuildHeadersListener {
    Map<String, String> buildHeaders();
}
