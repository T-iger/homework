package com.lihu.homework.webcontroller;

import com.lihu.homework.po.Answer;
import com.lihu.homework.po.Homework;
import com.lihu.homework.po.PublishHomework;
import com.lihu.homework.po.User;
import com.lihu.homework.service.AnswerService;
import com.lihu.homework.service.HomeworkService;
import com.lihu.homework.service.PublicHomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Li
 **/
@Controller
@RequestMapping("/login/student")
public class StudentController {
    @Autowired
    private PublicHomeworkService publicHomeworkService;
    @Autowired
    private AnswerService answerService;
    @Autowired
    private HomeworkService homeworkService;
    @GetMapping("/")
    public  String student( HttpSession session,Model model, @PageableDefault(size = 6,sort = {"updatetime"},direction = Sort.Direction.DESC)
            Pageable pageable){
        User user=(User)session.getAttribute("user");
        model.addAttribute("user",user.getUsername());
//        model.addAttribute("page",publicHomeworkService.showListPublic(pageable,user.getId()));
        model.addAttribute("page", publicHomeworkService.showListPublic(pageable, user.getId()));
        model.addAttribute("undo",answerService.findUndoHomework(user));
        return "/index";
    }

    @PostMapping("/showPublish")
    public String showPublish(@PageableDefault(size = 5, sort = {"updatetime"}, direction = Sort.Direction.DESC)
                                      Pageable pageable, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("page", publicHomeworkService.showListPublic(pageable, user.getId()));
        return "/student/index :: publishList";
    }

    @GetMapping("/doing/{id}")
    public String doing(@PathVariable Long id,
                        Model model,HttpSession httpSession) {
        User user=(User)httpSession.getAttribute("user");
        model.addAttribute("user" ,user.getUsername());
        model.addAttribute("testPagers", publicHomeworkService.listHomework(publicHomeworkService.getPublishHomework(id).get().getNote()));
        return "doing";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam("publishid") String publishid,
                         @RequestParam(value = "radio",required = false) String radio,
                         @RequestParam(value = "tk-id",required = false) String tk_id,
                         @RequestParam(value = "tk-answer",required = false)String tk_answer,
                         @RequestParam(value = "jd-id",required = false)String jd_id,
                         @RequestParam(value = "jd-answer",required = false)String jd_answer,
                         HttpSession session) {
        User user=(User)session.getAttribute("user");
        List<Answer> answerList=new ArrayList<>();
        PublishHomework publishHomework=new PublishHomework();
        publishHomework.setId(Long.valueOf(publishid));
        if (radio!=null){
            String[] radios = radio.split(",");
            for (int i = 0; i < radios.length; i++) {
                Answer answer=new Answer();
                Homework homework=new Homework();
                String[] split = radios[i].split(":");
                homework.setId(Long.valueOf(split[0]));
                answer.setHomework(homework);
                answer.setStudentradio(split[1]);
                answer.setUser(user);
                answer.setPublishHomework(publishHomework);
                answerList.add(answer);
            }
        }

        if (tk_id!=null){
            String[] tkid = tk_id.split(",");
            String[] tkanswer = tk_answer.split(",");
            for (int i = 0; i < tkid.length; i++) {
                Answer answer=new Answer();
                Homework homework=new Homework();
                homework.setId(Long.valueOf(tkid[i]));
                answer.setHomework(homework);
                answer.setStudenttk(tkanswer[i]);
                answer.setUser(user);
                answer.setPublishHomework(publishHomework);
                answerList.add(answer);
            }
        }
       if(jd_id!=null){
           String[] jdid = jd_id.split(",");
           String[] jdanswer = jd_answer.split(",");
           for (int i = 0; i < jdid.length; i++) {
               Answer answer=new Answer();
               Homework homework=new Homework();
               homework.setId(Long.valueOf(jdid[i]));
               answer.setHomework(homework);
               answer.setStudentanswer(jdanswer[i]);
               answer.setUser(user);
               answer.setPublishHomework(publishHomework);
               answerList.add(answer);
           }
       }
        answerService.save(answerList);
        return "redirect:/login/student/";
    }

    @GetMapping("/show/{id}")
    public String show(@PathVariable Long id,Model model,HttpSession httpSession){
        PublishHomework publishHomework=new PublishHomework();
        User user=(User)httpSession.getAttribute("user");
        publishHomework.setId(id);
        model.addAttribute("testPagers", homeworkService.findPublish(publishHomework));
        model.addAttribute("answers", answerService.findAnswer(user, publishHomework));
        model.addAttribute("Comment",answerService.getHomeworkStatus(user,publishHomework));
        return "show";
    }
}
