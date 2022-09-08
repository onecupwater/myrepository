package com.douyiyuan.first.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.douyiyuan.first.entity.Files;
import com.douyiyuan.first.mapper.FileMapper;
import com.douyiyuan.first.service.IFileService;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {
}
