package com.douyiyuan.first.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "role_menu")
@Data
public class RoleMenu {
    private Integer roleId;
    private Integer menuId;
}
