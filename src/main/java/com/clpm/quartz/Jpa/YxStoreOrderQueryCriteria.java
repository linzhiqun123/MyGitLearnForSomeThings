package com.clpm.quartz.Jpa;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
* @author hupeng
* @date 2019-10-14
*/
@Data
public class YxStoreOrderQueryCriteria {

    @Query(type = Query.Type.LEFT_LIKE)
    private String keyword;
}
