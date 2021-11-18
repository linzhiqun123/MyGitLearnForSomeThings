package com.clpm.quartz.Jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface YxStoreProoductRepository extends JpaRepository<YxStoreProduct, Integer>, JpaSpecificationExecutor<YxStoreProduct> {


    YxStoreProduct findYxStoreById(Integer id);

    @Query(value="select DISTINCT keyword,store_info from yx_store_product limit 0,100",nativeQuery = true)
    List<Map<String,String>> findCodeTableList();


}
