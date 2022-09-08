package com.douyiyuan.first.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.Files;
import com.douyiyuan.first.entity.User;
import com.douyiyuan.first.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.download.path}")
    private String downloadPath;

    @Autowired
    private IFileService fileService;

    /**
     * 获取MultipartFile类型变量的md5值
     * @param file
     * @return
     * @throws Exception
     */
    public String getMultipartFileMD5(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(fileBytes);
        return new BigInteger(1,digest).toString(16);
    }

    /**
     * 上传文件，同种文件只会存一张，但数据还是上传多少次就有多少条数据
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("upload")
    public String upload(@RequestBody MultipartFile file) throws Exception {
        if(file == null){
            throw new ServiceException(StatusCode.CODE_300,"请选择文件再上传");
        }

        String originalFilename = file.getOriginalFilename();
        //创建存储文件的目录
        File filePath = new File(uploadPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        //为了防止用户上传的文件名一样而失去辨识度，当然这个问题是用userid来做的
        //防止文件名有非法字符，并确保所有文件的文件名在磁盘独一份，重新给文件起名
        String fileType = FileUtil.extName(originalFilename);  //获取文件扩展名
        String simpleUUID = IdUtil.simpleUUID(); //文件的唯一标识
        String newFileName = simpleUUID+ StrUtil.DOT + fileType; //组成新的文件名
        String newFilePath = uploadPath + newFileName ; //组成存储在磁盘的文件目录位置

        //获取文件的md5，根据md5去查file表的所有数据，找到了就说明已经有相同的文件了，不必再上传
        //没找到的时候再把文件上传到磁盘
        String md5 = getMultipartFileMD5(file);

        //从数据库查获取相同md5的url，赋值给新插入的数据，从而保证相同的md5，url也相同，数据的其他属性可以不同
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        Map<String, Object> map = fileService.getMap(queryWrapper);
        String url;
        if(map!=null){
            url = (String)map.get("url");
        }else {
            url = downloadPath + newFileName; //组成文件下载路径

            //复制上传文件到磁盘
            File diskFile = new File(newFilePath);
            try {
                file.transferTo(diskFile);
            } catch (IOException e) {
                throw new ServiceException(StatusCode.CODE_500,"系统错误，文件上传失败");
            }
        }

        //将文件信息存进数据库
        Files files = new Files();
        files.setName(originalFilename);
        files.setType(fileType);
        files.setSize(file.getSize()/1024);
        files.setMd5(md5);
        files.setUrl(url);
        try {
            fileService.save(files);
        }catch (Exception e){
            throw new ServiceException(StatusCode.CODE_500,"系统错误，文件无法存进数据库");
        }

        //返回文件存进磁盘后，对磁盘文件的访问路径
        return url;
    }


    /**
     * 访问路径直接下载文件
     * @param newFileName
     * @param response
     */
    @GetMapping("/{newFileName}")
    public void fileDownload(@PathVariable String newFileName, HttpServletResponse response){
        String fileSuffix = FileUtil.extName(newFileName);
        File file = new File(uploadPath+newFileName);
        try {
            // 如果newFileName的后缀名是jpg,png，就把ContentType设置成image/jpeg,image/png，
            // 否则就是application/octet-stream
            if("png".equals(fileSuffix)){
                response.setContentType("image/png");
            }else if("jpg".equals(fileSuffix)){
                response.setContentType("image/jpeg");
            }else {
                response.setHeader("Content-Disposition","attachment;filename=" +
                        URLEncoder.encode(newFileName,"utf-8"));
                response.setContentType("application/octet-stream");
            }
            ServletOutputStream os = response.getOutputStream();
            //FileUtil.readBytes(file):把文件变成字节流
            os.write(FileUtil.readBytes(file));
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法下载文件");
        }
    }


    /**********************************常规操作******************************************/

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(name!=""){
            queryWrapper.like("name",name);
        }
        //查询所有，但忽略isDelete=true的数据
        queryWrapper.eq("is_delete",false);
        //倒叙排列
        queryWrapper.orderByDesc("id");
        Page<Files> objectPage = new Page<>(pageNum, pageSize);
        Page<Files> page = fileService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 根据id逻辑删除数据库记录，修改数据库文件的is_delete可以还原文件
     * @param files
     * @return
     */
    /*@DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(fileService.removeById(id));
    }*/
    @PostMapping("delete")
//    @CacheEvict(cacheNames = "files",allEntries = true)
//    @CachePut(key = "'getAllFiles'",cacheNames = "files")
    public Result delete(@RequestBody Files files) {
        fileService.updateById(files);
        List<Files> list = fileService.list();
        return Result.success(list);
    }

    /**
     * 根据ids逻辑批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param filesList
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Files> filesList){
        return Result.success(fileService.updateBatchById(filesList));
    }
}
