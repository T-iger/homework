package com.lihu.homework.service;

import com.lihu.homework.dao.AnswerRepository;
import com.lihu.homework.dao.HomeworkRepository;
import com.lihu.homework.dao.HomeworkStatusRepository;
import com.lihu.homework.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Li
 **/
@Service
public class AnswerServiceImpl implements AnswerService {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private HomeworkStatusRepository homeworkStatusRepository;
    @Autowired
    private HomeworkRepository homeworkRepository;

    @Override
    public List<Answer> save(List<Answer> answerList) {
        Optional<Homework> byId = homeworkRepository.findById(answerList.get(0).getHomework().getId());
        HomeworkStatus stu1 = homeworkStatusRepository.findByUserStatusAndPublishHomework(answerList.get(0).getUser(), byId.get().getPublishHomework());
        if (stu1 != null) {//查询该作业对应状态
            stu1.setStatus(true);
            homeworkStatusRepository.save(stu1);
        }
        return answerRepository.saveAll(answerList);
    }

    @Override
    public List<Answer> findAnswer(User user, PublishHomework publishHomework) {
        List<Answer> answerList = answerRepository.findByUserAndPublishHomework(user, publishHomework);
        for (Answer answer : answerList) {
            Optional<Homework> homework = homeworkRepository.findById(answer.getHomework().getId());
            if (answer.getStudentradio() != null && answer.getStudentradio().equals(homework.get().getRadio())) {
                answer.setScore(homework.get().getScore());
                answerRepository.save(answer);
            } else if(answer.getStudentradio() != null){
                answer.setScore(0);
                answerRepository.save(answer);
            }
            if (answer.getStudenttk() != null &&answer.getStudenttk().equals(homework.get().getTk())) {
                answer.setScore(homework.get().getScore());
                answerRepository.save(answer);
            }else if (answer.getStudenttk() != null){
                answer.setScore(0);
                answerRepository.save(answer);
            }
        }
        return answerRepository.findByUserAndPublishHomework(user, publishHomework);
    }

    @Override
    public Answer setScore(int score, Long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        answer.get().setScore(score);
        return answerRepository.save(answer.get());
    }

    @Override
    public List<Answer> getScore(Long id, List<User> users) {
        List<Answer> scoreList=new ArrayList<>();
        for (User user : users) {
            PublishHomework publishHomework=new PublishHomework();
            publishHomework.setId(id);
            List<Answer> answerList = answerRepository.findByUserAndPublishHomework(user, publishHomework);
            Integer sum=0;
            Answer an=new Answer();
            for (Answer answer : answerList) {
                    sum += answer.getScore();
            }
            //推荐不会的知识点
            if(sum!=0){
                for (Answer answer : answerList) {
                    if(answer.getScore()==0){//读取分值为0的题目
                        answer.getHomework().getKnowledge().getObject();
                    }
                }
            }

            an.setScore(sum);
            an.setUser(user);
            scoreList.add(an);
        }
        return scoreList;
    }
}