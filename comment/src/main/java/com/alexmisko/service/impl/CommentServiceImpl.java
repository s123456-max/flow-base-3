package com.alexmisko.service.impl;

import com.alexmisko.dao.CommentMapper;
import com.alexmisko.pojo.Comment;
import com.alexmisko.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
