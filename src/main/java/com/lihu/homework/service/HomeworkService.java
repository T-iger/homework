package com.lihu.homework.service;

import com.lihu.homework.po.Homework;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * @author Li
 **/
public interface HomeworkService {
    Homework addHomework(Homework homework);

    /*Page<Homework> listHomework(Pageable pageable);*/
}
