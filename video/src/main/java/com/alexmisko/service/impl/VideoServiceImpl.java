package com.alexmisko.service.impl;

import com.alexmisko.dao.VideoMapper;
import com.alexmisko.pojo.Video;
import com.alexmisko.service.VideoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService{
}
