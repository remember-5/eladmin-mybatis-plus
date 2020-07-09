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
package com.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.admin.exception.BadRequestException;
import com.admin.mapper.IColumnInfoMapper;
import com.admin.model.ColumnInfoModel;
import com.admin.model.GenConfigModel;
import com.admin.service.IGeneratorService;
import com.admin.utils.FileUtil;
import com.admin.utils.GenUtil;
import com.admin.utils.PageUtil;
import com.admin.vo.TableInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author adyfang
 * @since 2020-05-30
 */
@RequiredArgsConstructor
@Service
public class AdminGeneratorServiceImpl extends ServiceImpl<IColumnInfoMapper, ColumnInfoModel>
        implements IGeneratorService {
    @Override
    public Object getTables() {
        return this.baseMapper.getTables();
    }

    @Override
    public Object getTables(String name, int[] startEnd) {
        List<Map<String, Object>> result = this.baseMapper.getByTableName(
                StringUtils.isNotBlank(name) ? ("%" + name + "%") : "%%", startEnd[0], startEnd[1] - startEnd[0]);
        List<TableInfo> tableInfos = new ArrayList<>();
        for (Map<String, Object> obj : result) {
            tableInfos.add(new TableInfo(obj.get("table_name"), obj.get("create_time"), obj.get("engine"),
                    obj.get("table_collation"),
                    StringUtils.isNotEmpty((String) obj.get("table_comment")) ? obj.get("table_comment") : "-"));
        }
        return PageUtil.toPage(tableInfos, this.baseMapper.countTables());
    }

    @Override
    public List<ColumnInfoModel> getColumns(String tableName) {
        QueryWrapper<ColumnInfoModel> query = new QueryWrapper<ColumnInfoModel>();
        query.lambda().eq(ColumnInfoModel::getTableName, tableName).orderByAsc(ColumnInfoModel::getId);
        List<ColumnInfoModel> columnInfos = this.list(query);
        if (CollectionUtil.isNotEmpty(columnInfos)) {
            return columnInfos;
        } else {
            columnInfos = query(tableName);
            this.saveBatch(columnInfos);
            return columnInfos;
        }
    }

    @Override
    public List<ColumnInfoModel> query(String tableName) {
        List<Map<String, Object>> result = this.baseMapper.getByTableNameOrderByPosition(tableName);
        List<ColumnInfoModel> columnInfos = new ArrayList<>();
        for (Map<String, Object> obj : result) {
            columnInfos.add(new ColumnInfoModel(tableName, obj.get("column_name").toString(),
                    "NO".equals(obj.get("is_nullable")), obj.get("data_type").toString(),
                    ObjectUtil.isNotNull(obj.get("column_comment")) ? obj.get("column_comment").toString() : null,
                    ObjectUtil.isNotNull(obj.get("column_key")) ? obj.get("column_key").toString() : null,
                    ObjectUtil.isNotNull(obj.get("extra")) ? obj.get("extra").toString() : null));
        }
        return columnInfos;
    }

    @Override
    public void sync(List<ColumnInfoModel> columnInfos, List<ColumnInfoModel> columnInfoList) {
        // 第一种情况，数据库类字段改变或者新增字段
        for (ColumnInfoModel columnInfo : columnInfoList) {
            // 根据字段名称查找
            List<ColumnInfoModel> columns = columnInfos.stream()
                    .filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果能找到，就修改部分可能被字段
            if (CollectionUtil.isNotEmpty(columns)) {
                ColumnInfoModel column = columns.get(0);
                column.setColumnType(columnInfo.getColumnType());
                column.setExtra(columnInfo.getExtra());
                column.setKeyType(columnInfo.getKeyType());
                if (StringUtils.isBlank(column.getRemark())) {
                    column.setRemark(columnInfo.getRemark());
                }
                this.save(column);
            } else {
                // 如果找不到，则保存新字段信息
                this.save(columnInfo);
            }
        }
        // 第二种情况，数据库字段删除了
        for (ColumnInfoModel columnInfo : columnInfos) {
            // 根据字段名称查找
            List<ColumnInfoModel> columns = columnInfoList.stream()
                    .filter(c -> c.getColumnName().equals(columnInfo.getColumnName())).collect(Collectors.toList());
            // 如果找不到，就代表字段被删除了，则需要删除该字段
            if (CollectionUtil.isEmpty(columns)) {
                QueryWrapper<ColumnInfoModel> query = new QueryWrapper<ColumnInfoModel>(columnInfo);
                this.remove(query);
            }
        }
    }

    @Override
    public void save(List<ColumnInfoModel> columnInfos) {
        this.saveBatch(columnInfos);
    }

    @Override
    public void generator(GenConfigModel genConfig, List<ColumnInfoModel> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            GenUtil.generatorCode(columns, genConfig);
        } catch (IOException e) {
            log.error("connection close error：" + e.getMessage());
            throw new BadRequestException("生成失败，请手动处理已生成的文件");
        }
    }

    @Override
    public ResponseEntity<Object> preview(GenConfigModel genConfig, List<ColumnInfoModel> columns) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        List<Map<String, Object>> genList = GenUtil.preview(columns, genConfig);
        return new ResponseEntity<>(genList, HttpStatus.OK);
    }

    @Override
    public void download(GenConfigModel genConfig, List<ColumnInfoModel> columns, HttpServletRequest request,
                         HttpServletResponse response) {
        if (genConfig.getId() == null) {
            throw new BadRequestException("请先配置生成器");
        }
        try {
            File file = new File(GenUtil.download(columns, genConfig));
            String zipPath = file.getPath() + ".zip";
            ZipUtil.zip(file.getPath(), zipPath);
            FileUtil.downloadFile(request, response, new File(zipPath), true);
        } catch (IOException e) {
            throw new BadRequestException("打包失败");
        }
    }
}
