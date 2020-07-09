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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;

/**
 * @author adyfang
 * @date 2020年5月4日
 */
public class Utils {
    @SuppressWarnings("rawtypes")
    public static IPage convertPage(Pageable pageable) {
        Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        for (Iterator<Sort.Order> it = pageable.getSort().iterator(); it.hasNext(); ) {
            Sort.Order order = it.next();
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(order.isAscending());
            orderItem.setColumn(StringUtils.camelToUnderline(order.getProperty()));
            page.addOrder(orderItem);
        }
        return page;
    }
}
