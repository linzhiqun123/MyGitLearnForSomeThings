package com.clpm.quartz.config;

import com.clpm.quartz.Jpa.YxStoreProoductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author 86178
 * @create 2021/11/18 22:25
 */
@Component
public class CodeTable {

    public static Map<String,String> codeMap=new HashMap<>();

    @Autowired
    YxStoreProoductRepository yxStoreProoductRepository;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @PostConstruct
    private void InitCodeTable() throws ExecutionException, InterruptedException {

        CompletableFuture<List<Map<String, String>>> listCompletableFuture = getCodeInitList();

        List<Map<String, String>> mapList = listCompletableFuture.get();

        for (Map<String, String> map : mapList) {
            String keyword = map.get("keyword");
            String store_info = map.get("store_info");
            codeMap.put(keyword, store_info);
        }
    }

    private CompletableFuture<List<Map<String, String>>> getCodeInitList() {

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        ArrayList<Map<String, String>> arrayList = new ArrayList<>();

//        CompletableFuture<List<Map<String, String>>> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
//            stringStringHashMap.put("TestCode","韩语翻译");
//            arrayList.add(stringStringHashMap);
//            return arrayList;
//        });
//
//        CompletableFuture<List<Map<String, String>>> integerCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
//            stringStringHashMap.put("蚩罗","狼哥");
//            arrayList.add(stringStringHashMap);
//            return arrayList;
//        });

        return CompletableFuture.supplyAsync(() -> {
            return yxStoreProoductRepository.findCodeTableList();
        },threadPoolExecutor);
    }

}
