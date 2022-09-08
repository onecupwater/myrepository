package com.douyiyuan.first.controller.dto;

import com.douyiyuan.first.entity.Menu;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class UserDTO {

    private Integer id;

    private String name;

    private String nickname;

    private String phone;

    private String password;

    private String avatar;

    private String sign;

    private String email;

    private String code;  //验证码

    private String address;

    private String token;

    private String role;

    private List<Menu> menus;

    private Integer majorId;

    private Integer stuClassId;
}
