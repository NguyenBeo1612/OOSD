package com.eshop.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Lớp tiện ích bọc NamedParameterJdbcTemplate – tương ứng lớp Db.cs (Query / Execute / Scalar) thường dùng trong bản WinForms.
 * Kết quả truy vấn là List&lt;Map&gt; (tương tự DataTable), khóa không phân biệt hoa thường.
 */
@Component
public class Db {

    private final NamedParameterJdbcTemplate jdbc;

    public Db(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Tạo map tham số từ cặp khóa / giá trị (cho phép giá trị null). */
    public static Map<String, Object> p(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put((String) kv[i], kv[i + 1]);
        }
        return m;
    }

    public List<Map<String, Object>> query(String sql, Map<String, Object> params) {
        return jdbc.queryForList(sql, params);
    }

    public List<Map<String, Object>> query(String sql) {
        return jdbc.queryForList(sql, new HashMap<>());
    }

    public int execute(String sql, Map<String, Object> params) {
        return jdbc.update(sql, params);
    }

    public long count(String sql, Map<String, Object> params) {
        Long n = jdbc.queryForObject(sql, params, Long.class);
        return n == null ? 0 : n;
    }

    public BigDecimal money(String sql, Map<String, Object> params) {
        BigDecimal b = jdbc.queryForObject(sql, params, BigDecimal.class);
        return b == null ? BigDecimal.ZERO : b;
    }

    /** Một giá trị, hoặc null nếu không có dòng nào. */
    public <T> T scalar(String sql, Map<String, Object> params, Class<T> type) {
        List<T> l = jdbc.queryForList(sql, params, type);
        return l.isEmpty() ? null : l.get(0);
    }
}
