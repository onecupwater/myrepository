package com.douyiyuan.first.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.douyiyuan.first.common.ReleaseAnnotation;
import com.douyiyuan.first.common.Result;
import com.douyiyuan.first.common.StatusCode;
import com.douyiyuan.first.common.exception.ServiceException;
import com.douyiyuan.first.entity.Files;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.douyiyuan.first.service.IVideoService;
import com.douyiyuan.first.entity.Video;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 视频信息表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2022-07-27
 */
@RestController
@RequestMapping("/video")
public class VideoController {

    @Value("${video.upload.path}")
    private String uploadPath;

    @Value("${video.download.path}")
    private String downloadPath;

    @Value("${video.cover.upload.path}")
    private String coverUploadPath;

    @Value("${video.cover.download.path}")
    private String coverDownloadPath;

    @Resource
    private IVideoService videoService;

    /**
     * 根据id查询
     * @return
     */
    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable("id") Integer id) {
        Video video = videoService.getById(id);
        return Result.success(video);
    }

    /**
     * 查询所有
     * @return
     */
    @ReleaseAnnotation
    @GetMapping
    public Result findAll() {
        return Result.success(videoService.list());
    }

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ReleaseAnnotation
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        //分页查询如果没有条件，就把new QueryWrapper<>()删了，有条件就在queryWrapper变量加条件
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        //queryWrapper加条件
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name",name);
        }
        queryWrapper.orderByDesc("id");
        Page<Video> objectPage = new Page<>(pageNum, pageSize);
        Page<Video> page = videoService.page(objectPage, queryWrapper);
        return Result.success(page);
    }

    /**
     * 新增与修改
     * @param video
     * @return
     */
    @PostMapping("save")
    public Result save(@RequestBody Video video) {
        video.setUpdateTime(LocalDateTime.now());
        return Result.success(videoService.saveOrUpdate(video));
        }

    /**
     * 根据id删除
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(videoService.removeById(id));
        }

    /**
     * 批量删除，因为前端的axios传 "[1,2,3]" 这样的参数只能用post方式来传，不能用delete方式来传
     * @param ids
     * @return
     */
    @PostMapping("removeBatchByIds")
    public Result removeBatchByIds(@RequestBody List<Integer> ids){
        return Result.success(videoService.removeBatchByIds(ids));
        }

