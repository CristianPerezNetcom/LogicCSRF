package com.netcom.logintaptophone.util;

import com.netcom.logintaptophone.dto.RandomData;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CryptoCache {

    private HashMap<String, RandomData> cryptoCacheHashMap = new HashMap<>();

    public void addCache(String jwtToken, RandomData randomData) {
        cryptoCacheHashMap.put(jwtToken, randomData);
    }

    public RandomData getCacheValue(String jwtToken) {
        return cryptoCacheHashMap.get(jwtToken);
    }

    @Override
    public String toString() {
        return "CryptoCache{" + '}';
    }
}
