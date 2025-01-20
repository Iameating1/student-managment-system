package com.studentmanagement.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.studentmanagement.entity.Assignment;
import com.studentmanagement.entity.Course;
import com.studentmanagement.entity.GradeDetails;
import com.studentmanagement.entity.Student;
import com.studentmanagement.entity.AssignmentDetails;
import com.studentmanagement.entity.StudentCourseDetails;
import com.studentmanagement.service.CourseService;
import com.studentmanagement.service.AssignmentDetailsService;
import com.studentmanagement.service.StudentCourseDetailsService;
import com.studentmanagement.service.StudentService;


@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private CourseService courseService;
	
	
	@Autowired
	private StudentCourseDetailsService studentCourseDetailsService;

	
	@GetMapping("/{studentId}/courses")
	public String showStudentPanel(@PathVariable("studentId") int studentId, Model theModel) {
		Student student = studentService.findByStudentId(studentId); //accessing student logged in
		List<Course> courses = student.getCourses();
		
		theModel.addAttribute("student", student);
		theModel.addAttribute("courses", courses);
		return "student/student-courses";
	}
	
	@GetMapping("/{studentId}/courses/{courseId}")
	public String showStudentCourse(@PathVariable("studentId") int studentId, @PathVariable("courseId") int courseId, Model theModel) {
		Student student = studentService.findByStudentId(studentId);
		List<Course> courses = student.getCourses();
		Course course = courseService.findCourseById(courseId);
		StudentCourseDetails studentCourseDetails = studentCourseDetailsService.findByStudentAndCourseId(studentId, courseId);
		List<Assignment> assignments = studentCourseDetails.getAssignments();

		
		
		GradeDetails gradeDetails = studentCourseDetails.getGradeDetails();

		theModel.addAttribute("course", course);
		theModel.addAttribute("courses", courses);
		theModel.addAttribute("student", student);
		theModel.addAttribute("gradeDetails", gradeDetails);
		
		return "student/student-course-detail";
	}


}