//-------------------------------------------------------------------------------------

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
     * 上传视频，同种视频只会存一次
     * @param file
     * @return
     * @throws Exception
     */
    @ReleaseAnnotation  //取消拦截器拦截
    @PostMapping("upload")
    public Result upload(@RequestBody MultipartFile file) throws Exception {
        if(file == null){
            throw new ServiceException(StatusCode.CODE_300,"请选择视频再上传");
        }

        String originalFilename = file.getOriginalFilename();

        String fileType = FileUtil.extName(originalFilename);  //获取文件扩展名

        //创建存储文件的目录
        File filePath = new File(uploadPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        //为了防止用户上传的文件名一样而失去辨识度，当然这个问题是用userid来做的
        //防止文件名有非法字符，并确保所有文件的文件名在磁盘独一份，重新给文件起名
        String simpleUUID = IdUtil.simpleUUID(); //文件的唯一标识
        String newFileName = simpleUUID+ StrUtil.DOT + fileType; //组成新的文件名
        String newFilePath = uploadPath + newFileName ; //组成存储在磁盘的文件目录位置

        //获取文件的md5，根据md5去查file表的所有数据，找到了就说明已经有相同的文件了，不必再上传
        //没找到的时候再把文件上传到磁盘
        String md5 = getMultipartFileMD5(file);

        //从数据库查获取相同md5的url，赋值给新插入的数据，从而保证相同的md5，url也相同，数据的其他属性可以不同
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5",md5);
        Map<String, Object> map = videoService.getMap(queryWrapper);
        String url;
        String uuid;
        if(map!=null){
            url = (String)map.get("url");
            uuid = (String)map.get("uuid");
        }else {
            url = downloadPath + newFileName; //组成文件下载路径
            uuid = simpleUUID;
            //复制上传文件到磁盘
            File diskFile = new File(newFilePath);
            try {
                file.transferTo(diskFile);
            } catch (IOException e) {
                throw new ServiceException(StatusCode.CODE_500,"系统错误，视频上传失败");
            }
        }

        //将视频信息存进数据库
        Video video = new Video();
        video.setName(originalFilename);
        video.setType(fileType);
        video.setSize(file.getSize()/1024);
        video.setUrl(url);
        video.setMd5(md5);
        video.setUuid(uuid);
        video.setUpdateTime(LocalDateTime.now());
        try {
            videoService.save(video);
        }catch (Exception e){
            throw new ServiceException(StatusCode.CODE_500,"系统错误，视频无法存进数据库");
        }

        //返回文件存进磁盘后，对磁盘文件的访问路径
        return Result.success(url);
    }

    /**
     * 上传封面，同种封面只会存一次
     * @param file
     * @return
     * @throws Exception
     */
    @ReleaseAnnotation  //取消拦截器拦截
    @PostMapping("uploadCover/{id}")
    public Result uploadCover(@RequestBody MultipartFile file,
                              @PathVariable("id") Integer id) throws Exception {

        if(file == null){
            throw new ServiceException(StatusCode.CODE_300,"请选择封面再上传");
        }
        String originalFilename = file.getOriginalFilename();
        String fileType = FileUtil.extName(originalFilename);  //获取文件扩展名
        //创建存储文件的目录
        File filePath = new File(coverUploadPath);
        if(!filePath.exists()){
            filePath.mkdirs();
        }
        //为了防止用户上传的文件名一样而失去辨识度，当然这个问题是用userid来做的
        //防止文件名有非法字符，并确保所有文件的文件名在磁盘独一份，重新给文件起名
        String simpleUUID = IdUtil.simpleUUID(); //文件的唯一标识
        String newFileName = simpleUUID+ StrUtil.DOT + fileType; //组成新的文件名
        String newFilePath = coverUploadPath + newFileName ; //组成存储在磁盘的文件目录位置

        //获取文件的md5，根据md5去查file表的所有数据，找到了就说明已经有相同的文件了，不必再上传
        //没找到的时候再把文件上传到磁盘
        String coverMd5 = getMultipartFileMD5(file);

        //从数据库查获取相同md5的url，赋值给新插入的数据，从而保证相同的md5，url也相同，数据的其他属性可以不同
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cover_md5",coverMd5);
        Map<String, Object> map = videoService.getMap(queryWrapper);
        String cover;
        if(map!=null){
            cover = (String)map.get("cover");
        }else {
            cover = coverDownloadPath + newFileName; //组成文件下载路径

            //复制上传文件到磁盘
            File diskFile = new File(newFilePath);
            try {
                file.transferTo(diskFile);
            } catch (IOException e) {
                throw new ServiceException(StatusCode.CODE_500,"系统错误，封面上传失败");
            }
        }

        //将封面信息，coverMd5存进对应id的video表
        Video video = videoService.getById(id);
        if(ObjectUtil.isEmpty(video)){
            throw new ServiceException(StatusCode.CODE_300,"无法找到封面对应的video信息");
        }
        video.setCover(cover);
        video.setCoverMd5(coverMd5);
        video.setUpdateTime(LocalDateTime.now());
        try {
            videoService.updateById(video);
        }catch (Exception e){
            throw new ServiceException(StatusCode.CODE_500,"系统错误，封面无法存进数据库");
        }

        //返回文件存进磁盘后，对磁盘文件的访问路径
        return Result.success(cover);
    }

    /**
     * 访问路径直接下载封面
     * @param newFileName
     * @param response
     */
    @ReleaseAnnotation //放行
    @GetMapping("cover/{newFileName}")
    public void coverDownload(@PathVariable String newFileName, HttpServletResponse response){
        File file = new File(coverUploadPath+newFileName);
        try {
            response.setHeader("Content-Disposition","attachment;filename=" +
                    URLEncoder.encode(newFileName,"utf-8"));
            response.setContentType("application/octet-stream");
            ServletOutputStream os = response.getOutputStream();
            //FileUtil.readBytes(file):把文件变成字节流
            os.write(FileUtil.readBytes(file));
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法下载封面");
        }
    }

    /**
     * 访问路径直接下载视频
     * @param newFileName
     * @param response
     */
    @ReleaseAnnotation //放行
    @GetMapping("entity/{newFileName}")
    public void videoDownload(@PathVariable String newFileName,
                              HttpServletRequest request,
                              HttpServletResponse response){

        File file = new File(uploadPath+newFileName);
        try {
            response.setHeader("Content-Disposition","attachment;filename=" +
                    URLEncoder.encode(newFileName,"utf-8"));
            response.setHeader("Content-Type", "application/octet-stream");

            //如果前端使用video-player来访问这个接口，用这个接口来播放视频，那么response还需要设置更多的属性,
            // 否则前端视频播放器无法拖拽进度条
            String uuid = newFileName.substring(0,newFileName.lastIndexOf("."));
            QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("uuid",uuid);
            Map<String, Object> map = videoService.getMap(queryWrapper);
            Long size =((Long) map.get("size"))*1024;  //视频尺寸
            response.setContentLength(size.intValue());

            String rangeString = request.getHeader("Range");//如果是video标签发起的请求就不会为null
            long range = Long.valueOf(rangeString.substring(rangeString.indexOf("=") + 1,
                   rangeString.indexOf("-")));
            response.setHeader("Content-Range", String.valueOf(range + (size.intValue()-1)));
            response.setHeader("Accept-Ranges", "bytes");
            ServletOutputStream os = response.getOutputStream();
            //FileUtil.readBytes(file):把文件变成字节流
            os.write(FileUtil.readBytes(file));
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new ServiceException(StatusCode.CODE_500,"系统错误，无法下载视频");
        }
    }

}

