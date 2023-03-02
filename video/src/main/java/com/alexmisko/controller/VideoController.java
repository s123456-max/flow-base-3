package com.alexmisko.controller;

import com.alexmisko.feign.UserInfoFeign;
import com.alexmisko.pojo.Video;
import com.alexmisko.service.VideoService;
import com.alexmisko.vo.Result;
import com.alexmisko.vo.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VideoController {

    @Autowired
    VideoService videoService;

    @Autowired
    UserInfoFeign userInfoFeign;

    /**
     * 查询一条视频
     */
    @GetMapping("video/{id}")
    public Result<Video> oneVideo(@PathVariable Long id){
        Video video = videoService.getById(id);
        Result<UserInfo> result = userInfoFeign.getUserInfo(id);
        video.setUserInfo(result.getData());
        return Result.success(video);
    }

    /**
     * 分页查询视频
     */
    @GetMapping("video")
    public Result<IPage<Video>> pageVideo(Long page, Long num){
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        IPage<Video> iPage = new Page<>(page, num);
        IPage<Video> iPageVideo = videoService.page(iPage, queryWrapper);
        List<Video> videoList =  iPageVideo.getRecords();
        for(Video video : videoList){
            // 先遍历取id，再请求userInfo
            Result<UserInfo> result = userInfoFeign.getUserInfo(video.getUserId());
            video.setUserInfo(result.getData());
        }
        // 最后重新设置值
        iPageVideo.setRecords(videoList);
        return Result.success(iPageVideo);
    }

    /**
     * 上传视频
     */
    @PostMapping("video")
    public Result<Boolean> uploadVideo(@RequestBody Video video){
        return Result.success(videoService.save(video));
    }
}
