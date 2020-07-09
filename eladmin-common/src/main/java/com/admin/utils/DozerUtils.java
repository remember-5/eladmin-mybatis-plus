/*
 *  Copyright 2019-2020 Fang Jin Biao
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.admin.utils;

import org.dozer.Mapper;

import java.util.*;

/**
 * @author adyfang
 * @date 2020年4月29日
 */
public class DozerUtils {
    public static <T, S> List<T> mapList(final Mapper mapper, Collection<S> sourceList, Class<T> targetObjectClass) {
        List<T> targetList = new ArrayList<T>(sourceList.size());
        for (S s : sourceList) {
            targetList.add(mapper.map(s, targetObjectClass));
        }
        return targetList;
    }

    public static <T, S> Set<T> mapSet(final Mapper mapper, Collection<S> sourceList, Class<T> targetObjectClass) {
        Set<T> targetSet = new HashSet<T>(sourceList.size());
        for (S s : sourceList) {
            targetSet.add(mapper.map(s, targetObjectClass));
        }
        return targetSet;
    }
}
